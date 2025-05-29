package com.ndinhchien.m4y.domain.project.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ndinhchien.m4y.domain.auth.type.UserDetailsImpl;
import com.ndinhchien.m4y.domain.project.dto.ProjectRequestDto.HandleTranslatorRequestDto;
import com.ndinhchien.m4y.domain.project.dto.ProjectResponseDto.IRequest;
import com.ndinhchien.m4y.domain.project.entity.Request;
import com.ndinhchien.m4y.domain.project.service.RequestService;
import com.ndinhchien.m4y.global.dto.BaseResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "request", description = "Translator Request Related APIs")
@RequiredArgsConstructor
@RequestMapping("/api/v1/request")
@RestController
public class RequestController {

    private final RequestService requestService;

    @Operation(summary = "Get all translator requests")
    @GetMapping("/all")
    public BaseResponse<List<IRequest>> getRequests(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return BaseResponse.success("Requests",
                requestService.getUserRequests(userDetails.getUser()));
    }

    @Operation(summary = "Make translator request")
    @PostMapping
    public BaseResponse<Request> makeTranslatorRequest(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam Long projectId) {
        return BaseResponse.success("Request successfully",
                requestService.makeTranslatorRequest(userDetails.getUser(), projectId));
    }

    @Operation(summary = "Hard delete translator request")
    @DeleteMapping
    public BaseResponse<Integer> hardDeleteTranslatorRequest(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam Long requestId) {
        return BaseResponse.success("Delete count",
                requestService.hardDeleteRequest(userDetails.getUser(), requestId));
    }

    @Operation(summary = "Handle translator request")
    @PutMapping("/admin")
    public BaseResponse<?> adminHandleTranslatorRequest(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody HandleTranslatorRequestDto requestDto) {
        return BaseResponse.success("Request handled",
                requestService.adminHandleRequest(userDetails.getUser(),
                        requestDto));
    }

    @Operation(summary = "Add translators to project")
    @PostMapping("/admin")
    public BaseResponse<List<Request>> addTranslators(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam Long projectId,
            @RequestBody List<Long> requestDto) {
        return BaseResponse.success("Translators added",
                requestService.adminAddTranslators(userDetails.getUser(), projectId, requestDto));
    }

}
