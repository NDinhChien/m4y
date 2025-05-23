package com.ndinhchien.m4y.domain.address.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.ndinhchien.m4y.domain.address.entity.Language;
import com.ndinhchien.m4y.domain.address.repository.LanguageRepository;
import com.ndinhchien.m4y.global.exception.BusinessException;
import com.ndinhchien.m4y.global.exception.ErrorMessage;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class LanguageService {

    private final LanguageRepository languageRepository;

    public boolean existsByCode(String langCode) {
        return languageRepository.existsByCode(langCode);
    }

    public Language validateLanguageByCode(@NotNull String langCode) {

        return languageRepository.findByCode(langCode).orElseThrow(() -> {

            throw new BusinessException(HttpStatus.BAD_REQUEST, ErrorMessage.LANGUAGE_NOT_FOUND);
        });
    }

    public Language validateLanguage(@NotNull String langName) {

        return languageRepository.findByName(langName).orElseThrow(() -> {

            throw new BusinessException(HttpStatus.BAD_REQUEST, ErrorMessage.LANGUAGE_NOT_FOUND);
        });
    }

    public Language save(Language language) {
        return languageRepository.save(language);
    }
}
