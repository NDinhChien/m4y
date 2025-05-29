package com.ndinhchien.m4y.domain.proposal.service;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.ndinhchien.m4y.domain.proposal.dto.ProposalRequestDto.AddProposalDto;
import com.ndinhchien.m4y.domain.proposal.dto.ProposalRequestDto.DeleteProposalDto;
import com.ndinhchien.m4y.domain.proposal.dto.ProposalRequestDto.UpdateProposalDto;
import com.ndinhchien.m4y.domain.proposal.dto.ProposalResponseDto.ICountry;
import com.ndinhchien.m4y.domain.proposal.dto.ProposalResponseDto.IDeanery;
import com.ndinhchien.m4y.domain.proposal.dto.ProposalResponseDto.IDiocese;
import com.ndinhchien.m4y.domain.proposal.dto.ProposalResponseDto.IParish;
import com.ndinhchien.m4y.domain.proposal.entity.Country;
import com.ndinhchien.m4y.domain.proposal.entity.Deanery;
import com.ndinhchien.m4y.domain.proposal.entity.Diocese;
import com.ndinhchien.m4y.domain.proposal.entity.Parish;
import com.ndinhchien.m4y.domain.proposal.entity.Proposable;
import com.ndinhchien.m4y.domain.proposal.repository.CountryRepository;
import com.ndinhchien.m4y.domain.proposal.repository.DeaneryRepository;
import com.ndinhchien.m4y.domain.proposal.repository.DioceseRepository;
import com.ndinhchien.m4y.domain.proposal.repository.ParishRepository;
import com.ndinhchien.m4y.domain.proposal.type.ProposalType;
import com.ndinhchien.m4y.domain.user.entity.User;
import com.ndinhchien.m4y.global.exception.BusinessException;
import com.ndinhchien.m4y.global.exception.ErrorMessage;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class ProposalService {

    private final CountryRepository countryRepository;

    private final DioceseRepository dioceseRepository;

    private final DeaneryRepository deaneryRepository;

    private final ParishRepository parishRepository;

    @Transactional(readOnly = true)
    public Object getAddressProposals() {
        List<ICountry> countries = countryRepository.findAllByIsApproved(true);
        List<IDiocese> dioceses = dioceseRepository.findAllByIsApproved(true);
        List<IDeanery> deaneries = deaneryRepository.findAllByIsApproved(true);
        List<IParish> parishes = parishRepository.findAllByIsApproved(true);
        return Map.of("countries", countries, "dioceses", dioceses, "deaneries", deaneries,
                "parishes", parishes);
    }

    @Transactional
    public Object addAddressProposal(User proposer, AddProposalDto requestDto) {
        ProposalType type = requestDto.getType();
        String name = requestDto.getName();
        String countryName = requestDto.getCountryName();
        String dioceseName = requestDto.getDioceseName();
        String deaneryName = requestDto.getDeaneryName();
        String address = requestDto.getAddress();

        if (type == ProposalType.DIOCESE && StringUtils.hasText(countryName)
                && !dioceseRepository.existsByName(name)) {
            Country country = validateCountry(countryName);
            return dioceseRepository.save(new Diocese(proposer, name, country));

        } else if (type == ProposalType.DEANERY && StringUtils.hasText(dioceseName)
                && !deaneryRepository.existsByName(name)) {
            Diocese diocese = validateDiocese(dioceseName);
            return deaneryRepository.save(new Deanery(proposer, name, diocese));

        } else if (type == ProposalType.PARISH && StringUtils.hasText(deaneryName)
                && !parishRepository.existsByName(name)) {
            Deanery deanery = validateDeanary(deaneryName);
            return parishRepository.save(new Parish(proposer, name, deanery, address));
        }
        throw new BusinessException(HttpStatus.BAD_REQUEST, "Params are missed out or The proposal already exists");
    }

    @Transactional
    public Proposable sysAdminUpdateProposal(User admin, UpdateProposalDto requestDto) {
        if (!admin.isSysAdmin()) {
            return null;
        }
        ProposalType type = requestDto.getType();
        String name = requestDto.getName();
        Boolean isApproved = requestDto.getIsApproved();

        if (type == ProposalType.DIOCESE) {
            Diocese diocese = validateDiocese(name);
            diocese.update(isApproved);
            return dioceseRepository.save(diocese);
        } else if (type == ProposalType.DEANERY) {
            Deanery deanery = validateDeanary(name);
            deanery.update(isApproved);
            return deaneryRepository.save(deanery);
        } else if (type == ProposalType.PARISH) {
            Parish parish = validateParish(name);
            parish.update(isApproved);
            return parishRepository.save(parish);
        }
        throw new BusinessException(HttpStatus.BAD_REQUEST, "Invalid proposal type.");

    }

    @Transactional
    public long sysAdminHardDeleteProposal(User admin, DeleteProposalDto requestDto) {
        if (!admin.isSysAdmin()) {
            return 0;
        }
        ProposalType type = requestDto.getType();
        String name = requestDto.getName();

        if (type == ProposalType.DIOCESE) {
            return dioceseRepository.deleteByName(name);
        } else if (type == ProposalType.DEANERY) {
            return deaneryRepository.deleteByName(name);
        } else if (type == ProposalType.PARISH) {
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
