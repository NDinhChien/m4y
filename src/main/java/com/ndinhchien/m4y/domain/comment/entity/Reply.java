package com.ndinhchien.m4y.domain.comment.entity;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ndinhchien.m4y.domain.comment.type.BaseComment;
import com.ndinhchien.m4y.domain.project.entity.Project;
import com.ndinhchien.m4y.domain.user.entity.User;
import com.ndinhchien.m4y.global.dto.BaseResponse;

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

@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region = "commentCache")
@NoArgsConstructor
@Getter
@Entity
@Table(name = "replies", indexes = {
        @Index(name = "replies_comment_id_idx", columnList = "comment_id"),
})
public class Reply extends BaseComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "comment_id", nullable = false, insertable = false, updatable = false)
    private Long commentId;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "comment_id")
    private Comment comment;

    public Reply(User user, Comment comment, String content) {
        super(user, content);
        this.comment = comment;
        this.commentId = comment.getId();
    }
}
