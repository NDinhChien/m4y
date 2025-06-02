package com.ndinhchien.m4y.domain.proposal.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ndinhchien.m4y.domain.auth.type.UserDetailsImpl;
import com.ndinhchien.m4y.domain.proposal.dto.ProposalRequestDto.AddProposalDto;
import com.ndinhchien.m4y.domain.proposal.dto.ProposalRequestDto.DeleteProposalDto;
import com.ndinhchien.m4y.domain.proposal.dto.ProposalRequestDto.UpdateProposalDto;
import com.ndinhchien.m4y.domain.proposal.service.ProposalService;
import com.ndinhchien.m4y.global.dto.BaseResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "proposal", description = "Proposal Related APIs")
@RequiredArgsConstructor
@RequestMapping("/api/v1/proposal")
@RestController
public class ProposalController {

    private final ProposalService proposalService;

    @Operation(summary = "All address proposals")
    @GetMapping("/address")
    public BaseResponse<?> getData() {
        return BaseResponse.success("All address proposals",
                proposalService.getAddressProposals());
    }

    @Operation(summary = "Propose new address")
    @PostMapping("/address")
    public BaseResponse<?> addAddressProposal(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody @Valid AddProposalDto requestDto) {

        return BaseResponse.success("New address added",
                proposalService.addAddressProposal(userDetails.getUser(), requestDto));
    }

    @Operation(summary = "Update proposal")
    @PutMapping("/sys")
    public BaseResponse<?> updateProposal(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody @Valid UpdateProposalDto requestDto) {

        return BaseResponse.success("Update proposal",
                proposalService.sysAdminUpdateProposal(userDetails.getUser(), requestDto));
    }

    @Operation(summary = "Hard delete proposal")
    @DeleteMapping("/sys")
    public BaseResponse<?> deleteProposal(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody @Valid DeleteProposalDto requestDto) {

        return BaseResponse.success("Proposal deleted count",
                proposalService.sysAdminHardDeleteProposal(userDetails.getUser(), requestDto));
    }

}
