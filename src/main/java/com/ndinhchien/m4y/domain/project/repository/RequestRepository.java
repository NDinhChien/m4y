package com.ndinhchien.m4y.domain.project.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ndinhchien.m4y.domain.project.dto.ProjectResponseDto.IRequest;
import com.ndinhchien.m4y.domain.project.entity.Project;
import com.ndinhchien.m4y.domain.project.entity.Request;
import com.ndinhchien.m4y.domain.user.entity.User;

public interface RequestRepository extends JpaRepository<Request, Long> {

    boolean existsByProjectAndUser(Project project, User user);

    Optional<Request> findByProjectAndUser(Project project, User user);

    List<IRequest> findAllByUser(User user);
}
