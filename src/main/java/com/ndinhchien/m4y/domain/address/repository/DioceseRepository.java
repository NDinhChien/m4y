package com.ndinhchien.m4y.domain.address.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ndinhchien.m4y.domain.address.dto.AddressResponseDto.IDioceseDto;
import com.ndinhchien.m4y.domain.address.entity.Diocese;

public interface DioceseRepository extends JpaRepository<Diocese, String> {

    boolean existsByName(String name);

    Optional<Diocese> findByName(String name);

    List<IDioceseDto> findAllByIsApproved(Boolean isApproved);

    long deleteByName(String name);

}
