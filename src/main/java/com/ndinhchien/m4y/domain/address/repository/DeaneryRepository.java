package com.ndinhchien.m4y.domain.address.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ndinhchien.m4y.domain.address.dto.AddressResponseDto.IDeaneryDto;
import com.ndinhchien.m4y.domain.address.entity.Deanery;

public interface DeaneryRepository extends JpaRepository<Deanery, String> {

    boolean existsByName(String name);

    Optional<Deanery> findByName(String name);

    List<IDeaneryDto> findAllByIsApproved(Boolean isApproved);

    long deleteByName(String name);

}