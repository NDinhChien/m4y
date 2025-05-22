package com.ndinhchien.m4y.domain.reaction.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ndinhchien.m4y.domain.address.type.AddresssType;
import com.ndinhchien.m4y.domain.reaction.entity.ProposalReaction;
import com.ndinhchien.m4y.domain.user.entity.User;

public interface ProposalReactionRepository extends JpaRepository<ProposalReaction, Long> {

    Optional<ProposalReaction> findByProposalTypeAndProposalNameAndUser(AddresssType proposalType,
            String proposalName, User user);
}
