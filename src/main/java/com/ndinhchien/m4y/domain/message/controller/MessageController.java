package com.ndinhchien.m4y.domain.message.controller;

import java.util.Collection;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ndinhchien.m4y.domain.auth.type.UserDetailsImpl;
import com.ndinhchien.m4y.domain.message.dto.MessageRequestDto.SendMessageDto;
import com.ndinhchien.m4y.domain.message.dto.MessageRequestDto.UpdateMessageDto;
import com.ndinhchien.m4y.domain.message.dto.MessageResponseDto.IMessage;
import com.ndinhchien.m4y.domain.message.entity.Message;
import com.ndinhchien.m4y.domain.message.service.MessageService;
import com.ndinhchien.m4y.domain.user.entity.User;
import com.ndinhchien.m4y.global.dto.BaseResponse;
import com.ndinhchien.m4y.global.dto.PageDto;
import com.ndinhchien.m4y.global.service.MessageManager;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "message", description = "Message Related APIs")
@RequiredArgsConstructor
@RequestMapping("/api/v1/message")
@RestController
public class MessageController {

    private final MessageService messageService;
    private final MessageManager messageManager;

    @Operation(summary = "Get online users")
    @GetMapping("/online")
    public BaseResponse<Collection<User>> getOnlineUser() {
        return BaseResponse.success("Online users", messageManager.getOnlineUsers());
    }

    @Operation(summary = "Get global messages")
    @GetMapping("/global")
    public BaseResponse<PageDto<IMessage>> getGlobalMessages(
            @RequestParam Integer pageNumber) {
        Page<IMessage> page = messageService.getMessages(pageNumber);
        return BaseResponse.success("Messages", new PageDto<IMessage>(page));
    }

    @Operation(summary = "Send global message")
    @PostMapping("/global")
    public BaseResponse<Message> sendGlobalMessage(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody @Valid SendMessageDto requestDto) {
        return BaseResponse.success("Message sent!", messageService.sendMessage(userDetails.getUser(), requestDto));
    }

    @Operation(summary = "Update global message")
    @PutMapping("/global")
    public BaseResponse<?> updateMessage(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody @Valid UpdateMessageDto requestDto) {
        return BaseResponse.success("Message updated", messageService.updateMessage(userDetails.getUser(), requestDto));
    }
}
