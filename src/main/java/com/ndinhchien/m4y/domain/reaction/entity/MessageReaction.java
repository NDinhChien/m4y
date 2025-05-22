package com.ndinhchien.m4y.domain.reaction.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ndinhchien.m4y.domain.message.entity.Message;
import com.ndinhchien.m4y.domain.project.entity.Project;
import com.ndinhchien.m4y.domain.reaction.type.BaseReaction;
import com.ndinhchien.m4y.domain.reaction.type.Emoji;
import com.ndinhchien.m4y.domain.user.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "message_reactions", indexes = {
        @Index(name = "message_reactions_message_id_user_id_idx", columnList = "message_id, user_id"),
})
public class MessageReaction extends BaseReaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "message_id", nullable = false, insertable = false, updatable = false)
    private Long messageId;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "message_id")
    private Message message;

    public MessageReaction(User user, Emoji emoji, Message message) {
        super(user, emoji);
        this.message = message;
        this.messageId = message.getId();
    }
}
