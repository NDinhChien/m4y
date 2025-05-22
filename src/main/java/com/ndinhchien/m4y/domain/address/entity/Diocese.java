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
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "dioceses", indexes = {
        @Index(name = "dioceses_country_name_idx", columnList = "country_name"),
})
public class Diocese extends Proposable {

    @Id
    private String name;

    @Column(name = "country_name", nullable = false, insertable = false, updatable = false)
    private String countryName;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_name")
    private Country country;

    public Diocese(User proposer, String name, Country country) {
        super(proposer);
        this.name = name;
        this.country = country;
        this.countryName = country.getName();
    }
}
