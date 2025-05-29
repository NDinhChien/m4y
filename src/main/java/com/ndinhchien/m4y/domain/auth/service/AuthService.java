package com.ndinhchien.m4y.domain.auth.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ndinhchien.m4y.domain.auth.dto.AuthRequestDto.LoginRequestDto;
import com.ndinhchien.m4y.domain.auth.dto.AuthRequestDto.RegisterRequestDto;
import com.ndinhchien.m4y.domain.auth.dto.AuthRequestDto.UpdatePasswordDto;
import com.ndinhchien.m4y.domain.auth.dto.AuthResponseDto.JwtResponseDto;
import com.ndinhchien.m4y.domain.auth.strategy.GoogleLoginStrategy;
import com.ndinhchien.m4y.domain.user.entity.User;
import com.ndinhchien.m4y.domain.user.repository.UserRepository;
import com.ndinhchien.m4y.global.email.EmailService;
import com.ndinhchien.m4y.global.exception.BusinessException;
import com.ndinhchien.m4y.global.exception.ErrorMessage;

import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthService {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final EmailService emailService;

    @Value("${account.verification.required}")
    private boolean verificationRequired;

    @Transactional
    public User register(RegisterRequestDto requestDto) {
        String password = requestDto.getPassword();
        String email = requestDto.getEmail();
        String userName = requestDto.getUserName();

        if (!password.equals(requestDto.getConfirmPassword())) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, ErrorMessage.PASSWORD_MISMATCH);
        }
        if (userRepository.existsByEmail(email)) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, ErrorMessage.EMAIL_REGISTERED);
        }
        if (userRepository.existsByUserName(userName)) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, ErrorMessage.USERNAME_EXISTED);
        }
        User user = User.builder()
                .userName(userName)
                .email(email)
                .password(password)
                .build();
        if (verificationRequired == true) {
            emailService.sendAccountVerification(user, userName);
        } else {
            user.updateIsVerified();
        }

        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public JwtResponseDto login(LoginRequestDto requestDto, HttpServletResponse response) {
        String email = requestDto.getEmail();
        String password = requestDto.getPassword();

        User user = validateUser(email);

        if (!user.getIsVerified()) {
            throw new BusinessException(HttpStatus.UNAUTHORIZED, ErrorMessage.UNVERIFIED_ACCOUNT);
        }
        if (!user.isPasswordCorrect(password)) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, ErrorMessage.INCORRECT_PASSWORD);
        }

        return jwtService.addTokenToResponse(user, response);

    }

    @Transactional
    public void logout(@Nullable User user, HttpServletResponse response) {
        jwtService.removeTokenFromResponse(user, response);
    }

    @Transactional
    public boolean updatePassword(User user, UpdatePasswordDto dto) {
        String oldPassword = dto.getOldPassword();
        String newPassword = dto.getNewPassword();
        String confirmPassword = dto.getConfirmPassword();

        if (!user.isPasswordCorrect(oldPassword)) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, ErrorMessage.INCORRECT_PASSWORD);
        }
        if (!newPassword.equals(confirmPassword)) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, ErrorMessage.PASSWORD_MISMATCH);
        }
        user.setPassword(newPassword);
        userRepository.save(user);
        return true;
    }

    @Transactional(readOnly = true)
    public boolean requestAccountVerification(String email) {
        User user = validateUser(email);
        if (user.getIsVerified() == true) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, ErrorMessage.ACCOUNT_VERIFIED);
        }
        String token = jwtService.createVerifyToken(user);
        emailService.sendAccountVerification(user, token);
        return true;
    }

    @Transactional(readOnly = true)
    public boolean requestPasswordReset(String email) {
        User user = validateUser(email);
        String token = jwtService.createResetToken(user);
        emailService.sendPasswordReset(user, token);
        return true;
    }

    @Transactional
    public boolean verifyAccount(String token) {
        User user = jwtService.validateToken(token, "verify");
        user.updateIsVerified();
        userRepository.save(user);
        return true;
    }

    @Transactional
    public boolean resetPassword(String token, String newPassword) {
        User user = jwtService.validateToken(token, "reset");
        user.setPassword(newPassword);
        userRepository.save(user);
        return true;
    }

    private User validateUser(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> {
            return new BusinessException(HttpStatus.BAD_REQUEST, ErrorMessage.USER_NOT_FOUND);
        });
    }
}