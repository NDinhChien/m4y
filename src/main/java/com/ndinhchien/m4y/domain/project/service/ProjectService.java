package com.ndinhchien.m4y.domain.project.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.ndinhchien.m4y.domain.project.dto.ProjectRequestDto.CreateVideoDto;
import com.ndinhchien.m4y.domain.project.dto.ProjectRequestDto.CreateProjectDto;
import com.ndinhchien.m4y.domain.project.dto.ProjectRequestDto.UpdateProjectDto;
import com.ndinhchien.m4y.domain.project.dto.ProjectResponseDto.IBasicChannel;
import com.ndinhchien.m4y.domain.project.dto.ProjectResponseDto.IBasicProject;
import com.ndinhchien.m4y.domain.project.dto.ProjectResponseDto.IBasicProjectWithRequest;
import com.ndinhchien.m4y.domain.project.dto.ProjectResponseDto.IChannel;
import com.ndinhchien.m4y.domain.project.dto.ProjectResponseDto.IProject;
import com.ndinhchien.m4y.domain.project.dto.ProjectResponseDto.IVideo;
import com.ndinhchien.m4y.domain.project.entity.Channel;
import com.ndinhchien.m4y.domain.project.entity.Project;
import com.ndinhchien.m4y.domain.project.entity.Video;
import com.ndinhchien.m4y.domain.project.entity.ViewHistory;
import com.ndinhchien.m4y.domain.project.repository.ChannelRepository;
import com.ndinhchien.m4y.domain.project.repository.ViewHistoryRepository;
import com.ndinhchien.m4y.domain.project.type.ProjectSortBy;
import com.ndinhchien.m4y.domain.proposal.dto.ProposalResponseDto.ILanguage;
import com.ndinhchien.m4y.domain.proposal.entity.Language;
import com.ndinhchien.m4y.domain.proposal.repository.LanguageRepository;
import com.ndinhchien.m4y.domain.project.repository.ProjectRepository;
import com.ndinhchien.m4y.domain.project.repository.VideoRepository;
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

    private final VideoRepository videoRepository;
    private final ProjectRepository projectRepository;
    private final ChannelRepository channelRepository;
    private final ViewHistoryRepository viewHistoryRepository;
    private final LanguageRepository languageRepository;

    @Transactional(readOnly = true)
    public IProject getProjectById(Long projectId) {
        return projectRepository.findProjectById(projectId).orElse(null);
    }

    public List<IBasicProject> getProjectsByIds(List<Long> ids) {
        return projectRepository.findAllByIdIn(ids);
    }

    @Transactional(readOnly = true)
    public List<IBasicProjectWithRequest> getProjectsByAdmin(User admin) {
        return projectRepository.findAllByAdmin(admin);
    }

    @Transactional(readOnly = true)
    public List<IBasicProject> getProjectsByChannelUrl(String channelUrl) {
        return projectRepository.findAllByChannelUrl(channelUrl);
    }

    private boolean isProjectAdmin(Long projectId, User user) {
        return projectRepository.existsByIdAndAdmin(projectId, user);
    }

    @Transactional(readOnly = true)
    public List<IBasicChannel> getChannels() {
        return channelRepository.findAllBy();
    }

    @Transactional(readOnly = true)
    public List<ILanguage> getLanguages() {
        return languageRepository.findAllByIsApproved(true);
    }

    @Transactional(readOnly = true)
    public IChannel getChannelByUrl(String channelUrl) {
        return channelRepository.findChannelByUrl(channelUrl).orElse(null);
    }

    @Transactional(readOnly = true)
    public Object getVideoByUrl(String videoUrl, Boolean includeSubtitles) {
        if (includeSubtitles == true) {
            return videoRepository.findOneByUrl(videoUrl).orElse(null);
        }
        return videoRepository.findVideoByUrl(videoUrl).orElse(null);
    }

    @Transactional
    public Video createVideo(User user, CreateVideoDto requestDto) {
        String channelUrl = requestDto.getChannelUrl();
        String channelName = requestDto.getChannelName();
        String channelDescription = requestDto.getChannelDescription();
        String channelImage = requestDto.getChannelImage();

        Channel channel = channelRepository.findByUrl(channelUrl).orElse(null);
        if (channel == null) {
            channel = channelRepository.save(new Channel(channelUrl, channelName, channelDescription, channelImage));
        }

        String videoUrl = requestDto.getVideoUrl();
        String videoName = requestDto.getVideoName();
        String videoDescription = requestDto.getVideoDescription();
        String videoImage = requestDto.getVideoImage();
        Integer videoDuration = requestDto.getVideoDuration();

        String videoLangCode = requestDto.getVideoLangCode();
        Language videoLanguage = null;
        if (StringUtils.hasText(videoLangCode)) {
            videoLanguage = validateLanguageByCode(videoLangCode);
        }

        Video video = videoRepository.findByUrl(videoUrl).orElse(null);
        if (video == null) {
            if (channel == null || videoLanguage == null) {
                throw new BusinessException(HttpStatus.BAD_REQUEST);
            }
            video = videoRepository
                    .save(new Video(channel, videoUrl, videoName, videoDescription, videoImage, videoDuration,
                            videoLanguage));
        }
        return video;
    }

    @Transactional
    public Project createProject(User user, CreateProjectDto requestDto) {
        String channelUrl = requestDto.getChannelUrl();
        Channel channel = validateChannel(channelUrl);

        String videoUrl = requestDto.getVideoUrl();
        Video video = validateVideo(videoUrl);

        String name = requestDto.getName();
        String description = requestDto.getDescription();
        String langCode = requestDto.getLangCode();
        Language language = validateLanguageByCode(langCode);

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
            language = validateLanguageByCode(langCode);
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
    public Project hardDeleteProject(User user, Long projectId) {
        Project project = validateProject(projectId);
        if (user.isSysAdmin() || project.isAdmin(user)) {
            projectRepository.delete(project);
            return project;
        }
        return null;
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
    public Integer updateViewCount(User user, Long projectId) {
        Project project = validateProject(projectId);
        ViewHistory view = viewHistoryRepository.findByProjectAndUser(project, user)
                .orElse(new ViewHistory(user, project));
        int count = view.updateViewCount(project.getDuration());
        viewHistoryRepository.save(view);

        project.updateViewCount(count);
        project = projectRepository.save(project);
        return project.getViewCount();

    }

    @Transactional(readOnly = true)
    public List<IBasicChannel> searchChannels(String name) {
        return channelRepository.findByNameContaining(name);
    }

    @Transactional(readOnly = true)
    public Page<?> searchProjects(String keywords, String langCode, ProjectSortBy sortBy, Direction sortOrder,
            String channelUrl, int pageNumber) {
        Sort sortDetails = Sort.by(sortOrder, sortBy.toString());
        Pageable pageDetails = PageRequest.of(Math.max(0, pageNumber), PAGE_SIZE, sortDetails);
        Boolean shouldIncludeLangCode = StringUtils.hasText(langCode) && !langCode.toUpperCase().equals("ALL")
                && languageRepository.existsByCode(langCode);

        if (StringUtils.hasText(keywords)) {
            if (shouldIncludeLangCode) {
                return projectRepository.search(keywords, langCode, pageDetails);
            }
            return projectRepository.search(keywords, pageDetails);
        }

        if (sortBy.equals(ProjectSortBy.relevance)) {
            sortDetails = Sort.by(sortOrder, ProjectSortBy.viewCount.toString());
            pageDetails = PageRequest.of(Math.max(0, pageNumber), PAGE_SIZE, sortDetails);
        }

        if (StringUtils.hasText(channelUrl) && !channelUrl.toUpperCase().equals("ALL")) {
            return projectRepository.findAllByChannelUrl(channelUrl, pageDetails);
        } else {

            if (shouldIncludeLangCode) {
                return projectRepository.findAllByLangCode(langCode,
                        pageDetails);
            }
            return projectRepository.findAllBy(pageDetails);
        }
    }

    public Project validateProject(Long projectId) {
        return projectRepository.findById(projectId).orElseThrow(() -> {
            throw new BusinessException(HttpStatus.BAD_REQUEST, ErrorMessage.PROJECT_NOT_FOUND);
        });
    }

    public Video validateVideo(String videoUrl) {
        return videoRepository.findByUrl(videoUrl).orElseThrow(() -> {
            throw new BusinessException(HttpStatus.BAD_REQUEST, ErrorMessage.VIDEO_NOT_FOUND);
        });
    }

    public Channel validateChannel(String channelUrl) {
        return channelRepository.findByUrl(channelUrl).orElseThrow(() -> {
            throw new BusinessException(HttpStatus.BAD_REQUEST, ErrorMessage.CHANNEL_NOT_FOUND);
        });
    }

    public Language validateLanguageByCode(@NotNull String langCode) {

        return languageRepository.findByCode(langCode).orElseThrow(() -> {

            throw new BusinessException(HttpStatus.BAD_REQUEST, ErrorMessage.LANGUAGE_NOT_FOUND);
        });
    }

}
