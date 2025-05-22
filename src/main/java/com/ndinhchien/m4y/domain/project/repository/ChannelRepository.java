package com.ndinhchien.m4y.domain.project.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ndinhchien.m4y.domain.project.dto.ProjectResponseDto.IChannel;
import com.ndinhchien.m4y.domain.project.entity.Channel;

public interface ChannelRepository extends JpaRepository<Channel, Long> {

    Optional<Channel> findByUrl(String url);

    List<IChannel> findByNameContaining(String pattern);
}
