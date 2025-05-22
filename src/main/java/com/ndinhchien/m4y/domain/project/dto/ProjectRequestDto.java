package com.ndinhchien.m4y.domain.project.dto;

import java.util.List;

import com.ndinhchien.m4y.domain.project.type.ProjectSortBy;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

public class ProjectRequestDto {

    @Getter
    public static class CreateProjectDto {

        @NotBlank
        private String srcUrl;

        private String title;

        private String description;

        @NotBlank
        private String channelName;

        @NotBlank
        private String channelUrl;

        @Min(1)
        @NotNull
        private Integer duration;

        @NotBlank
        private String desLangName;

        @NotBlank
        private String srcLangName;

    }

    @Getter
    public static class UpdateProjectDto {
        private String title;

        private String description;

        private String desLangName;

        private String srcLangName;

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
