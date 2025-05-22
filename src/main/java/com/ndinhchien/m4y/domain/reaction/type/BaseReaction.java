package com.ndinhchien.m4y.domain.reaction.type;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ndinhchien.m4y.domain.user.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@MappedSuperclass
public class BaseReaction implements Serializable {

    @Column(name = "user_id", nullable = false, insertable = false, updatable = false)
    private Long userId;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private Emoji emoji;

    @Column(nullable = false)
    private Boolean isDeleted;

    @PrePersist
    private void prePersist() {
        if (isDeleted == null) {
            isDeleted = false;
        }
    }

    public BaseReaction(User user, Emoji emoji) {
        this.user = user;
        this.emoji = emoji;

        this.prePersist();
    }

    public int update(Emoji emoji, Boolean isDeleted) {
        int count = 0;
        if (emoji != null) {
            count = this.update(emoji);
        } else if (isDeleted == true) {
            count = this.updateIsDeleted();
        }
        return count;
    }

    private int update(Emoji emoji) {
        this.emoji = emoji;
        if (this.isDeleted) {
            this.isDeleted = false;
            return 1;
        }
        return 0;
    }

    private int updateIsDeleted() {
        boolean preIsDeleted = this.isDeleted;
        this.isDeleted = true;
        if (!preIsDeleted) {
            return -1;
        }
        return 0;
    }

}
