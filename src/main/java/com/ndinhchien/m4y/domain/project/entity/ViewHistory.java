package com.ndinhchien.m4y.domain.project.entity;

import java.io.Serializable;
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
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Entity
@Table(name = "view_histories", indexes = {
        @Index(name = "view_histories_project_id_user_id_idx", columnList = "project_id, user_id")
})
public class ViewHistory implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "project_id", nullable = false, insertable = false, updatable = false)
    private Long projectId;

    @JsonIgnore
    @JoinColumn(name = "project_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Project project;

    @Column(name = "user_id", nullable = false, insertable = false, updatable = false)
    private Long userId;

    @JsonIgnore
    @JoinColumn(name = "user_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @Column(nullable = false)
    private Integer viewCount;

    @Column(nullable = false)
    private Instant lastViewStartAt;

    public ViewHistory(User user, Project project) {
        this.user = user;
        this.userId = user.getId();
        this.project = project;
        this.projectId = project.getId();

        this.viewCount = 0;
        this.lastViewStartAt = null;
    }

    public int updateViewCount(int duration) {
        if (lastViewStartAt == null || lastViewStartAt.plusSeconds(duration).isBefore(Instant.now())) {
            this.lastViewStartAt = Instant.now();
            this.viewCount += 1;
            return 1;
        }
        return 0;
    }
}
