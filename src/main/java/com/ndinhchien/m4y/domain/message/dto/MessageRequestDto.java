package com.ndinhchien.m4y.domain.message.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

public class MessageRequestDto {

    @Getter
    public static class SendMessageDto {

        @NotBlank
        private String content;

    }

    @Getter
    public static class UpdateMessageDto {
        @NotNull
        private Long messageId;

        private String content;

        private Boolean isDeleted;

    }
}
