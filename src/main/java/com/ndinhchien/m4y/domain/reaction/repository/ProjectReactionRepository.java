package com.ndinhchien.m4y.domain.reaction.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ndinhchien.m4y.domain.project.entity.Project;
import com.ndinhchien.m4y.domain.reaction.dto.ReactionResponseDto.IProjectReaction;
import com.ndinhchien.m4y.domain.reaction.entity.ProjectReaction;
import com.ndinhchien.m4y.domain.user.entity.User;

public interface ProjectReactionRepository extends JpaRepository<ProjectReaction, Long> {

    Optional<ProjectReaction> findByProjectAndAndUser(Project project, User user);

    List<IProjectReaction> findAllByUser(User user);
}
