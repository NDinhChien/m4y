package com.ndinhchien.m4y.domain.subtitle.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

public class SubtitleRequestDto {

    @Getter
    public static class AddSubtitleDto {

        @NotNull
        private Integer start;

        @NotNull
        private Integer end;

        @NotNull
        private String srcText;

    }

    @Getter
    public static class UpdateSubtitleDto {
        @NotNull
        private Long id;

        private Integer start;

        private Integer end;

        private String srcText;

    }

    @Getter
    public static class UpdateDesTextDto {
        @NotNull
        private Long id;

        @NotBlank
        private String desText;

    }

}
