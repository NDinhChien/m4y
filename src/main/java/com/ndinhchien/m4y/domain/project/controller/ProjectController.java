package com.ndinhchien.m4y.domain.project.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ndinhchien.m4y.domain.auth.type.UserDetailsImpl;
import com.ndinhchien.m4y.domain.project.dto.ProjectRequestDto.CreateProjectDto;
import com.ndinhchien.m4y.domain.project.dto.ProjectRequestDto.CreateVideoDto;
import com.ndinhchien.m4y.domain.project.dto.ProjectRequestDto.UpdateProjectDto;
import com.ndinhchien.m4y.domain.project.dto.ProjectResponseDto.IBasicChannel;
import com.ndinhchien.m4y.domain.project.dto.ProjectResponseDto.IBasicProject;
import com.ndinhchien.m4y.domain.project.dto.ProjectResponseDto.IBasicProjectWithRequest;
import com.ndinhchien.m4y.domain.project.dto.ProjectResponseDto.IVideo;
import com.ndinhchien.m4y.domain.project.entity.Project;
import com.ndinhchien.m4y.domain.project.entity.Video;
import com.ndinhchien.m4y.domain.project.service.ProjectService;
import com.ndinhchien.m4y.domain.project.type.ProjectSortBy;
import com.ndinhchien.m4y.domain.user.dto.UserResponseDto.IPublicUser;
import com.ndinhchien.m4y.global.dto.BaseResponse;
import com.ndinhchien.m4y.global.dto.PageDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "project", description = "Project Related APIs")
@RequiredArgsConstructor
@RequestMapping("/api/v1/project")
@RestController
public class ProjectController {

    private final ProjectService projectService;

    @Operation(summary = "Get channels")
    @GetMapping("/channel/all")
    public BaseResponse<?> getChannels() {
        return BaseResponse.success("Channels", projectService.getChannels());
    }

    @Operation(summary = "Get video by url")
    @GetMapping("/video")
    public BaseResponse<IVideo> getVideoByUrl(
            @RequestParam String videoUrl) {
        return BaseResponse.success("Video", projectService.getVideoByUrl(videoUrl));
    }

    @Operation(summary = "Create video")
    @PostMapping("/video")
    public BaseResponse<Video> createVideo(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody @Valid CreateVideoDto requestDto) {
        return BaseResponse.success("Video created",
                projectService.createVideo(userDetails.getUser(), requestDto));
    }

    @Operation(summary = "Get project by id")
    @GetMapping("/id/{id}")
    public BaseResponse<?> getProject(
            @AuthenticationPrincipal @Nullable UserDetailsImpl userDetails,
            @PathVariable Long id) {
        return BaseResponse.success("Project",
                projectService.getProjectById(userDetails == null ? null : userDetails.getUser(), id));
    }

    @Operation(summary = "Get projects")
    @GetMapping("/many")
    @Transactional(readOnly = true)
    public List<IBasicProject> getProjectsByIds(List<Long> ids) {
        return projectService.getProjectsByIds(ids);
    }

    @Operation(summary = "Get user's projects")
    @GetMapping("/all")
    public BaseResponse<List<IBasicProjectWithRequest>> getProjects(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return BaseResponse.success("Projects", projectService.getProjectsByAdmin(userDetails.getUser()));
    }

    @Operation(summary = "Create project")
    @PostMapping
    public BaseResponse<Project> createProject(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody @Valid CreateProjectDto requestDto) {
        return BaseResponse.success("Project created",
                projectService.createProject(userDetails.getUser(), requestDto));
    }

    @Operation(summary = "Update view count")
    @PutMapping("/viewCount")
    public BaseResponse<Integer> updateViewCount(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam Long projectId) {
        return BaseResponse.success("Project updated",
                projectService.updateViewCount(userDetails.getUser(), projectId));
    }

    @Operation(summary = "Update project")
    @PutMapping
    public BaseResponse<Project> updateProject(@AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody @Valid UpdateProjectDto requestDto) {
        return BaseResponse.success("Project updated",
                projectService.updateProject(userDetails.getUser(), requestDto));
    }

    @Operation(summary = "Hard delete projects")
    @DeleteMapping
    public BaseResponse<?> hardDeleteProject(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody List<Long> ids) {
        return BaseResponse.success("Projects deleted",
                projectService.hardDeleteProjects(userDetails.getUser(), ids));
    }

    @Operation(summary = "Search channels")
    @GetMapping("/channel/search")
    public BaseResponse<List<IBasicChannel>> searchChannels(
            @RequestParam String name) {
        return BaseResponse.success("Search channels", projectService.searchChannels(name));
    }

    @Operation(summary = "Search projects")
    @GetMapping("/search")
    public BaseResponse<?> searchProjects(
            @RequestParam(defaultValue = "") String keywords,
            @RequestParam(defaultValue = "all") String langCode,
            @RequestParam(defaultValue = "") String channelUrl,
            @RequestParam(defaultValue = "relevance") ProjectSortBy sortBy,
            @RequestParam(defaultValue = "DESC") Direction sortOrder,
            @RequestParam(defaultValue = "0") int pageNumber) {
        Page<?> page = projectService.searchProjects(keywords, langCode, sortBy, sortOrder, channelUrl,
                pageNumber);
        return BaseResponse.success("Search projects", new PageDto<>(page));
    }
}
