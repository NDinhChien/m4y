package com.ndinhchien.m4y.domain.auth.strategy;

import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ndinhchien.m4y.domain.auth.dto.AuthResponseDto.JwtResponseDto;
import com.ndinhchien.m4y.domain.auth.service.JwtService;
import com.ndinhchien.m4y.domain.auth.type.SociaInfo;
import com.ndinhchien.m4y.domain.auth.type.SocialType;
import com.ndinhchien.m4y.domain.user.entity.User;
import com.ndinhchien.m4y.domain.user.repository.UserRepository;
import com.ndinhchien.m4y.global.exception.BusinessException;
import com.ndinhchien.m4y.global.util.CommonUtils;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoogleLoginStrategy implements ISocialLoginStrategy {

    private final UserRepository userRepository;
    private final RestTemplate restTemplate;
    private final JwtService jwtService;
    private final ObjectMapper objectMapper;

    @Value("${spring.security.google.client-id}")
    private String googleClientId;
    @Value("${spring.security.google.client-secret}")
    private String googleClientSecret;
    @Value("${spring.security.google.redirect-uri}")
    private String googleRedirectUri;

    @Transactional
    @Override
    public JwtResponseDto socialLogin(String code, HttpServletResponse response) {
        try {
            String token = getToken(code);
            SociaInfo socialInfo = getSocialInfo(token);
            User googleUser = registerUserIfNeeded(socialInfo);
            return this.jwtService.addTokenToResponse(googleUser, response);
        } catch (Exception e) {
            log.info("Failed to do social login ", e);
            throw new BusinessException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public String getToken(String code) throws JsonProcessingException {
        URI uri = UriComponentsBuilder
                .fromUriString("https://oauth2.googleapis.com/token")
                .encode()
                .build()
                .toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", googleClientId);
        body.add("client_secret", googleClientSecret);
        body.add("redirect_uri", googleRedirectUri);
        body.add("code", code);

        RequestEntity<MultiValueMap<String, String>> requestEntity = RequestEntity
                .post(uri)
                .headers(headers)
                .body(body);

        ResponseEntity<String> response = restTemplate.exchange(
                requestEntity,
                String.class);

        JsonNode jsonNode = objectMapper.readTree(response.getBody());
        return jsonNode.get("access_token").asText();
    }

    @Override
    public SociaInfo getSocialInfo(String token) throws JsonProcessingException {
        URI uri = UriComponentsBuilder
                .fromUriString("https://www.googleapis.com/oauth2/v2/userinfo")
                .queryParam("access_token", token)
                .encode()
                .build()
                .toUri();

        ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);

        JsonNode jsonNode = new ObjectMapper().readTree(response.getBody());
        String id = jsonNode.get("id").asText();
        String email = jsonNode.get("email").asText();
        String avatar = jsonNode.get("picture").asText();

        return new SociaInfo(id, email, avatar);
    }

    @Override
    public User registerUserIfNeeded(SociaInfo socialInfo) {
        String googleId = socialInfo.getId();
        String googleEmail = socialInfo.getEmail();

        User user = userRepository.findBySocialIdAndSocialType(googleId, SocialType.GOOGLE)
                .orElse(null);
        if (user == null) {
            User userWithSameEmail = userRepository.findByEmail(googleEmail).orElse(null);
            if (userWithSameEmail != null) {
                user = userWithSameEmail;
                user.updateSocialInfo(googleId, SocialType.GOOGLE);
            } else {
                String defaultPassword = "Aa12345$";
                String defaultUserName = CommonUtils.generateUserName(15);

                user = User.builder()
                        .email(googleEmail)
                        .userName(defaultUserName)
                        .password(defaultPassword)
                        .socialId(googleId)
                        .socialType(SocialType.GOOGLE)
                        .avatar(socialInfo.getAvatar())
                        .build();
            }
        }
        if (user != null) {
            user = userRepository.save(user);
        }
        return user;
    }

}