package com.ndinhchien.m4y.domain.auth.handler;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import com.ndinhchien.m4y.global.util.ResponseUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@NoArgsConstructor
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(
            @NotNull HttpServletRequest request,
            @NotNull HttpServletResponse response,
            @NotNull AccessDeniedException accessDeniedException) throws IOException {

        ResponseUtils.fail(response, HttpStatus.FORBIDDEN);
    }
}