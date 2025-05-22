package com.ndinhchien.m4y.domain.auth.handler;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.ndinhchien.m4y.global.util.ResponseUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(
            @NotNull HttpServletRequest request,
            @NotNull HttpServletResponse response,
            @NotNull AuthenticationException authException) {

        ResponseUtils.fail(response, HttpStatus.UNAUTHORIZED, authException.getMessage());
    }
}
