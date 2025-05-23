package com.ndinhchien.m4y.domain.project.dto;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

public class ProjectRequestDto {

    @Getter
    public static class CreateProjectDto {

        @NotBlank
        private String channelUrl;
        private String channelName;
        private String channelDescription;

        @NotBlank
        private String videoUrl;
        private String videoName;
        private String videoDescription;
        private Integer videoDuration;
        private String videoLangCode;

        private String name;

        private String description;

        @NotBlank
        private String langCode;

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
    public static class AddTranslatorsDto {
        @NotNull
        private Long projectId;

        @NotNull
        private List<Long> ids;
    }

    @Getter
    public static class AcceptTranslatorDto {
        @NotNull
        private Long requestId;
    }
}
