package com.ndinhchien.m4y.domain.comment.entity;

import java.util.List;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.ndinhchien.m4y.domain.project.entity.Project;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ndinhchien.m4y.domain.comment.type.BaseComment;
import com.ndinhchien.m4y.domain.user.entity.User;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region = "commentCache")
@NoArgsConstructor
@Getter
@Entity
@Table(name = "comments", indexes = {
        @Index(name = "comments_project_id_user_id_idx", columnList = "project_id, user_id"),
})
public class Comment extends BaseComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "project_id", nullable = false, insertable = false, updatable = false)
    private Long projectId;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    @JsonIgnore
    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Reply> replies;

    public Comment(User user, Project project, String content) {
        super(user, content);
        this.project = project;
        this.projectId = project.getId();
    }

}
