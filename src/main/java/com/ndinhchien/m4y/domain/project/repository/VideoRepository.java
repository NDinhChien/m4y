package com.ndinhchien.m4y.domain.project.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ndinhchien.m4y.domain.project.entity.Video;

public interface VideoRepository extends JpaRepository<Video, Long> {
    Optional<Video> findByUrl(String url);
}
