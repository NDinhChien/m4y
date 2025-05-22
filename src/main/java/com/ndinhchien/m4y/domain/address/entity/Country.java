package com.ndinhchien.m4y.domain.address.entity;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ndinhchien.m4y.domain.user.entity.User;
import com.ndinhchien.m4y.global.entity.Proposable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Cache(usage = CacheConcurrencyStrategy.READ_ONLY, region = "readOnlyCache")
@Getter
@NoArgsConstructor
@Entity
@Table(name = "countries")
public class Country extends Proposable {
    @Id
    private String name;

    @Column(nullable = false)
    private String code;

    @Column(name = "language_name", nullable = false, insertable = false, updatable = false)
    private String languageName;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "language_name")
    private Language language;

    public Country(User proposer, String name, String code, Language language) {
        super(proposer);
        this.name = name;
        this.code = code;
        this.language = language;
        this.languageName = language.getName();
    }

}
