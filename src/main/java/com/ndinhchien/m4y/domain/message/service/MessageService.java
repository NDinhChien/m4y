package com.ndinhchien.m4y.domain.message.service;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ndinhchien.m4y.domain.message.dto.MessageRequestDto.SendMessageDto;
import com.ndinhchien.m4y.domain.message.dto.MessageRequestDto.UpdateMessageDto;
import com.ndinhchien.m4y.domain.message.dto.MessageResponseDto.IMessage;
import com.ndinhchien.m4y.domain.message.entity.Message;
import com.ndinhchien.m4y.domain.message.repository.MessageRepository;
import com.ndinhchien.m4y.domain.user.entity.User;
import com.ndinhchien.m4y.global.exception.BusinessException;
import com.ndinhchien.m4y.global.exception.ErrorMessage;
import com.ndinhchien.m4y.global.websocket.MessageDestination;
import com.ndinhchien.m4y.global.websocket.MessageManager;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Order(value = Ordered.HIGHEST_PRECEDENCE + 199)
@RequiredArgsConstructor
@Service
public class MessageService {

    private static final int PAGE_SIZE = 12;

    private final MessageRepository messageRepository;
    private final MessageManager messageManager;

    @Transactional(readOnly = true)
    public Page<IMessage> getMessages(int pageNumber) {
        Sort sortDetail = Sort.by(Direction.DESC, "createdAt");
        Pageable pageDetail = PageRequest.of(Math.max(0, pageNumber), PAGE_SIZE, sortDetail);

        return messageRepository.findAllBy(pageDetail);
    }

    @Transactional
    public Message sendMessage(User user, SendMessageDto dto) {
        String content = dto.getContent();
        Message message = messageRepository.save(new Message(user, content));

        messageManager.sendToGlobal(MessageDestination.GLOBAL_MESSAGE, message);
        return message;
    }

    @Transactional
    public Message updateMessage(User user, UpdateMessageDto dto) {
        Long messageId = dto.getMessageId();
        Message message = validateMessage(messageId);
        if (!message.isAuthor(user)) {
            throw new BusinessException(HttpStatus.FORBIDDEN, "You are not author of this message");
        }
        message.update(dto);
        message = messageRepository.save(message);
        messageManager.sendToGlobal(MessageDestination.GLOBAL_MESSAGE, message);
        return message;
    }

    public Message validateMessage(Long messageId) {
        return messageRepository.findById(messageId).orElseThrow(() -> {
            throw new BusinessException(HttpStatus.BAD_REQUEST, ErrorMessage.MESSAGE_NOT_FOUND);
        });
    }
}
