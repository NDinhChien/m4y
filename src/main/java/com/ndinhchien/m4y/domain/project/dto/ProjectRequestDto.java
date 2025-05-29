package com.ndinhchien.m4y.domain.project.dto;

import java.util.List;

import com.ndinhchien.m4y.domain.project.type.RequestStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

public class ProjectRequestDto {

    @Getter
    public class CreateVideoDto {
        @NotBlank
        private String channelUrl;
        private String channelName;
        private String channelDescription;
        private String channelImage;

        @NotBlank
        private String videoUrl;
        private String videoName;
        private String videoDescription;
        private String videoImage;
        @NotBlank
        private Integer videoDuration;
        @NotBlank
        private String videoLangCode;
    }

    @Getter
    public static class CreateProjectDto {

        @NotBlank
        private String channelUrl;

        @NotBlank
        private String videoUrl;

        @NotBlank
        private String langCode;

        private String name;

        private String description;

    }

    @Getter
    public static class UpdateProjectDto {

        @NotNull
        private Long projectId;

        private String name;

        private String description;

        private String langCode;

        private Boolean isCompleted;
    }

    @Getter
    public static class HandleTranslatorRequestDto {
        @NotNull
        private Long id;

        private RequestStatus status;
    }
}
