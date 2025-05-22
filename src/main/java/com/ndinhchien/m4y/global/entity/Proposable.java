package com.ndinhchien.m4y.global.entity;

import java.io.Serializable;
import java.time.Instant;

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
public class Proposable implements Serializable {

    @Column(name = "proposer_id", nullable = false, insertable = false, updatable = false)
    private Long proposerId;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proposer_id")
    private User proposer;

    @Column(nullable = false)
    private Integer reactCount;

    @Column(nullable = false)
    private Boolean isApproved;

    @Column(nullable = false)
    private Instant createdAt;

    @PrePersist
    private void prePersist() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
        if (isApproved == null) {
            isApproved = false;
        }
        if (reactCount == null) {
            reactCount = 0;
        }
    }

    public Proposable(User proposer) {
        this.proposer = proposer;
        this.proposerId = proposer.getId();
        if (this.proposer.isSysAdmin()) {
            this.isApproved = true;
        }
        this.prePersist();
    }

    public void updateReactCount(int count) {
        this.reactCount = Math.max(0, this.reactCount + count);
    }

    public void update(Boolean isApproved) {
        if (isApproved != null) {
            this.isApproved = isApproved;
        }
    }
}
