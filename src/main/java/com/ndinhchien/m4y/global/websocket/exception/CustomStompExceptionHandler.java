package com.ndinhchien.m4y.global.websocket.exception;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ndinhchien.m4y.global.dto.BaseResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CustomStompExceptionHandler extends StompSubProtocolErrorHandler {
    private static byte[] EMPTY_PAYLOAD = new byte[0];

    @Autowired
    private ObjectMapper objectMapper;

    public CustomStompExceptionHandler() {
        super();
    }

    @Override
    public Message<byte[]> handleClientMessageProcessingError(Message<byte[]> clientMessage, Throwable ex) {
        log.info("ClientMessageProcessingError: {}", ex.getMessage());

        Throwable exception = converterThrowException(ex);

        if (exception != null) {
            return handleStompException(clientMessage, exception.getMessage());
        }
        return super.handleClientMessageProcessingError(clientMessage, ex);
    }

    private Throwable converterThrowException(Throwable exception) {
        if (exception instanceof MessageDeliveryException) {
            return exception.getCause();
        }
        return exception;
    }

    private Message<byte[]> handleStompException(Message<byte[]> clientMessage, String errorMessage) {
        // SET response dto
        BaseResponse<?> errorResponse = BaseResponse.error(HttpStatus.BAD_REQUEST, errorMessage);

        // SET Header
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.ERROR);
        accessor.setLeaveMutable(true);
        accessor.setContentType(MimeTypeUtils.APPLICATION_JSON);

        setReceiptIdForClient(clientMessage, accessor);

        String errorResponseJSON = null;
        try {
            errorResponseJSON = objectMapper.writeValueAsString(errorResponse);
        } catch (JsonProcessingException e) {
        }

        return MessageBuilder.createMessage(
                errorResponseJSON != null ? errorResponseJSON.getBytes(StandardCharsets.UTF_8) : EMPTY_PAYLOAD,
                accessor.getMessageHeaders());
    }

    private void setReceiptIdForClient(Message<byte[]> clientMessage, StompHeaderAccessor accessor) {

        if (Objects.isNull(clientMessage)) {
            return;
        }

        StompHeaderAccessor clientHeaderAccessor = MessageHeaderAccessor.getAccessor(clientMessage,
                StompHeaderAccessor.class);

        String receiptId = Objects.isNull(clientHeaderAccessor) ? null : clientHeaderAccessor.getReceipt();

        if (receiptId != null) {
            accessor.setReceiptId(receiptId);
        }
    }
}