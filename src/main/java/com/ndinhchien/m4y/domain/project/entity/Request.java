package com.ndinhchien.m4y.domain.project.entity;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ndinhchien.m4y.domain.project.type.RequestStatus;
import com.ndinhchien.m4y.domain.user.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Entity
@Table(name = "requests", indexes = {
        @Index(name = "requests_user_id_idx", columnList = "user_id"),
        @Index(name = "requests_project_id_idx", columnList = "project_id"),
})
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "project_id", nullable = false, insertable = false, updatable = false)
    private Long projectId;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    @Column(name = "user_id", nullable = false, insertable = false, updatable = false)
    private Long userId;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private Integer status;

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    @PrePersist
    private void prePersist() {
        if (status == null) {
            status = RequestStatus.PENDING.value;
        }
        if (createdAt == null) {
            createdAt = Instant.now();
        }
        if (updatedAt == null) {
            updatedAt = Instant.now();
        }
    }

    public Request(Project project, User user) {
        this.project = project;
        this.projectId = project.getId();
        this.user = user;
        this.userId = user.getId();

        this.prePersist();
    }

    public boolean isOwner(User user) {
        return this.userId.equals(user.getId());
    }

    public boolean isPending() {
        return this.status.equals(RequestStatus.PENDING.value);
    }

    public boolean isAccepted() {
        return this.status.equals(RequestStatus.ACCEPTED.value);
    }

    public void updateStatus(RequestStatus status) {
        if (this.status.equals(RequestStatus.PENDING.value)) {
            this.status = status.value;
            this.updatedAt = Instant.now();
        }
    }

}
