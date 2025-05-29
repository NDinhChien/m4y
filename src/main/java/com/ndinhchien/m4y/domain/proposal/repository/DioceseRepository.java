package com.ndinhchien.m4y.domain.proposal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ndinhchien.m4y.domain.proposal.dto.ProposalResponseDto.IDiocese;
import com.ndinhchien.m4y.domain.proposal.entity.Diocese;

public interface DioceseRepository extends JpaRepository<Diocese, String> {

    boolean existsByName(String name);

    Optional<Diocese> findByName(String name);

    List<IDiocese> findAllByIsApproved(Boolean isApproved);

    long deleteByName(String name);

}
