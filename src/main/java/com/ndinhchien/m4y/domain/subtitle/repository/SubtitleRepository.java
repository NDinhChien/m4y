package com.ndinhchien.m4y.domain.subtitle.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ndinhchien.m4y.domain.subtitle.dto.SubtitleResponseDto.ISubtitle;
import com.ndinhchien.m4y.domain.subtitle.entity.Subtitle;

public interface SubtitleRepository extends JpaRepository<Subtitle, Long> {

    long countByProjectSrcUrl(String projectSrcUrl);

    List<ISubtitle> findAllByProjectSrcUrl(String projectSrcUrl);
}
