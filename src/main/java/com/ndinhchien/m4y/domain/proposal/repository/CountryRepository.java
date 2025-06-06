package com.ndinhchien.m4y.domain.proposal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ndinhchien.m4y.domain.proposal.dto.ProposalResponseDto.ICountry;
import com.ndinhchien.m4y.domain.proposal.entity.Country;

public interface CountryRepository extends JpaRepository<Country, String> {

    boolean existsByName(String name);

    Optional<Country> findByName(String name);

    List<ICountry> findAllByIsApproved(Boolean isApproved);

    long deleteByName(String name);

}
