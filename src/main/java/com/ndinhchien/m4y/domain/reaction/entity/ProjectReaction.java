package com.ndinhchien.m4y.domain.reaction.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ndinhchien.m4y.domain.address.entity.Country;
import com.ndinhchien.m4y.domain.address.type.AddresssType;
import com.ndinhchien.m4y.domain.project.entity.Project;
import com.ndinhchien.m4y.domain.reaction.type.Emoji;
import com.ndinhchien.m4y.domain.reaction.type.BaseReaction;
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

@Getter
@NoArgsConstructor
@Entity
@Table(name = "project_reactions", indexes = {
        @Index(name = "project_reactions_project_id_user_id_idx", columnList = "project_id, user_id"),
})
public class ProjectReaction extends BaseReaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "project_id", nullable = false, insertable = false, updatable = false)
    private Long projectId;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    public ProjectReaction(User user, Emoji emoji, Project project) {
        super(user, emoji);
        this.project = project;
        this.projectId = project.getId();
    }
}
