package com.ndinhchien.m4y.domain.auth.strategy;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ndinhchien.m4y.domain.auth.dto.AuthResponseDto.JwtResponseDto;
import com.ndinhchien.m4y.domain.auth.type.SociaInfo;
import com.ndinhchien.m4y.domain.user.entity.User;

import jakarta.servlet.http.HttpServletResponse;

public interface ISocialLoginStrategy {

    JwtResponseDto socialLogin(String code, HttpServletResponse response) throws JsonProcessingException;

    String getToken(String code) throws JsonProcessingException;

    SociaInfo getSocialInfo(String token) throws JsonProcessingException;

    User registerUserIfNeeded(SociaInfo socialInfo);
}