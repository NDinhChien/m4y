package com.ndinhchien.m4y.domain.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ndinhchien.m4y.domain.user.entity.Follower;
import com.ndinhchien.m4y.domain.user.entity.User;

public interface FollowerRepository extends JpaRepository<Follower, Long> {

    Optional<Follower> findByUserAndTarget(User user, User target);

}
