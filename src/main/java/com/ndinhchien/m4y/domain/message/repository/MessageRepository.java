package com.ndinhchien.m4y.domain.message.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.ndinhchien.m4y.domain.message.dto.MessageResponseDto.IMessage;
import com.ndinhchien.m4y.domain.message.entity.Message;

public interface MessageRepository extends JpaRepository<Message, Long> {

    Page<IMessage> findAllBy(Pageable pageable);

}
