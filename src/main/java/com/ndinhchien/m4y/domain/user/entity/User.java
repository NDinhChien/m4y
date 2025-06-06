package com.ndinhchien.m4y.domain.user.entity;

import java.io.Serializable;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ndinhchien.m4y.domain.auth.type.SocialType;
import com.ndinhchien.m4y.domain.notification.entity.Notification;
import com.ndinhchien.m4y.domain.project.entity.Project;
import com.ndinhchien.m4y.domain.project.entity.Request;
import com.ndinhchien.m4y.domain.proposal.entity.Country;
import com.ndinhchien.m4y.domain.proposal.entity.Deanery;
import com.ndinhchien.m4y.domain.proposal.entity.Diocese;
import com.ndinhchien.m4y.domain.proposal.entity.Parish;
import com.ndinhchien.m4y.domain.reaction.entity.CommentReaction;
import com.ndinhchien.m4y.domain.reaction.entity.MessageReaction;
import com.ndinhchien.m4y.domain.reaction.entity.ProjectReaction;
import com.ndinhchien.m4y.domain.reaction.entity.ProposalReaction;
import com.ndinhchien.m4y.domain.user.dto.UserRequestDto.UpdateProfileDto;
import com.ndinhchien.m4y.domain.user.type.UserRole;
import com.ndinhchien.m4y.global.util.CommonUtils;

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
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "userCache")
@DynamicUpdate
@NoArgsConstructor
@Getter
@Entity
@Table(name = "users", indexes = {
        @Index(name = "users_social_id_social_type_idx", columnList = "social_id, social_type"),
        @Index(name = "users_birthday_idx", columnList = "birthday"),
})
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @Column
    private String socialId;

    @JsonIgnore
    @Column
    private SocialType socialType;

    @Column(nullable = false, unique = true)
    private String userName;

    @JsonIgnore
    @Column(nullable = false, unique = true)
    private String email;

    @JsonIgnore
    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private Boolean isVerified;

    @Column(nullable = false)
    private Boolean isBanned;

    @Column(nullable = false)
    private UserRole role;

    @Column
    private String avatar;

    @Column
    private String fullName;

    @Column
    private Instant birthday;

    @Column(length = 2048)
    private String bio;

    @Column(name = "country_name", insertable = false, updatable = false)
    private String countryName;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_name")
    private Country country;

    @Column(name = "diocese_name", insertable = false, updatable = false)
    private String dioceseName;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diocese_name")
    private Diocese diocese;

    @Column(name = "deanery_name", insertable = false, updatable = false)
    private String deaneryName;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deanery_name")
    private Deanery deanery;

    @Column(name = "parish_name", insertable = false, updatable = false)
    private String parishName;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parish_name")
    private Parish parish;

    @Column
    private String address;

    @Column(nullable = false)
    private Instant joinedAt;

    @Column
    private Instant userNameUpdatedAt;

    @JsonIgnore
    @Column(nullable = false)
    private String tokenSecret;

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Follower> followings;

    @JsonIgnore
    @OneToMany(mappedBy = "target", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Follower> followers;

    @JsonIgnore
    @OneToMany(mappedBy = "admin", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Project> projects;

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Request> requests;

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Notification> notifications;

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ProposalReaction> proposalReactions;

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ProjectReaction> projectReactions;

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<CommentReaction> commentReactions;

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<MessageReaction> messageReactions;

    @PrePersist
    private void prePersist() {
        if (role == null) {
            role = UserRole.TRANSLATOR;
        }
        if (tokenSecret == null) {
            tokenSecret = CommonUtils.generateSecureString(30);
        }
        if (joinedAt == null) {
            joinedAt = Instant.now();
        }
        if (isVerified == null) {
            isVerified = false;
        }
        if (isBanned == null) {
            isBanned = false;
        }
    }

    @Builder
    public User(String email, String userName, String password, UserRole role, String avatar, String socialId,
            SocialType socialType) {
        this.email = email;
        this.userName = userName;
        this.password = CommonUtils.hash(password);
        this.role = role;
        this.avatar = avatar;
        this.socialId = socialId;
        this.socialType = socialType;

        this.prePersist();
    }

    public String getName() {
        return this.fullName == null ? this.userName : this.fullName;
    }

    public boolean isSysAdmin() {
        return this.role.equals(UserRole.ADMIN);
    }

    public void setAvatar(String avatarUrl) {
        this.avatar = avatarUrl;
    }

    public void logout() {
        this.tokenSecret = CommonUtils.generateSecureString(30);
    }

    public boolean canUpdateUserName() {
        if (userNameUpdatedAt == null)
            return true;
        return userNameUpdatedAt.plus(7, ChronoUnit.DAYS).isBefore(Instant.now());
    }

    public void updateUserName(String userName) {
        if (canUpdateUserName()) {
            this.userName = userName;
            this.userNameUpdatedAt = Instant.now();
        }
    }

    public void updateProfile(UpdateProfileDto dto) {
        String fullName = dto.getFullName();
        Instant birthday = dto.getBirthday();
        String bio = dto.getBio();
        String address = dto.getAddress();

        if (StringUtils.hasText(fullName)) {
            this.fullName = fullName;
        }
        if (StringUtils.hasText(bio)) {
            this.bio = bio;
        }
        if (birthday != null) {
            this.birthday = birthday;
        }
        if (StringUtils.hasText(address)) {
            this.address = address;
        }
    }

    public void resetAddress() {
        this.country = null;
        this.diocese = null;
        this.deanery = null;
        this.parish = null;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public void setDiocese(Diocese diocese) {
        this.diocese = diocese;
    }

    public void setDeanery(Deanery deanery) {
        this.deanery = deanery;
    }

    public void setParish(Parish parish) {
        this.parish = parish;
    }

    public void updateIsVerified() {
        this.isVerified = true;
    }

    public boolean isPasswordCorrect(String password) {
        return CommonUtils.compare(password, this.password);
    }

    public void setPassword(String password) {
        this.password = CommonUtils.hash(password);
        this.logout();
    }

    public void updateSocialInfo(String socialId, SocialType socialType) {
        if (this.socialId == null) {
            this.socialId = socialId;
            this.socialType = socialType;
        }
    }

}