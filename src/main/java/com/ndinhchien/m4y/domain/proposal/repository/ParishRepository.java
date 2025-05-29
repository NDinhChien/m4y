package com.ndinhchien.m4y.domain.proposal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ndinhchien.m4y.domain.proposal.dto.ProposalResponseDto.IParish;
import com.ndinhchien.m4y.domain.proposal.entity.Parish;

public interface ParishRepository extends JpaRepository<Parish, String> {
    boolean existsByName(String name);

    Optional<Parish> findByName(String name);

    List<IParish> findAllByIsApproved(Boolean isApproved);

    long deleteByName(String name);
}