package com.ndinhchien.m4y.domain.reaction.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ndinhchien.m4y.domain.proposal.type.ProposalType;
import com.ndinhchien.m4y.domain.reaction.dto.ReactionResponseDto.IProposalReaction;
import com.ndinhchien.m4y.domain.reaction.entity.ProposalReaction;
import com.ndinhchien.m4y.domain.user.entity.User;

public interface ProposalReactionRepository extends JpaRepository<ProposalReaction, Long> {

    Optional<ProposalReaction> findByProposalTypeAndProposalNameAndUser(ProposalType proposalType,
            String proposalName, User user);

    List<IProposalReaction> findAllByUser(User user);
}
