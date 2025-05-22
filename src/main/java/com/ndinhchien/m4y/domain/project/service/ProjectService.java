package com.ndinhchien.m4y.domain.project.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.ndinhchien.m4y.domain.address.entity.Language;
import com.ndinhchien.m4y.domain.address.service.LanguageService;
import com.ndinhchien.m4y.domain.project.dto.ProjectRequestDto.CreateProjectDto;
import com.ndinhchien.m4y.domain.project.dto.ProjectRequestDto.UpdateProjectDto;
import com.ndinhchien.m4y.domain.project.dto.ProjectResponseDto.IBasicProject;
import com.ndinhchien.m4y.domain.project.dto.ProjectResponseDto.IChannel;
import com.ndinhchien.m4y.domain.project.entity.Channel;
import com.ndinhchien.m4y.domain.project.entity.Project;
import com.ndinhchien.m4y.domain.project.entity.ViewHistory;
import com.ndinhchien.m4y.domain.project.repository.ChannelRepository;
import com.ndinhchien.m4y.domain.project.repository.ViewHistoryRepository;
import com.ndinhchien.m4y.domain.project.type.ProjectSortBy;
import com.ndinhchien.m4y.domain.project.repository.ProjectRepository;
import com.ndinhchien.m4y.domain.user.entity.User;
import com.ndinhchien.m4y.global.exception.BusinessException;
import com.ndinhchien.m4y.global.exception.ErrorMessage;

import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ProjectService {

    private final static int PAGE_SIZE = 12;

    private final ProjectRepository projectRepository;
    private final ChannelRepository channelRepository;
    private final ViewHistoryRepository viewHistoryRepository;
    private final LanguageService languageService;

    @Transactional(readOnly = true)
    public Object getProjectById(@Nullable User user, Long projectId) {
        if (user == null || !isProjectAdmin(user, projectId)) {
            return projectRepository.findOneById(projectId).orElse(null);
        }
        return projectRepository.findProjectById(projectId).orElse(null);
    }

    @Transactional(readOnly = true)
    public List<IBasicProject> getProjectsByChannel(String channelUrl) {
        Channel channel = validateChannel(channelUrl);
        return projectRepository.findAllByChannel(channel);
    }

    private boolean isProjectAdmin(User user, Long projectId) {
        return projectRepository.existsByIdAndAdminId(projectId, user.getId());
    }

    @Transactional
    public Project createProject(User user, CreateProjectDto requestDto) {
        String channelName = requestDto.getChannelName();
        String channelUrl = requestDto.getChannelUrl();

        Channel channel = channelRepository.findByUrl(channelUrl).orElse(null);
        if (channel == null) {
            channel = channelRepository.save(new Channel(channelUrl, channelName));
        }

        String srcUrl = requestDto.getSrcUrl();
        String title = requestDto.getTitle();
        String description = requestDto.getDescription();
        Integer duration = requestDto.getDuration();
        String srcLangName = requestDto.getSrcLangName();
        String desLangName = requestDto.getDesLangName();

        Language srcLang = languageService.validateLanguage(srcLangName);
        Language desLang = languageService.validateLanguage(desLangName);

        List<Project> projects = projectRepository.findBySrcUrl(srcUrl);
        if (projects.stream().anyMatch((p) -> !p.getSrcLangCode().equals(srcLang.getCode()))) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Source language is invalid");
        }
        if (projectRepository.existsBySrcUrlAndDesLangCode(srcUrl, desLang.getCode())) {
            throw new BusinessException(HttpStatus.BAD_REQUEST,
                    String.format("This project has %s subtitles already", desLang.getName()));
        }

        Project project = new Project(user, channel, srcUrl, title, description, duration, srcLang, desLang);
        return projectRepository.save(project);
    }

    @Transactional
    public Project updateProject(User user, Long projectId, UpdateProjectDto requestDto) {

        String srcLangName = requestDto.getSrcLangName();
        String desLangName = requestDto.getDesLangName();
        Language srcLang = null;
        if (StringUtils.hasText(srcLangName)) {
            srcLang = languageService.validateLanguage(srcLangName);
        }
        Language desLang = null;
        if (StringUtils.hasText(desLangName)) {
            desLang = languageService.validateLanguage(desLangName);
        }

        Project project = validateProject(projectId);
        if (!project.isAdmin(user)) {
            throw new BusinessException(HttpStatus.FORBIDDEN, ErrorMessage.NOT_PROJECT_ADMIN);
        }

        project.update(requestDto);

        String srcUrl = project.getSrcUrl();

        if (srcLang != null && !project.getSrcLangCode().equals(srcLang.getCode())) {
            if (projectRepository.countBySrcUrl(srcUrl) <= 1l) {
                project.setSrcLangCode(srcLang);
            }
        }

        if (desLang != null && !project.getDesLangCode().equals(desLang.getCode())) {
            if (!projectRepository.existsBySrcUrlAndDesLangCode(srcUrl, desLang.getCode())) {
                project.setDesLangCode(desLang);
            }
        }
        return projectRepository.save(project);
    }

    @Transactional
    public List<Project> hardDeleteProjects(User user, List<Long> ids) {
        List<Project> deleted = new ArrayList<>();
        List<Project> projects = projectRepository.findAllById(ids);
        for (Project project : projects) {
            if (user.isSysAdmin() || project.isAdmin(user)) {
                deleted.add(project);
            }
        }
        projectRepository.deleteAll(deleted);
        return deleted;
    }

    @Transactional
    public Project updateViewCount(User user, Long projectId) {
        Project project = validateProject(projectId);
        ViewHistory view = viewHistoryRepository.findByProjectAndUser(project, user)
                .orElse(new ViewHistory(user, project));
        int count = view.updateViewCount(project.getDuration());
        viewHistoryRepository.save(view);

        project.updateViewCount(count);
        return projectRepository.save(project);

    }

    @Transactional(readOnly = true)
    public List<IChannel> searchChannels(String name) {
        return channelRepository.findByNameContaining(name);
    }

    @Transactional(readOnly = true)
    public Page<?> searchProjects(String keywords, String desLangCode, ProjectSortBy sortBy,
            int pageNumber) {
        Sort sortDetails = Sort.by(Direction.DESC, sortBy.toString());
        Pageable pageDetails = PageRequest.of(Math.max(0, pageNumber), PAGE_SIZE, sortDetails);
        Boolean shouldIncludeDeslangCode = StringUtils.hasText(desLangCode) && !desLangCode.toUpperCase().equals("ALL")
                && languageService.existsByCode(desLangCode);

        if (!StringUtils.hasText(keywords)) {
            if (shouldIncludeDeslangCode) {
                return projectRepository.findAllByDesLangCode(desLangCode,
                        pageDetails);
            }
            return projectRepository.findAllBy(pageDetails);
        } else {
            if (shouldIncludeDeslangCode) {
                return projectRepository.search(keywords, desLangCode, pageDetails);
            }

            return projectRepository.search(keywords, pageDetails);
        }
    }

    public Project validateProject(Long projectId) {
        return projectRepository.findById(projectId).orElseThrow(() -> {
            throw new BusinessException(HttpStatus.BAD_REQUEST, ErrorMessage.PROJECT_NOT_FOUND);
        });
    }

    public Channel validateChannel(String channelUrl) {
        return channelRepository.findByUrl(channelUrl).orElseThrow(() -> {
            throw new BusinessException(HttpStatus.BAD_REQUEST, ErrorMessage.CHANNEL_NOT_FOUND);
        });
    }
}
