package com.ndinhchien.m4y.domain.address.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ndinhchien.m4y.domain.address.dto.AddressResponseDto.ICountryDto;
import com.ndinhchien.m4y.domain.address.entity.Country;

public interface CountryRepository extends JpaRepository<Country, String> {

    boolean existsByName(String name);

    Optional<Country> findByName(String name);

    List<ICountryDto> findAllByIsApproved(Boolean isApproved);

    long deleteByName(String name);

}
