package com.ndinhchien.m4y.domain.user.dto;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

public class UserRequestDto {

    @Getter
    public static class UpdateProfileDto {

        private String userName;

        private String fullName;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC")
        private Instant birthday;

        private String bio;

        private String address;

    }

    @Getter
    public static class UpdateAddressDto {
        @Schema(example = "Vietnam")
        private String countryName;

        @Schema(example = "Xuân Lộc")
        private String dioceseName;

        @Schema(example = "Biên Hòa")
        private String deaneryName;

        @Schema(example = "Đông Hòa")
        private String parishName;
    }
}
