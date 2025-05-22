package com.ndinhchien.m4y.domain.address.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ndinhchien.m4y.domain.user.entity.User;
import com.ndinhchien.m4y.global.entity.Proposable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "parishes", indexes = {
        @Index(name = "parishes_deanery_name_idx", columnList = "deanery_name"),
})
public class Parish extends Proposable {

    @Id
    private String name;

    @Column(name = "deanery_name", nullable = false, insertable = false, updatable = false)
    private String deaneryName;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deanery_name")
    private Deanery deanery;

    @Column
    private String address;

    @Builder
    public Parish(User proposer, String name, Deanery deanery, String address) {
        super(proposer);
        this.name = name;
        this.deanery = deanery;
        this.deaneryName = deanery.getName();
        this.address = address;
    }
}
