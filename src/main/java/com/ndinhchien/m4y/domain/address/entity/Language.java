package com.ndinhchien.m4y.domain.address.entity;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.ndinhchien.m4y.domain.user.entity.User;
import com.ndinhchien.m4y.global.entity.Proposable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Cache(usage = CacheConcurrencyStrategy.READ_ONLY, region = "readOnlyCache")
@NoArgsConstructor
@Getter
@Entity
@Table(name = "languages")
public class Language extends Proposable {
    @Id
    private String name;

    @Column(nullable = false, unique = true)
    private String code;

    public Language(User proposer, String name, String code) {
        super(proposer);
        this.name = name;
        this.code = code;
    }

}
