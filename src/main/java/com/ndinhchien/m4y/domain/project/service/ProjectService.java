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
import com.ndinhchien.m4y.domain.project.entity.Video;
import com.ndinhchien.m4y.domain.project.entity.ViewHistory;
import com.ndinhchien.m4y.domain.project.repository.ChannelRepository;
import com.ndinhchien.m4y.domain.project.repository.ViewHistoryRepository;
import com.ndinhchien.m4y.domain.project.type.ProjectSortBy;
import com.ndinhchien.m4y.domain.project.repository.ProjectRepository;
import com.ndinhchien.m4y.domain.user.entity.User;
import com.ndinhchien.m4y.global.exception.BusinessException;
import com.ndinhchien.m4y.global.exception.ErrorMessage;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class ProjectService {

    private final static int PAGE_SIZE = 12;

    private final VideoService videoService;
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
        String channelUrl = requestDto.getChannelUrl();
        String channelName = requestDto.getChannelName();
        String channelDescription = requestDto.getChannelDescription();

        Channel channel = channelRepository.findByUrl(channelUrl).orElse(null);
        if (channel == null) {
            channel = channelRepository.save(new Channel(channelUrl, channelName, channelDescription));
        }

        String videoUrl = requestDto.getVideoUrl();
        String videoName = requestDto.getVideoName();
        String videoDescription = requestDto.getDescription();
        Integer videoDuration = requestDto.getVideoDuration();
        String videoLangCode = requestDto.getVideoLangCode();
        Language videoLanguage = null;
        if (StringUtils.hasText(videoLangCode)) {
            videoLanguage = languageService.validateLanguageByCode(videoLangCode);
        }

        Video video = videoService.findByUrl(videoUrl);
        if (video == null) {
            if (channel == null || videoLanguage == null) {
                throw new BusinessException(HttpStatus.BAD_REQUEST);
            }
            video = videoService
                    .save(new Video(channel, videoUrl, videoName, videoDescription, videoDuration, videoLanguage));
        }

        String name = requestDto.getName();
        String description = requestDto.getDescription();
        String langCode = requestDto.getLangCode();
        Language language = languageService.validateLanguageByCode(langCode);

        if (projectRepository.existsByVideoUrlAndLangCode(video.getUrl(), language.getCode())) {
            throw new BusinessException(HttpStatus.BAD_REQUEST,
                    String.format("This project has %s subtitles already", language.getName()));
        }

        Project project = new Project(user, channel, video, name, description, language);
        return projectRepository.save(project);
    }

    @Transactional
    public Project updateProject(User user, UpdateProjectDto requestDto) {
        Long projectId = requestDto.getProjectId();
        String langCode = requestDto.getLangCode();
        Language language = null;
        if (StringUtils.hasText(langCode)) {
            language = languageService.validateLanguageByCode(langCode);
        }

        Project project = validateProject(projectId);
        if (!project.isAdmin(user)) {
            throw new BusinessException(HttpStatus.FORBIDDEN, ErrorMessage.NOT_PROJECT_ADMIN);
        }

        project.update(requestDto);
        if (language != null && !project.getLangCode().equals(language.getCode())) {
            if (!projectRepository.existsByVideoUrlAndLangCode(project.getVideoUrl(), language.getCode())) {
                project.setLangCode(language);
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
    public Page<?> searchProjects(String keywords, String langCode, ProjectSortBy sortBy,
            int pageNumber) {
        Sort sortDetails = Sort.by(Direction.DESC, sortBy.toString());
        Pageable pageDetails = PageRequest.of(Math.max(0, pageNumber), PAGE_SIZE, sortDetails);
        Boolean shouldIncludeLangCode = StringUtils.hasText(langCode) && !langCode.toUpperCase().equals("ALL")
                && languageService.existsByCode(langCode);

        if (!StringUtils.hasText(keywords)) {
            if (shouldIncludeLangCode) {
                return projectRepository.findAllByLangCode(langCode,
                        pageDetails);
            }
            return projectRepository.findAllBy(pageDetails);
        } else {
            if (shouldIncludeLangCode) {
                return projectRepository.search(keywords, langCode, pageDetails);
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
