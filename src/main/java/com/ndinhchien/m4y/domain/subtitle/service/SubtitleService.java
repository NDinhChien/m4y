package com.ndinhchien.m4y.domain.subtitle.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ndinhchien.m4y.domain.project.entity.Project;
import com.ndinhchien.m4y.domain.project.entity.Video;
import com.ndinhchien.m4y.domain.project.repository.ProjectRepository;
import com.ndinhchien.m4y.domain.project.service.RequestService;
import com.ndinhchien.m4y.domain.project.service.VideoService;
import com.ndinhchien.m4y.domain.subtitle.dto.SubtitleRequestDto.AddSubtitleDto;
import com.ndinhchien.m4y.domain.subtitle.dto.SubtitleRequestDto.UpdateDesTextDto;
import com.ndinhchien.m4y.domain.subtitle.dto.SubtitleRequestDto.UpdateSubtitleDto;
import com.ndinhchien.m4y.domain.subtitle.dto.SubtitleResponseDto.ISubtitle;
import com.ndinhchien.m4y.domain.subtitle.entity.Subtitle;
import com.ndinhchien.m4y.domain.subtitle.repository.SubtitleRepository;
import com.ndinhchien.m4y.domain.user.entity.User;
import com.ndinhchien.m4y.global.exception.BusinessException;
import com.ndinhchien.m4y.global.exception.ErrorMessage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class SubtitleService {

    private final VideoService videoService;
    private final ProjectRepository projectRepository;
    private final RequestService requestService;
    private final SubtitleRepository subtitleRepository;

    @Transactional(readOnly = true)
    public List<ISubtitle> getSubtitles(String videoUrl) {
        return subtitleRepository.findAllByVideoUrl(videoUrl);
    }

    @Transactional
    public List<Subtitle> addSubtitles(User user, String videoUrl, List<AddSubtitleDto> requestDto) {
        Video video = videoService.validate(videoUrl);
        if (!video.isCreator(user)) {
            throw new BusinessException(HttpStatus.FORBIDDEN, "You are not the creator of this project's subtitles.");
        }

        if (!projectRepository.existsByVideoUrlAndAdmin(videoUrl, user)) {
            throw new BusinessException(HttpStatus.FORBIDDEN,
                    "You have to create a project with this video first.");
        }
        List<Subtitle> subtitles = requestDto.stream().map((dto) -> {
            return new Subtitle(user, video, dto.getStart(),
                    dto.getEnd(), dto.getSrcText());
        }).toList();

        if (subtitles.size() > 0 && !video.hasCreator()) {
            video.setCreator(user);
            videoService.save(video);
        }

        return subtitleRepository.saveAll(subtitles);
    }

    @Transactional
    public List<Subtitle> updateSubtitles(User user, String videoUrl, List<UpdateSubtitleDto> requestDto) {
        Video video = videoService.validate(videoUrl);
        if (!video.isCreator(user)) {
            throw new BusinessException(HttpStatus.FORBIDDEN, "You are not the creator of this project's subtitles.");
        }

        List<Subtitle> updated = new ArrayList<>();
        for (UpdateSubtitleDto dto : requestDto) {
            Long subtitleId = dto.getId();

            Subtitle subtitle = subtitleRepository.findById(subtitleId).orElse(null);
            if (subtitle == null || !subtitle.getVideoUrl().equals(videoUrl)) {
                continue;
            }

            subtitle.update(user, dto);
            updated.add(subtitle);
        }
        return subtitleRepository.saveAll(updated);
    }

    @Transactional
    public List<Subtitle> hardDeleteSubtitles(User user, List<Long> ids) {
        List<Subtitle> deleted = new ArrayList<>();
        List<Subtitle> subtitles = subtitleRepository.findAllById(ids);
        for (Subtitle subtitle : subtitles) {
            if (!subtitle.hasOtherAdminDesTexts(user)) {
                deleted.add(subtitle);
            }
        }
        subtitleRepository.deleteAll(deleted);
        return deleted;
    }

    @Transactional
    public List<Subtitle> updateTranslations(User user, String videoUrl, String langCode,
            List<UpdateDesTextDto> requestDto) {
        Project project = projectRepository.findByVideoUrlAndLangCode(videoUrl, langCode)
                .orElseThrow(() -> {
                    throw new BusinessException(HttpStatus.BAD_REQUEST, ErrorMessage.PROJECT_NOT_FOUND);
                });

        if (!requestService.isTranslator(project, user)) {
            throw new BusinessException(HttpStatus.FORBIDDEN, "You are not a translator of this project");
        }

        List<Subtitle> updated = new ArrayList<>();
        for (UpdateDesTextDto dto : requestDto) {
            Long subtitleId = dto.getId();

            Subtitle subtitle = subtitleRepository.findById(subtitleId).orElse(null);
            if (subtitle == null || !subtitle.getVideoUrl().equals(videoUrl)) {
                continue;
            }

            subtitle.update(user, dto.getDesText(), langCode);
            updated.add(subtitle);
        }
        return subtitleRepository.saveAll(updated);
    }

    public Subtitle validateSubtitle(Long subtitleId) {
        return subtitleRepository.findById(subtitleId).orElseThrow(() -> {
            throw new BusinessException(HttpStatus.BAD_REQUEST, ErrorMessage.SUBTITLE_NOT_FOUND);
        });
    }

}
