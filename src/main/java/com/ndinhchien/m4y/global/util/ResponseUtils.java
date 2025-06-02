package com.ndinhchien.m4y.global.util;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ndinhchien.m4y.global.dto.BaseResponse;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ResponseUtils {

    private static ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    private static void response(HttpServletResponse response, BaseResponse<?> responseDto) {
        try {

            String responseJson = mapper.writeValueAsString(responseDto);

            response.setContentType("application/json; charset=utf-8");
            response.setStatus(responseDto.getCode());
            response.getWriter().print(responseJson);
        } catch (Exception e) {
            log.error("Failed to response {}", e.getMessage());
        }
    }

    public static void success(HttpServletResponse response, Object data) {
        ResponseUtils.response(response, BaseResponse.success(data));
    }

    public static void fail(HttpServletResponse response, HttpStatus status) {
        ResponseUtils.response(response, BaseResponse.error(status));
    }

    public static void fail(HttpServletResponse response, HttpStatus status, String message) {
        ResponseUtils.response(response, BaseResponse.error(status, message));
    }
}
