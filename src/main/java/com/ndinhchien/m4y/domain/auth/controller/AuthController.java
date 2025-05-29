package com.ndinhchien.m4y.domain.auth.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.ModelMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ndinhchien.m4y.domain.auth.dto.AuthRequestDto.LoginRequestDto;
import com.ndinhchien.m4y.domain.auth.dto.AuthRequestDto.RegisterRequestDto;
import com.ndinhchien.m4y.domain.auth.dto.AuthRequestDto.ResetPasswordDto;
import com.ndinhchien.m4y.domain.auth.dto.AuthRequestDto.UpdatePasswordDto;
import com.ndinhchien.m4y.domain.auth.dto.AuthRequestDto.VerifyAccountDto;
import com.ndinhchien.m4y.domain.auth.dto.AuthResponseDto.JwtResponseDto;
import com.ndinhchien.m4y.domain.auth.service.AuthService;
import com.ndinhchien.m4y.domain.auth.strategy.GoogleLoginStrategy;
import com.ndinhchien.m4y.domain.auth.type.UserDetailsImpl;
import com.ndinhchien.m4y.domain.user.entity.User;
import com.ndinhchien.m4y.global.dto.BaseResponse;
import com.ndinhchien.m4y.global.service.LinkService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;

@Tag(name = "auth", description = "Auth Related APIs")
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@RestController
public class AuthController {

    private final AuthService authService;
    private final GoogleLoginStrategy googleLoginStrategy;
    private final LinkService linkService;

    @Operation(summary = "Register account")
    @PostMapping("/register")
    public BaseResponse<User> register(
            @RequestBody @Valid RegisterRequestDto requestDto) {
        return BaseResponse.success("Registered successfully.", authService.register(requestDto));
    }

    @Operation(summary = "Check auth")
    @GetMapping("/check")
    public BaseResponse<User> check(
            @AuthenticationPrincipal @Nullable UserDetailsImpl userDetails) {
        return BaseResponse.success("Auth info: ", userDetails == null ? null : userDetails.getUser());
    }

    @Operation(summary = "Login user")
    @PostMapping("/login")
    public BaseResponse<JwtResponseDto> login(
            @RequestBody @Valid LoginRequestDto requestDto,
            HttpServletResponse response) {
        return BaseResponse.success("Logged in", authService.login(requestDto, response));
    }

    @Operation(summary = "Logout user")
    @DeleteMapping("/logout")
    public void logout(
            @AuthenticationPrincipal @Nullable UserDetailsImpl userDetails,
            HttpServletResponse response) {
        authService.logout(userDetails == null ? null : userDetails.getUser(), response);
    }

    @Operation(summary = "Change password")
    @PutMapping("/password")
    public BaseResponse<Boolean> changePassword(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody @Valid UpdatePasswordDto requestDto) {
        return BaseResponse.success("Password updated", authService.updatePassword(userDetails.getUser(), requestDto));
    }

    @Operation(summary = "Request account's email verification")
    @GetMapping("/request/verify")
    public BaseResponse<?> requestAccountVerification(
            @RequestParam @Email String email) {
        return BaseResponse.success("Please check your email", authService.requestAccountVerification(email));
    }

    @Operation(summary = "Request account's password reset")
    @GetMapping("/request/reset")
    public BaseResponse<Boolean> requestPasswordReset(
            @RequestParam @Email String email) {
        return BaseResponse.success("Please check your email", authService.requestPasswordReset(email));

    }

    @Operation(summary = "Verify account")
    @PutMapping("/verify")
    public BaseResponse<Boolean> verifyAccount(
            @RequestBody @Valid VerifyAccountDto requestDto) {
        return BaseResponse.success("Account verified", authService.verifyAccount(requestDto.getToken()));
    }

    @Operation(summary = "Reset password")
    @PutMapping("/reset")
    public BaseResponse<Boolean> resetPassword(
            @RequestBody @Valid ResetPasswordDto requestDto) {
        return BaseResponse.success("Password reset",
                authService.resetPassword(requestDto.getToken(), requestDto.getPassword()));
    }

    @Operation(summary = "Google login redirect")
    @GetMapping("/google/redirect")
    public ModelAndView googleLogin(
            @RequestParam String code,
            HttpServletResponse response,
            ModelMap model) throws JsonProcessingException {
        JwtResponseDto responseDto = googleLoginStrategy.socialLogin(code, response);
        String redirectUrl = linkService.getGoogleRedirectUrl();
        model.addAttribute("accessToken", responseDto.getAccessToken());
        model.addAttribute("refreshToken", responseDto.getRefreshToken());
        return new ModelAndView("redirect:" + redirectUrl, model);
    }

}
