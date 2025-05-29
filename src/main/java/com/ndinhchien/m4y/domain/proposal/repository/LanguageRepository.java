package com.ndinhchien.m4y.domain.proposal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ndinhchien.m4y.domain.proposal.dto.ProposalResponseDto.ILanguage;
import com.ndinhchien.m4y.domain.proposal.entity.Language;

public interface LanguageRepository extends JpaRepository<Language, String> {

    boolean existsByName(String name);

    boolean existsByCode(String code);

    Optional<Language> findByName(String name);

    Optional<Language> findByCode(String code);

    List<ILanguage> findAllByIsApproved(Boolean isApproved);

    long deleteByName(String name);

}