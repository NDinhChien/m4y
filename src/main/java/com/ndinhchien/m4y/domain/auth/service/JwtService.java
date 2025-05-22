package com.ndinhchien.m4y.domain.auth.service;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.util.WebUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ndinhchien.m4y.domain.auth.dto.AuthResponseDto.JwtResponseDto;
import com.ndinhchien.m4y.domain.auth.type.JwtPayload;
import com.ndinhchien.m4y.domain.user.entity.User;
import com.ndinhchien.m4y.domain.user.repository.UserRepository;
import com.ndinhchien.m4y.global.exception.ErrorMessage;
import com.ndinhchien.m4y.global.exception.TokenException;
import com.ndinhchien.m4y.global.service.EnvironmentService;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.annotation.Nullable;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtService {

    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";

    private static final long ACCESS_TOKEN_TIME = 24 * 60 * 60 * 1000L;
    private static final long REFRESH_TOKEN_TIME = 7 * 24 * 60 * 60 * 1000L;
    private static final long RESET_TOKEN_TIME = 15 * 60 * 1000L;
    private static final long VERIFY_TOKEN_TIME = 15 * 60 * 1000L;

    @Value("${spring.security.jwt.secret}")
    private String secret;

    private final UserRepository userRepository;

    private final EnvironmentService environment;

    private SecretKey getDynamicSecretKey(User user) {
        String combined = this.secret + user.getTokenSecret();
        return new SecretKeySpec(combined.getBytes(StandardCharsets.UTF_8),
                Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    public User validateToken(String token, String desireType) throws TokenException {
        JwtPayload jwtPayload = decodeJwtPayload(token);
        if (!jwtPayload.getType().equals(desireType)) {
            throw new TokenException(String.format("Invalid %s token", desireType));
        }

        String userEmail = jwtPayload.getEmail();
        User user = validateUser(userEmail);
        validateToken(token, desireType, user);
        return user;
    }

    private void validateToken(String token, String desireType, User user) throws TokenException {
        try {
            Jwts.parser()
                    .verifyWith(getDynamicSecretKey(user))
                    .build()
                    .parseSignedClaims(token);
        } catch (MalformedJwtException e) {
            throw new TokenException(String.format("Invalid JWT token: {}", e.getMessage()));
        } catch (ExpiredJwtException e) {
            throw new TokenException(String.format("JWT token is expired: {}", e.getMessage()));
        } catch (UnsupportedJwtException e) {
            throw new TokenException(String.format("JWT token is unsupported: {}", e.getMessage()));
        } catch (IllegalArgumentException e) {
            throw new TokenException(String.format("JWT claims string is empty: {}", e.getMessage()));
        } catch (Exception e) {
            throw new TokenException(String.format("Invalid JWT token"));
        }
    }

    private JwtPayload decodeJwtPayload(String jwtToken) throws TokenException {
        try {
            String[] parts = jwtToken.split("\\.");
            if (parts.length != 3) {
                throw new Exception();
            }
            String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]));
            JsonNode payload = new ObjectMapper().readTree(payloadJson);
            String type = payload.get("type").asText();
            String email = payload.get("email").asText();
            String role = payload.get("role").asText();
            return new JwtPayload(type, email, role);
        } catch (Exception e) {
            throw new TokenException("Invalid JWT token");
        }
    }

    public String createAccessToken(User user) {
        return createToken(user, "access");
    }

    public String createRefreshToken(User user) {
        return createToken(user, "refresh");
    }

    public String createResetToken(User user) {
        return createToken(user, "reset");
    }

    public String createVerifyToken(User user) {
        return createToken(user, "verify");
    }

    private String createToken(User user, String type) {
        long duration = 0;
        switch (type) {
            case "access":
                duration = ACCESS_TOKEN_TIME;
                break;
            case "refresh":
                duration = REFRESH_TOKEN_TIME;
                break;
            case "reset":
                duration = RESET_TOKEN_TIME;
                break;
            case "verify":
                duration = VERIFY_TOKEN_TIME;
                break;
            default:
                log.error("Invalid token type {}", type);
                break;
        }

        return Jwts.builder()
                .claim("type", type)
                .claim("email", user.getEmail())
                .claim("role", user.getRole())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + duration))
                .signWith(getDynamicSecretKey(user))
                .compact();
    }

    public JwtResponseDto addTokenToResponse(User user, HttpServletResponse response) {

        String accessToken = createAccessToken(user);
        String refreshToken = createRefreshToken(user);
        addAccessTokenToCookie(accessToken, response);
        return new JwtResponseDto(accessToken, refreshToken);
    }

    public void removeTokenFromResponse(@Nullable User user, HttpServletResponse response) {
        if (user != null) {
            user.logout();
            userRepository.save(user);
        }
        removeAccessTokenFromCookie(response);
    }

    public String getAccessTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);

        String token = getAccessTokenFromHeader(bearerToken);
        if (!StringUtils.hasText(token)) {
            token = getAccesTokenFromCookie(request);
        }
        return token;
    }

    private String getAccesTokenFromCookie(HttpServletRequest request) {
        var cookie = WebUtils.getCookie(request, AUTHORIZATION_HEADER);
        if (cookie != null) {
            return URLDecoder.decode(cookie.getValue(), StandardCharsets.UTF_8);
        }
        return null;
    }

    public String getAccessTokenFromHeader(String bearerToken) {
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }
        return null;
    }

    private void addAccessTokenToCookie(String token, HttpServletResponse response) {
        token = URLEncoder.encode(token, StandardCharsets.UTF_8);

        boolean isProd = environment.isProdEnv();
        Cookie cookie = new Cookie(AUTHORIZATION_HEADER, token);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setAttribute("SameSite", isProd ? "None" : "Lax");
        cookie.setMaxAge((int) (ACCESS_TOKEN_TIME / 1000));
        cookie.setSecure(isProd);
        response.addCookie(cookie);
    }

    private void removeAccessTokenFromCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie(AUTHORIZATION_HEADER, null);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

    private User validateUser(String email) throws TokenException {
        return userRepository.findByEmail(email).orElseThrow(
                () -> {
                    return new TokenException(ErrorMessage.USER_NOT_FOUND);
                });
    }

}
