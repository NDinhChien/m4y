package com.ndinhchien.m4y.domain.subtitle.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.hibernate.type.SqlTypes;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ndinhchien.m4y.domain.project.entity.Project;
import com.ndinhchien.m4y.domain.subtitle.dto.SubtitleRequestDto.AddSubtitleDto;
import com.ndinhchien.m4y.domain.subtitle.dto.SubtitleRequestDto.UpdateDesTextDto;
import com.ndinhchien.m4y.domain.subtitle.dto.SubtitleRequestDto.UpdateSubtitleDto;
import com.ndinhchien.m4y.domain.subtitle.type.SubtitleText;
import com.ndinhchien.m4y.domain.user.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
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
import io.hypersistence.utils.hibernate.type.json.JsonType;

@NoArgsConstructor
@Getter
@Entity
@Table(name = "subtitles", indexes = {
        @Index(name = "subtitles_project_src_url_idx", columnList = "project_src_url"),
})
public class Subtitle implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String projectSrcUrl;

    @Column(nullable = false)
    private Integer startAt;

    @Column(nullable = false)
    private Integer endAt;

    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb")
    private SubtitleText srcText;

    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb")
    private List<SubtitleText> desTexts;

    @Column(name = "creator_id", nullable = false, insertable = false, updatable = false)
    private Long creatorId;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id")
    private User creator;

    public Subtitle(User creator, String projectSrcUrl, String projectSrcLangCode, Integer start,
            Integer end, String text) {
        this.projectSrcUrl = projectSrcUrl;
        this.creator = creator;
        this.creatorId = creator.getId();
        this.startAt = start;
        this.endAt = end;
        this.srcText = new SubtitleText(projectSrcLangCode, text, creator);

        this.desTexts = new ArrayList<>();
    }

    public SubtitleText getDesText(String languageCode) {
        for (SubtitleText text : this.desTexts) {
            if (text.getLangCode().equals(languageCode)) {
                return text;
            }
        }
        return null;
    }

    public void updateSrcText(SubtitleText srcText) {
        this.srcText = srcText;
    }

    public void updateDesText(User admin, UpdateDesTextDto dto, String desLangCode) {
        SubtitleText current = getDesText(desLangCode);
        if (current != null) {
            current.updateText(dto.getDesText());
        } else {
            this.desTexts.add(new SubtitleText(desLangCode, dto.getDesText(), admin));
        }
    }

    public boolean isCreator(User admin) {
        return admin.getId().equals(this.creatorId);
    }

    public void creatorUpdate(User creator, UpdateSubtitleDto dto, String srcLangCode) {
        Integer start = dto.getStart();
        Integer end = dto.getEnd();
        String srcText = dto.getSrcText();
        if (start != null) {
            this.startAt = start;
        }
        if (end != null && start < end) {
            this.endAt = end;
        }

        if (StringUtils.hasText(srcText)) {
            updateSrcText(new SubtitleText(srcLangCode, srcText, creator));
        }
    }

    public boolean creatorCanDelete(User admin) {
        if (!isCreator(admin))
            return false;
        return this.desTexts.stream().anyMatch(text -> !text.getTranslatorId().equals(admin.getId()));
    }
}
