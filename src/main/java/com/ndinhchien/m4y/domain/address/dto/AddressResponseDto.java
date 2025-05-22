package com.ndinhchien.m4y.domain.address.dto;

import java.time.Instant;

public class AddressResponseDto {
    public static interface IProposable {

        Long getProposerId();

        Integer getReactCount();

        Boolean getIsApproved();

        Instant getCreatedAt();
    }

    public static interface ILanguageDto extends IProposable {
        String getName();

        String getCode();

    }

    public static interface ICountryDto extends IProposable {
        String getName();

        String getCode();

        String getLanguageName();
    }

    public static interface IDioceseDto extends IProposable {

        String getName();

        String getCountryName();
    }

    public static interface IDeaneryDto extends IProposable {
        String getName();

        String getDioceseName();
    }

    public static interface IParishDto extends IProposable {
        String getName();

        String getDeaneryName();

        String getAddress();
    }
}
