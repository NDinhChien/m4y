package com.ndinhchien.m4y.domain.user.repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ndinhchien.m4y.domain.auth.type.SocialType;
import com.ndinhchien.m4y.domain.user.dto.UserResponseDto.IBasicUser;
import com.ndinhchien.m4y.domain.user.dto.UserResponseDto.IPublicUser;
import com.ndinhchien.m4y.domain.user.dto.UserResponseDto.IUser;
import com.ndinhchien.m4y.domain.user.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);

    boolean existsByUserName(String userName);

    Optional<User> findByEmail(String email);

    Optional<IUser> findUserById(Long id);

    Optional<IPublicUser> findOneById(Long id);

    List<IPublicUser> findAllByIdIn(List<Long> ids);

    Optional<User> findBySocialIdAndSocialType(String socialId, SocialType socialType);

    List<IBasicUser> findByUserNameContaining(String name);

    List<User> findAllByBirthdayGreaterThanAndBirthdayLessThan(Instant start, Instant end);

}
