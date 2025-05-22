package com.ndinhchien.m4y.domain.auth.filter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.catalina.connector.Response;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.ndinhchien.m4y.domain.auth.service.JwtService;
import com.ndinhchien.m4y.domain.auth.type.UserDetailsImpl;
import com.ndinhchien.m4y.domain.user.entity.User;
import com.ndinhchien.m4y.global.exception.ErrorMessage;
import com.ndinhchien.m4y.global.util.ResponseUtils;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    private List<String> excludedPaths = Arrays.asList(
            "/api/v1/auth/login", "/api/v1/auth/register",
            "/api/v1/auth/request/verify", "/api/v1/auth/verify",
            "^/swagger-ui.*$", "^/v3/api-docs.*$",
            "^/ws.*$");

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();

        return excludedPaths.stream().anyMatch(excludedPath -> path.matches(excludedPath));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String accessToken = jwtService.getAccessTokenFromRequest(request);
        if (!StringUtils.hasText(accessToken)) {
            filterChain.doFilter(request, response);
            return;
        }

        User user;
        try {
            user = jwtService.validateToken(accessToken, "access");
        } catch (Exception e) {
            ResponseUtils.fail(response, HttpStatus.UNAUTHORIZED, e.getMessage());
            return;
        }

        if (!user.getIsVerified()) {
            ResponseUtils.fail(response, HttpStatus.UNAUTHORIZED, ErrorMessage.UNVERIFIED_ACCOUNT);
            return;
        }

        if (user.getIsBanned()) {
            ResponseUtils.fail(response, HttpStatus.FORBIDDEN, ErrorMessage.BANNED_ACCOUNT);
            return;
        }

        setAuthentication(user);
        filterChain.doFilter(request, response);
    }

    public void setAuthentication(User user) {
        Authentication authentication = createAuthentication(user);

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
    }

    private Authentication createAuthentication(User user) {
        UserDetails userDetails = new UserDetailsImpl(user);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

}
