package com.ndinhchien.m4y.global.websocket.exception;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ndinhchien.m4y.global.dto.BaseResponse;
import com.ndinhchien.m4y.global.service.MessageManager;
import com.ndinhchien.m4y.global.websocket.MessageDestination;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice
public class GlobalMessageExceptionHandler {

    private final MessageManager messageManager;

    @Autowired
    private ObjectMapper objectMapper;

    @MessageExceptionHandler(Exception.class)
    public void CustomExceptionHandler(Exception e, Principal principal) {
        handleException(e, principal);
    }

    private void handleException(
            Exception e, Principal principal) {
        String errorResponseJSON = null;
        try {
            BaseResponse<?> response = BaseResponse.error(HttpStatus.BAD_REQUEST, e.getMessage());
            errorResponseJSON = objectMapper.writeValueAsString(response);
            log.error("error response: " + errorResponseJSON);
        } catch (Exception ex) {
            errorResponseJSON = "Failed to perform action, consider try again later";
        }

        messageManager.sendToUser(principal.getName(), MessageDestination.PRIVATE_ERROR, errorResponseJSON);
    }
}