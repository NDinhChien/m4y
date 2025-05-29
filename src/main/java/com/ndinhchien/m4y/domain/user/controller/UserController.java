package com.ndinhchien.m4y.domain.user.controller;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ndinhchien.m4y.domain.auth.type.UserDetailsImpl;
import com.ndinhchien.m4y.domain.user.dto.UserRequestDto.UpdateAddressDto;
import com.ndinhchien.m4y.domain.user.dto.UserRequestDto.UpdateProfileDto;
import com.ndinhchien.m4y.domain.user.dto.UserResponseDto.IPublicUser;
import com.ndinhchien.m4y.domain.user.dto.UserResponseDto.IUser;
import com.ndinhchien.m4y.domain.user.service.UserService;
import com.ndinhchien.m4y.global.dto.BaseResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "user", description = "User Related APIs")
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
@RestController
public class UserController {

    private final UserService userService;

    @Operation(summary = "Get profile")
    @GetMapping("/profile")
    public BaseResponse<IUser> getProfile(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return BaseResponse.success("Profile", userService.getProfile(userDetails.getUser()));
    }

    @Operation(summary = "Get user by id")
    @GetMapping("/id/{id}")
    public BaseResponse<IPublicUser> getUser(
            @PathVariable Long id) {
        return BaseResponse.success("Users profile", userService.getUserById(id));
    }

    @Operation(summary = "Get users by ids")
    @PostMapping("/many")
    public BaseResponse<List<IPublicUser>> getUsers(
            @RequestBody List<Long> ids) {
        return BaseResponse.success("Users profile", userService.getUsersByIds(ids));
    }

    @Operation(summary = "Search users")
    @GetMapping("/search")
    public BaseResponse<?> searchUsers(
            @RequestParam String name) {
        return BaseResponse.success("Search users", userService.searchUsers(name));
    }

    @Operation(summary = "Update profile")
    @PutMapping("/profile")
    public BaseResponse<?> updateProfile(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody @Valid UpdateProfileDto requestDto) {
        return BaseResponse.success("Profile updated", userService.updateProfile(userDetails.getUser(), requestDto));
    }

    @Operation(summary = "Update address")
    @PutMapping("/address")
    public BaseResponse<?> updateAddress(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody @Valid UpdateAddressDto requestDto) {
        return BaseResponse.success("Address updated", userService.updateAddress(userDetails.getUser(), requestDto));
    }

    @Operation(summary = "Update avatar")
    @PutMapping(path = "/avatar", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public BaseResponse<?> updateAvatar(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestPart(name = "avatar", required = true) MultipartFile avatar) {
        return BaseResponse.success("Update avatar successfully.",
                userService.updateAvatar(userDetails.getUser(), avatar));
    }

    @Operation(summary = "(Un)follow another user")
    @PutMapping("/follow/toggle")
    public BaseResponse<Integer> toggleFollow(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam Long userId) {
        return BaseResponse.success("Updated followings",
                userService.toggleFollow(userDetails.getUser(), userId));
    }

}
