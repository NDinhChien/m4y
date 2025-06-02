package com.ndinhchien.m4y.domain.subtitle.controller;

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
import com.ndinhchien.m4y.domain.subtitle.dto.SubtitleRequestDto.AddSubtitleDto;
import com.ndinhchien.m4y.domain.subtitle.dto.SubtitleRequestDto.UpdateDesTextDto;
import com.ndinhchien.m4y.domain.subtitle.dto.SubtitleRequestDto.UpdateSubtitleDto;
import com.ndinhchien.m4y.domain.subtitle.dto.SubtitleResponseDto.ISubtitle;
import com.ndinhchien.m4y.domain.subtitle.entity.Subtitle;
import com.ndinhchien.m4y.domain.subtitle.service.SubtitleService;
import com.ndinhchien.m4y.global.dto.BaseResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "subtitle", description = "Subtitle Related APIs")
@RequiredArgsConstructor
@RequestMapping("/api/v1/subtitle")
@RestController
public class SubtitleController {

    private final SubtitleService subtitleService;

    @Operation(summary = "Get video's subtitles")
    @GetMapping
    public BaseResponse<List<ISubtitle>> getSubtitles(
            @RequestParam String videoUrl) {
        return BaseResponse.success("Video's subtitles", subtitleService.getSubtitles(videoUrl));
    }

    @Operation(summary = "Add original subtitles")
    @PostMapping("/origin")
    public BaseResponse<List<Subtitle>> addSubtitles(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam String videoUrl,
            @RequestBody List<@Valid AddSubtitleDto> requestDto) {

        return BaseResponse.success("Add subtitles",
                subtitleService.addSubtitles(userDetails.getUser(), videoUrl, requestDto));
    }

    @Operation(summary = "Update original subtitles")
    @PutMapping("/origin")
    public BaseResponse<List<Subtitle>> updateSubtitles(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam String videoUrl,
            @RequestBody List<@Valid UpdateSubtitleDto> requestDto) {
        return BaseResponse.success("Update subtitles",
                subtitleService.updateSubtitles(userDetails.getUser(), videoUrl,
                        requestDto));
    }

    @Operation(summary = "Delete original subtitles")
    @DeleteMapping("/origin")
    public BaseResponse<List<Subtitle>> deteteSubtitles(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody List<Long> ids) {
        return BaseResponse.success("Subtitles deleted",
                subtitleService.hardDeleteSubtitles(userDetails.getUser(), ids));
    }

    @Operation(summary = "Update subtitle translations")
    @PutMapping("/translation")
    public BaseResponse<List<Subtitle>> updateTranslations(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam String videoUrl,
            @RequestParam String langCode,
            @RequestBody List<@Valid UpdateDesTextDto> requestDto) {
        return BaseResponse.success("Update subtitle translations",
                subtitleService.updateTranslations(userDetails.getUser(), videoUrl, langCode,
                        requestDto));
    }
}
