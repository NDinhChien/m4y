package com.ndinhchien.m4y.domain.proposal.dto;

import com.ndinhchien.m4y.domain.proposal.type.ProposalType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

public class ProposalRequestDto {

    @Getter
    public static class AddProposalDto {

        @NotBlank
        private String name;

        @NotNull
        private ProposalType type;

        private String countryName;

        private String dioceseName;

        private String deaneryName;

        private String address;

    }

    @Getter
    public static class DeleteProposalDto {
        @NotBlank
        private String name;

        @NotNull
        private ProposalType type;
    }

    @Getter
    public static class UpdateProposalDto {
        @NotBlank
        private String name;

        @NotNull
        private ProposalType type;

        @NotNull
        private Boolean isApproved;
    }

}
