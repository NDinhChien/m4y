package com.ndinhchien.m4y.domain.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class AuthResponseDto {
    @Getter
    @AllArgsConstructor
    public static class JwtResponseDto {
        private String accessToken;
        private String refreshToken;
    }
}
