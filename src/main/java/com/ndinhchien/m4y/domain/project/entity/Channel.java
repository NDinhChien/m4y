package com.ndinhchien.m4y.domain.project.entity;

import java.io.Serializable;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Cache(usage = CacheConcurrencyStrategy.READ_ONLY, region = "readOnlyCache")
@NoArgsConstructor
@Getter
@Entity
@Table(name = "channels")
public class Channel implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String url;

    @Column
    private String name;

    public Channel(String url, String name) {
        this.url = url;
        this.name = name;
    }
}
