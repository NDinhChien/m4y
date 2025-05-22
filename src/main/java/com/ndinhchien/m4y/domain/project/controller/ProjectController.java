package com.ndinhchien.m4y.domain.project.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
import com.ndinhchien.m4y.domain.project.dto.ProjectRequestDto.AcceptTranslatorDto;
import com.ndinhchien.m4y.domain.project.dto.ProjectRequestDto.AddTranslatorsDto;
import com.ndinhchien.m4y.domain.project.dto.ProjectRequestDto.CreateProjectDto;
import com.ndinhchien.m4y.domain.project.dto.ProjectRequestDto.UpdateProjectDto;
import com.ndinhchien.m4y.domain.project.dto.ProjectResponseDto.IChannel;
import com.ndinhchien.m4y.domain.project.entity.Project;
import com.ndinhchien.m4y.domain.project.entity.ProjectTranslator;
import com.ndinhchien.m4y.domain.project.service.ProjectService;
import com.ndinhchien.m4y.domain.project.service.ProjectTranslatorService;
import com.ndinhchien.m4y.domain.project.type.ProjectSortBy;
import com.ndinhchien.m4y.global.dto.BaseResponse;
import com.ndinhchien.m4y.global.dto.PageDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "project", description = "Project Related APIs")
@RequiredArgsConstructor
@RequestMapping("/api/v1/project")
@RestController
public class ProjectController {

        private final ProjectService projectService;
        private final ProjectTranslatorService translatorService;

        @Operation(summary = "Get project by id")
        @GetMapping("/id/{id}")
        public BaseResponse<?> getProject(
                        @AuthenticationPrincipal UserDetailsImpl userDetails,
                        @PathVariable Long id) {
                return BaseResponse.success("Project",
                                projectService.getProjectById(userDetails == null ? null : userDetails.getUser(), id));
        }

        @Operation(summary = "Get projects by channel")
        @GetMapping("/channel")
        public BaseResponse<?> getProjectsByChannel(
                        @RequestParam String url) {
                return BaseResponse.success("Channel's projects", projectService.getProjectsByChannel(url));
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
        public BaseResponse<?> updateViewCount(
                        @AuthenticationPrincipal UserDetailsImpl userDetails,
                        @RequestParam Long projectId) {
                return BaseResponse.success("Project updated",
                                projectService.updateViewCount(userDetails.getUser(), projectId));

        }

        @Operation(summary = "Update project")
        @PutMapping
        public BaseResponse<Project> updateProject(@AuthenticationPrincipal UserDetailsImpl userDetails,
                        @RequestParam Long projectId,
                        @RequestBody @Valid UpdateProjectDto requestDto) {
                return BaseResponse.success("Project updated",
                                projectService.updateProject(userDetails.getUser(), projectId, requestDto));
        }

        @Operation(summary = "Hard delete project")
        @DeleteMapping()
        public BaseResponse<?> hardDeleteProject(
                        @AuthenticationPrincipal UserDetailsImpl userDetails,
                        @RequestBody List<Long> ids) {
                return BaseResponse.success("Projects permanently deleted",
                                projectService.hardDeleteProjects(userDetails.getUser(), ids));
        }

        @Operation(summary = "Add translators")
        @PostMapping("/translators")
        public BaseResponse<List<ProjectTranslator>> addTranslators(
                        @AuthenticationPrincipal UserDetailsImpl userDetails,
                        @RequestBody AddTranslatorsDto requestDto) {
                return BaseResponse.success("Translators added",
                                translatorService.adminAddTranslators(userDetails.getUser(), requestDto));
        }

        @Operation(summary = "Accept translator")
        @PutMapping("/translator")
        public BaseResponse<?> acceptTranslator(
                        @AuthenticationPrincipal UserDetailsImpl userDetails,
                        @RequestBody AcceptTranslatorDto requestDto) {
                return BaseResponse.success("Translator accepted",
                                translatorService.adminAcceptTranslatorRequest(userDetails.getUser(),
                                                requestDto.getRequestId()));
        }

        @Operation(summary = "Search channels")
        @GetMapping("/channel/search")
        public BaseResponse<List<IChannel>> searchChannels(
                        @RequestParam String name) {
                return BaseResponse.success("Search channels", projectService.searchChannels(name));
        }

        @Operation(summary = "Search projects")
        @GetMapping("/search")
        public BaseResponse<?> searchProjects(
                        @RequestParam(defaultValue = "") String keywords,
                        @RequestParam(defaultValue = "all") String desLangCode,
                        @RequestParam(defaultValue = "relevance") ProjectSortBy sortBy,
                        @RequestParam(defaultValue = "0") int pageNumber) {
                Page<?> page = projectService.searchProjects(keywords, desLangCode, sortBy, pageNumber);
                return BaseResponse.success("Search projects", new PageDto<>(page));
        }
}
