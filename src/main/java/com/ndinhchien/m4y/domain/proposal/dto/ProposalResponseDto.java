package com.ndinhchien.m4y.domain.proposal.dto;

import java.time.Instant;

public class ProposalResponseDto {
    public static interface IProposable {

        Long getProposerId();

        Integer getReactCount();

        Boolean getIsApproved();

        Instant getCreatedAt();
    }

    public static interface ILanguage extends IProposable {
        String getName();

        String getCode();

    }

    public static interface ICountry extends IProposable {
        String getName();

        String getCode();

        String getLanguageName();
    }

    public static interface IDiocese extends IProposable {

        String getName();

        String getCountryName();
    }

    public static interface IDeanery extends IProposable {
        String getName();

        String getDioceseName();
    }

    public static interface IParish extends IProposable {
        String getName();

        String getDeaneryName();

        String getAddress();
    }
}
