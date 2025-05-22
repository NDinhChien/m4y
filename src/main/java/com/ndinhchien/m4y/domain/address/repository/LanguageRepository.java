package com.ndinhchien.m4y.domain.address.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ndinhchien.m4y.domain.address.dto.AddressResponseDto.ILanguageDto;
import com.ndinhchien.m4y.domain.address.entity.Language;

public interface LanguageRepository extends JpaRepository<Language, String> {

    boolean existsByName(String name);

    boolean existsByCode(String code);

    Optional<Language> findByName(String name);

    Optional<Language> findByCode(String code);

    List<ILanguageDto> findAllByIsApproved(Boolean isApproved);

    long deleteByName(String name);

}