package com.ndinhchien.m4y.domain.reaction.entity;

import com.ndinhchien.m4y.domain.reaction.type.Emoji;
import com.ndinhchien.m4y.domain.address.type.AddresssType;
import com.ndinhchien.m4y.domain.reaction.type.BaseReaction;
import com.ndinhchien.m4y.domain.user.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "proposal_reactions", indexes = {
        @Index(name = "proposal_reactions_proposal_type_proposal_name_user_id_idx", columnList = "proposal_type, proposal_name, user_id"),
        @Index(name = "proposal_reactions_user_id_idx", columnList = "user_id"),
})
public class ProposalReaction extends BaseReaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private AddresssType proposalType;

    @Column(nullable = false)
    private String proposalName;

    public ProposalReaction(User user, Emoji emoji, AddresssType proposalType, String proposalName) {
        super(user, emoji);
        this.proposalType = proposalType;
        this.proposalName = proposalName;
    }
}
