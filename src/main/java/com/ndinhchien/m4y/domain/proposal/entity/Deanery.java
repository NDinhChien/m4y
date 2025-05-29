package com.ndinhchien.m4y.domain.proposal.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ndinhchien.m4y.domain.user.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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
@Table(name = "deaneries", indexes = {
        @Index(name = "deaneries_diocese_name_idx", columnList = "diocese_name"),
})
public class Deanery extends Proposable {

    @Id
    private String name;

    @Column(name = "diocese_name", nullable = false, insertable = false, updatable = false)
    private String dioceseName;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diocese_name")
    private Diocese diocese;

    public Deanery(User proposer, String name, Diocese diocese) {
        super(proposer);
        this.name = name;
        this.diocese = diocese;
        this.dioceseName = diocese.getName();

    }
}
