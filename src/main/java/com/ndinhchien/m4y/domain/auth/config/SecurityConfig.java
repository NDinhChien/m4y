package com.ndinhchien.m4y.domain.auth.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.ndinhchien.m4y.domain.auth.filter.JwtAuthorizationFilter;
import com.ndinhchien.m4y.domain.auth.handler.CustomAccessDeniedHandler;
import com.ndinhchien.m4y.domain.auth.handler.CustomAuthenticationEntryPoint;
import com.ndinhchien.m4y.domain.auth.service.JwtService;
import com.ndinhchien.m4y.domain.user.type.UserRole;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
public class SecurityConfig {

    private final JwtService jwtService;
    private final CustomAccessDeniedHandler accessDeniedHandler;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;

    @Value("${spring.security.cors.allowed-origins}")
    private String[] allowedOrigins;

    @Value("${spring.security.cors.allowed-methods}")
    private String[] allowedMethods;

    @Value("${spring.security.cors.allowed-headers}")
    private String[] allowedHeaders;

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    JwtAuthorizationFilter jwtAuthorizationFilter() {
        return new JwtAuthorizationFilter(jwtService);
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()));
        http.csrf(AbstractHttpConfigurer::disable);
        http.formLogin(AbstractHttpConfigurer::disable);
        http.httpBasic(AbstractHttpConfigurer::disable);
        http.headers(header -> header.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin));
        http.sessionManagement(
                sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.authorizeHttpRequests(authorizeHttpRequests -> authorizeHttpRequests
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()

                .requestMatchers("/api/v1/auth/password").authenticated()
                .requestMatchers("/api/v1/auth/**").permitAll()

                .requestMatchers("/api/v1/user/id/**", "/api/v1/user/search").permitAll()
                .requestMatchers("/api/v1/user/**").authenticated()

                .requestMatchers("/api/v1/proposal/all").permitAll()
                .requestMatchers("/api/v1/proposal/sys/**").hasAuthority(UserRole.ADMIN.name())
                .requestMatchers("/api/v1/proposal/**").authenticated()

                .requestMatchers("/api/v1/comment/project").permitAll()
                .requestMatchers("/api/v1/comment/**").authenticated()

                .requestMatchers("/api/v1/reaction/**").authenticated()

                .requestMatchers(HttpMethod.GET, "/api/v1/subtitle").permitAll()
                .requestMatchers("/api/v1/subtitle/**").authenticated()

                .requestMatchers(HttpMethod.GET, "/api/v1/message/global", "/api/v1/message/online").permitAll()
                .requestMatchers("/api/v1/message/**").authenticated()

                .requestMatchers("/api/v1/notification/**").authenticated()

                .requestMatchers("/api/v1/project/id/**", "/api/v1/project/channel/**", "/api/v1/project/search")
                .permitAll()
                .requestMatchers("/api/v1/project/**").authenticated()

                .requestMatchers("/api/v1/request/**").authenticated()

                .requestMatchers("/ws/**").permitAll()
                .requestMatchers("/topic/**", "/queue/**").permitAll()
                .anyRequest().authenticated());

        http.addFilterBefore(jwtAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);
        http.exceptionHandling(ex -> ex
                .authenticationEntryPoint(this.authenticationEntryPoint)
                .accessDeniedHandler(this.accessDeniedHandler));

        return http.build();
    }

    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(allowedOrigins));
        configuration.setAllowedMethods(Arrays.asList(allowedMethods));
        configuration.setAllowedHeaders(Arrays.asList(allowedHeaders));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
