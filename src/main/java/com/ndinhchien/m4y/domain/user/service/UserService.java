package com.ndinhchien.m4y.domain.user.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.ndinhchien.m4y.domain.address.entity.Country;
import com.ndinhchien.m4y.domain.address.entity.Deanery;
import com.ndinhchien.m4y.domain.address.entity.Diocese;
import com.ndinhchien.m4y.domain.address.entity.Parish;
import com.ndinhchien.m4y.domain.address.service.AddressService;
import com.ndinhchien.m4y.domain.project.service.ProjectTranslatorService;
import com.ndinhchien.m4y.domain.user.dto.UserRequestDto.UpdateAddressDto;
import com.ndinhchien.m4y.domain.user.dto.UserRequestDto.UpdateProfileDto;
import com.ndinhchien.m4y.domain.user.dto.UserResponseDto.IBasicUser;
import com.ndinhchien.m4y.domain.user.dto.UserResponseDto.IPublicUser;
import com.ndinhchien.m4y.domain.user.dto.UserResponseDto.IUser;
import com.ndinhchien.m4y.domain.user.entity.User;
import com.ndinhchien.m4y.domain.user.repository.UserRepository;
import com.ndinhchien.m4y.global.exception.BusinessException;
import com.ndinhchien.m4y.global.exception.ErrorMessage;
import com.ndinhchien.m4y.global.service.SystemFileService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {
    private final AddressService addressService;
    private final UserRepository userRepository;
    private final SystemFileService systemFileService;

    @Transactional(readOnly = true)
    public IUser getProfile(User user) {
        return userRepository.findUserById(user.getId()).orElse(null);
    }
    
    @Transactional(readOnly = true)
    public IPublicUser getPublicProfile(Long userId) {
        return userRepository.findOneById(userId).orElse(null);
    }

    @Transactional(readOnly = true)
    public List<IBasicUser> searchUsers(String name) {
        if (!StringUtils.hasText(name)) {
            return new ArrayList<>();
        }
        return userRepository.findByUserNameContaining(name);
    }

    @Transactional
    public User updateProfile(User user, UpdateProfileDto requestDto) {
        String userName = requestDto.getUserName();

        if (StringUtils.hasText(userName) && !userRepository.existsByUserName(userName) && user.canUpdateUserName()) {
            user.updateUserName(userName);
        }
        user.updateProfile(requestDto);
        return userRepository.save(user);
    }

    @Transactional
    public User updateAvatar(User user, MultipartFile avatar) {
        String avatarUrl = systemFileService.saveAvatar(user, avatar);
        user.setAvatar(avatarUrl);
        return userRepository.save(user);
    }

    @Transactional
    public User updateAddress(User user, UpdateAddressDto requestDto) {
        String countryName = requestDto.getCountryName();
        String dioceseName = requestDto.getDioceseName();
        String deaneryName = requestDto.getDeaneryName();
        String parishName = requestDto.getParishName();

        Country country = user.getCountry();
        Diocese diocese = user.getDiocese();
        Deanery deanery = user.getDeanery();
        Parish parish = user.getParish();

        if (StringUtils.hasText(countryName)) {
            country = addressService.validateCountry(countryName);
        }
        if (StringUtils.hasText(dioceseName)) {
            diocese = addressService.validateDiocese(dioceseName);
        }
        if (StringUtils.hasText(deaneryName)) {
            deanery = addressService.validateDeanary(deaneryName);
        }
        if (StringUtils.hasText(parishName)) {
            parish = addressService.validateParish(parishName);
        }

        user.resetAddress();
        if (country != null && country.getIsApproved()) {
            user.setCountry(country);
        }
        if (diocese != null && diocese.getIsApproved() && country != null
                && diocese.getCountryName().equals(country.getName())) {
            user.setDiocese(diocese);
        }
        if (deanery != null && deanery.getIsApproved() && diocese != null
                && deanery.getDioceseName().equals(diocese.getName())) {
            user.setDeanery(deanery);
        }
        if (parish != null && parish.getIsApproved() && deanery != null
                && parish.getDeaneryName().equals(deanery.getName())) {
            user.setParish(parish);
        }
        return userRepository.save(user);
    }

    @Transactional
    public int toggleFollow(User user, Long translatorId) {
        if (user.getId().equals(translatorId)) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Can't (un)follow yourself");
        }
        User translator = validateUser(translatorId);
        int result = user.toggleFollow(translator);
        userRepository.save(translator);
        userRepository.save(user);
        return result;
    }

    private User validateUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> {
            throw new BusinessException(HttpStatus.BAD_REQUEST, ErrorMessage.USER_NOT_FOUND);
        });
    }
}
