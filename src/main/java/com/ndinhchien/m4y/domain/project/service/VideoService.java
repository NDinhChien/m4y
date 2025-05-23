package com.ndinhchien.m4y.domain.project.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.ndinhchien.m4y.domain.project.entity.Video;
import com.ndinhchien.m4y.domain.project.repository.VideoRepository;
import com.ndinhchien.m4y.global.exception.BusinessException;
import com.ndinhchien.m4y.global.exception.ErrorMessage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class VideoService {

    private final VideoRepository videoRepository;

    public Video findByUrl(String videoUrl) {
        return videoRepository.findByUrl(videoUrl).orElse(null);
    }

    public Video save(Video video) {
        return videoRepository.save(video);
    }

    public Video validateVideo(String videoUrl) {
        return videoRepository.findByUrl(videoUrl).orElseThrow(() -> {
            throw new BusinessException(HttpStatus.BAD_REQUEST, ErrorMessage.VIDEO_NOT_FOUND);
        });
    }
}
