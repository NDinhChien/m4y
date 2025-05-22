package com.ndinhchien.m4y.domain.address.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ndinhchien.m4y.domain.address.dto.AddressResponseDto.IParishDto;
import com.ndinhchien.m4y.domain.address.entity.Parish;

public interface ParishRepository extends JpaRepository<Parish, String> {
    boolean existsByName(String name);

    Optional<Parish> findByName(String name);

    List<IParishDto> findAllByIsApproved(Boolean isApproved);

    long deleteByName(String name);
}