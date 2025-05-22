package com.ndinhchien.m4y.domain.address.service;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.ndinhchien.m4y.domain.address.dto.AddressRequestDto.AddAddressDto;
import com.ndinhchien.m4y.domain.address.dto.AddressRequestDto.DeleteAddressDto;
import com.ndinhchien.m4y.domain.address.dto.AddressRequestDto.UpdateAddressDto;
import com.ndinhchien.m4y.domain.address.dto.AddressResponseDto.ICountryDto;
import com.ndinhchien.m4y.domain.address.dto.AddressResponseDto.IDeaneryDto;
import com.ndinhchien.m4y.domain.address.dto.AddressResponseDto.IDioceseDto;
import com.ndinhchien.m4y.domain.address.dto.AddressResponseDto.ILanguageDto;
import com.ndinhchien.m4y.domain.address.dto.AddressResponseDto.IParishDto;
import com.ndinhchien.m4y.domain.address.entity.Country;
import com.ndinhchien.m4y.domain.address.entity.Deanery;
import com.ndinhchien.m4y.domain.address.entity.Diocese;
import com.ndinhchien.m4y.domain.address.entity.Language;
import com.ndinhchien.m4y.domain.address.entity.Parish;
import com.ndinhchien.m4y.domain.address.repository.CountryRepository;
import com.ndinhchien.m4y.domain.address.repository.DeaneryRepository;
import com.ndinhchien.m4y.domain.address.repository.DioceseRepository;
import com.ndinhchien.m4y.domain.address.repository.LanguageRepository;
import com.ndinhchien.m4y.domain.address.repository.ParishRepository;
import com.ndinhchien.m4y.domain.address.type.AddresssType;
import com.ndinhchien.m4y.domain.user.entity.User;
import com.ndinhchien.m4y.global.entity.Proposable;
import com.ndinhchien.m4y.global.exception.BusinessException;
import com.ndinhchien.m4y.global.exception.ErrorMessage;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class AddressService {

    private final LanguageRepository languageRepository;

    private final CountryRepository countryRepository;

    private final DioceseRepository dioceseRepository;

    private final DeaneryRepository deaneryRepository;

    private final ParishRepository parishRepository;

    @Transactional(readOnly = true)
    public Object getAddresses() {
        List<ILanguageDto> languages = languageRepository.findAllByIsApproved(true);
        List<ICountryDto> countries = countryRepository.findAllByIsApproved(true);
        List<IDioceseDto> dioceses = dioceseRepository.findAllByIsApproved(true);
        List<IDeaneryDto> deaneries = deaneryRepository.findAllByIsApproved(true);
        List<IParishDto> parishes = parishRepository.findAllByIsApproved(true);

        return Map.of("languages", languages, "countries", countries, "dioceses", dioceses, "deaneries", deaneries,
                "parishes", parishes);
    }

    @Transactional
    public Object addAddress(User proposer, AddAddressDto requestDto) {
        AddresssType type = requestDto.getType();
        String name = requestDto.getName();
        String countryName = requestDto.getCountryName();
        String dioceseName = requestDto.getDioceseName();
        String deaneryName = requestDto.getDeaneryName();
        String detailedAddress = requestDto.getDetailedAddress();

        if (type == AddresssType.DIOCESE && StringUtils.hasText(countryName)
                && !dioceseRepository.existsByName(name)) {
            Country country = validateCountry(countryName);
            return dioceseRepository.save(new Diocese(proposer, name, country));

        } else if (type == AddresssType.DEANERY && StringUtils.hasText(dioceseName)
                && !deaneryRepository.existsByName(name)) {
            Diocese diocese = validateDiocese(dioceseName);
            return deaneryRepository.save(new Deanery(proposer, name, diocese));

        } else if (type == AddresssType.PARISH && StringUtils.hasText(deaneryName)
                && !parishRepository.existsByName(name)) {
            Deanery deanery = validateDeanary(deaneryName);
            return parishRepository.save(new Parish(proposer, name, deanery, detailedAddress));
        }
        throw new BusinessException(HttpStatus.BAD_REQUEST, "Params are missed out or The proposal already exists");
    }

    @Transactional
    public Proposable sysAdminUpdateAddress(User admin, UpdateAddressDto requestDto) {
        if (!admin.isSysAdmin()) {
            return null;
        }
        AddresssType type = requestDto.getType();
        String name = requestDto.getName();
        Boolean isApproved = requestDto.getIsApproved();

        if (type == AddresssType.DIOCESE) {
            Diocese diocese = validateDiocese(name);
            diocese.update(isApproved);
            return dioceseRepository.save(diocese);
        } else if (type == AddresssType.DEANERY) {
            Deanery deanery = validateDeanary(name);
            deanery.update(isApproved);
            return deaneryRepository.save(deanery);
        } else if (type == AddresssType.PARISH) {
            Parish parish = validateParish(name);
            parish.update(isApproved);
            return parishRepository.save(parish);
        }
        throw new BusinessException(HttpStatus.BAD_REQUEST, "Invalid proposal type.");

    }

    @Transactional
    public long sysAdminHardDeleteAddress(User admin, DeleteAddressDto requestDto) {
        if (!admin.isSysAdmin()) {
            return 0;
        }
        AddresssType type = requestDto.getType();
        String name = requestDto.getName();

        if (type == AddresssType.DIOCESE) {
            return dioceseRepository.deleteByName(name);
        } else if (type == AddresssType.DEANERY) {
            return deaneryRepository.deleteByName(name);
        } else if (type == AddresssType.PARISH) {
            return parishRepository.deleteByName(name);
        }
        throw new BusinessException(HttpStatus.BAD_REQUEST, "Invalid address type.");
    }

    public Country validateCountry(@NotNull String countryName) {

        return countryRepository.findByName(countryName).orElseThrow(() -> {
            throw new BusinessException(HttpStatus.BAD_REQUEST, ErrorMessage.COUNTRY_NOT_FOUND);
        });

    }

    public Country save(Country country) {
        return countryRepository.save(country);
    }

    public Diocese validateDiocese(@NotNull String dioceseName) {

        return dioceseRepository.findByName(dioceseName).orElseThrow(() -> {
            throw new BusinessException(HttpStatus.BAD_REQUEST, ErrorMessage.DIOCESE_NOT_FOUND);
        });

    }

    public Diocese save(Diocese diocese) {
        return dioceseRepository.save(diocese);
    }

    public Deanery validateDeanary(@NotNull String deaneryName) {

        return deaneryRepository.findByName(deaneryName).orElseThrow(() -> {
            throw new BusinessException(HttpStatus.BAD_REQUEST, ErrorMessage.DEANERY_NOT_FOUND);
        });

    }

    public Deanery save(Deanery deanery) {
        return deaneryRepository.save(deanery);
    }

    public Parish validateParish(@NotNull String parishName) {
        return parishRepository.findByName(parishName).orElseThrow(() -> {
            throw new BusinessException(HttpStatus.BAD_REQUEST, ErrorMessage.PARISH_NOT_FOUND);
        });
    }

    public Parish save(Parish parish) {
        return parishRepository.save(parish);
    }
}
