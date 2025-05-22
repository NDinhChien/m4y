package com.ndinhchien.m4y.domain.project.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ndinhchien.m4y.domain.project.entity.Project;
import com.ndinhchien.m4y.domain.project.entity.ProjectTranslator;
import com.ndinhchien.m4y.domain.user.entity.User;

public interface ProjectTranslatorRepository extends JpaRepository<ProjectTranslator, Long> {

    boolean existsByProjectAndUser(Project project, User user);

    Optional<ProjectTranslator> findByProjectAndUser(Project project, User user);

}
