package com.ndinhchien.m4y.domain.reaction.entity;

import com.ndinhchien.m4y.domain.reaction.type.Emoji;
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
@Table(name = "comment_reactions", indexes = {
        @Index(name = "comment_reactions_is_reply_comment_id_user_id_idx", columnList = "is_reply, comment_id, user_id")
})
public class CommentReaction extends BaseReaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long commentId;

    @Column(nullable = false)
    private Boolean isReply;

    public CommentReaction(User user, Emoji emoji, Long commentId, Boolean isReply) {
        super(user, emoji);
        this.commentId = commentId;
        this.isReply = isReply;
    }

}