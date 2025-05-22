package com.ndinhchien.m4y.domain.subtitle.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ndinhchien.m4y.domain.project.entity.Project;
import com.ndinhchien.m4y.domain.project.repository.ProjectRepository;
import com.ndinhchien.m4y.domain.project.service.ProjectTranslatorService;
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

    private final ProjectRepository projectRepository;
    private final ProjectTranslatorService translatorService;
    private final SubtitleRepository subtitleRepository;

    @Transactional(readOnly = true)
    public List<ISubtitle> getSubtitles(String projectSrcUrl) {
        return subtitleRepository.findAllByProjectSrcUrl(projectSrcUrl);
    }

    @Transactional
    public List<Subtitle> initSubtitles(User user, String projectSrcUrl, List<AddSubtitleDto> requestDto) {
        Project project = projectRepository.findBySrcUrlAndAdmin(projectSrcUrl, user)
                .orElseThrow(() -> {
                    throw new BusinessException(HttpStatus.FORBIDDEN,
                            "You have to create a project with this video first.");
                });

        if (subtitleRepository.countByProjectSrcUrl(projectSrcUrl) > 0l) {
            throw new BusinessException(HttpStatus.FORBIDDEN, "This video already has initial subtitles");
        }

        List<Subtitle> subtitles = requestDto.stream().map((dto) -> {
            return new Subtitle(user, project.getSrcUrl(), project.getSrcLangCode(), dto.getStart(),
                    dto.getEnd(), dto.getSrcText());
        }).toList();

        return subtitleRepository.saveAll(subtitles);
    }

    @Transactional
    public List<Subtitle> creatorUpdateSubtitles(User user, String projectSrcUrl, List<UpdateSubtitleDto> requestDto) {
        Project project = projectRepository.findBySrcUrlAndAdmin(projectSrcUrl, user)
                .orElseThrow(() -> {
                    throw new BusinessException(HttpStatus.FORBIDDEN, ErrorMessage.NOT_PROJECT_ADMIN);
                });

        String srcLangCode = project.getSrcLangCode();

        List<Subtitle> updated = new ArrayList<>();
        for (UpdateSubtitleDto dto : requestDto) {
            Long subtitleId = dto.getId();

            Subtitle subtitle = subtitleRepository.findById(subtitleId).orElse(null);
            if (subtitle == null || !subtitle.getProjectSrcUrl().equals(projectSrcUrl)) {
                continue;
            }

            if (subtitle.isCreator(user)) {
                subtitle.creatorUpdate(user, dto, srcLangCode);
                updated.add(subtitle);
            }
        }
        return subtitleRepository.saveAll(updated);
    }

    @Transactional
    public List<Subtitle> creatorHardDeleteSubtitles(User user, List<Long> ids) {
        List<Subtitle> deleted = new ArrayList<>();
        List<Subtitle> subtitles = subtitleRepository.findAllById(ids);
        for (Subtitle subtitle : subtitles) {
            if (subtitle.creatorCanDelete(user)) {
                deleted.add(subtitle);
            }
        }
        subtitleRepository.deleteAll(deleted);
        return deleted;
    }

    @Transactional
    public List<Subtitle> updateDesTexts(User user, String projectSrcUrl, String projectDesLangCode,
            List<UpdateDesTextDto> requestDto) {
        Project project = projectRepository.findBySrcUrlAndDesLangCode(projectSrcUrl, projectDesLangCode)
                .orElseThrow(() -> {
                    throw new BusinessException(HttpStatus.BAD_REQUEST, ErrorMessage.PROJECT_NOT_FOUND);
                });

        if (!translatorService.isTranslator(project, user)) {
            throw new BusinessException(HttpStatus.FORBIDDEN, "You are not a translator of this project");
        }

        List<Subtitle> updated = new ArrayList<>();
        for (UpdateDesTextDto dto : requestDto) {
            Long subtitleId = dto.getId();

            Subtitle subtitle = subtitleRepository.findById(subtitleId).orElse(null);
            if (subtitle == null || !subtitle.getProjectSrcUrl().equals(projectSrcUrl)) {
                continue;
            }

            subtitle.updateDesText(user, dto, projectDesLangCode);
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
