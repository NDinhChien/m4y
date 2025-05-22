package com.ndinhchien.m4y.domain.project.entity;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Table(name = "project_translators", indexes = {
        @Index(name = "project_translators_project_id_idx", columnList = "project_id"),
})
public class ProjectTranslator {
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
    private Boolean isAccepted;

    @Column(nullable = false)
    private Instant createdAt;

    @PrePersist
    private void prePersist() {
        if (isAccepted == null) {
            isAccepted = false;
        }
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }

    public ProjectTranslator(Project project, User user) {
        this.project = project;
        this.projectId = project.getId();
        this.user = user;
        this.userId = user.getId();

        this.prePersist();
    }

    public void updateIsAccepted() {
        this.isAccepted = true;
    }
}
