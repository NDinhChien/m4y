package com.ndinhchien.m4y.domain.proposal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ndinhchien.m4y.domain.proposal.dto.ProposalResponseDto.IDeanery;
import com.ndinhchien.m4y.domain.proposal.entity.Deanery;

public interface DeaneryRepository extends JpaRepository<Deanery, String> {

    boolean existsByName(String name);

    Optional<Deanery> findByName(String name);

    List<IDeanery> findAllByIsApproved(Boolean isApproved);

    long deleteByName(String name);

}