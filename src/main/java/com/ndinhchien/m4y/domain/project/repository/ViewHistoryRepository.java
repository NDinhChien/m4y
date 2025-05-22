package com.ndinhchien.m4y.domain.project.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ndinhchien.m4y.domain.project.entity.Project;
import com.ndinhchien.m4y.domain.project.entity.ViewHistory;
import com.ndinhchien.m4y.domain.user.entity.User;

public interface ViewHistoryRepository extends JpaRepository<ViewHistory, Long> {

    Optional<ViewHistory> findByProjectAndUser(Project project, User user);

}