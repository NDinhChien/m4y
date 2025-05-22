package com.ndinhchien.m4y.domain.address.dto;

import com.ndinhchien.m4y.domain.address.type.AddresssType;
import com.ndinhchien.m4y.domain.reaction.type.Emoji;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

public class AddressRequestDto {

    @Getter
    public static class AddAddressDto {

        @NotBlank
        private String name;

        @NotNull
        private AddresssType type;

        private String countryName;

        private String dioceseName;

        private String deaneryName;

        private String detailedAddress;

    }

    @Getter
    public static class DeleteAddressDto {
        @NotBlank
        private String name;

        @NotNull
        private AddresssType type;
    }

    @Getter
    public static class UpdateAddressDto {
        @NotBlank
        private String name;

        @NotNull
        private AddresssType type;

        @NotNull
        private Boolean isApproved;
    }

}
