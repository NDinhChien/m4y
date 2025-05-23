package com.ndinhchien.m4y.domain.subtitle.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.Type;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ndinhchien.m4y.domain.project.entity.Video;
import com.ndinhchien.m4y.domain.subtitle.dto.SubtitleRequestDto.UpdateDesTextDto;
import com.ndinhchien.m4y.domain.subtitle.dto.SubtitleRequestDto.UpdateSubtitleDto;
import com.ndinhchien.m4y.domain.subtitle.type.SubtitleText;
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
import io.hypersistence.utils.hibernate.type.json.JsonType;

@NoArgsConstructor
@Getter
@Entity
@Table(name = "subtitles", indexes = {
        @Index(name = "subtitles_video_url_idx", columnList = "video_url"),
})
public class Subtitle implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "video_id")
    private Video video;

    @Column(name = "video_id", nullable = false, insertable = false, updatable = false)
    private Long videoId;

    @Column(nullable = false)
    private String videoUrl;

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

    public Subtitle(User creator, Video video, Integer start,
            Integer end, String text) {

        this.video = video;
        this.videoId = video.getId();
        this.videoUrl = video.getUrl();

        this.startAt = start;
        this.endAt = end;
        this.srcText = new SubtitleText(video.getLangCode(), text, creator);

        this.desTexts = new ArrayList<>();
    }

    private SubtitleText getDesText(String langCode) {
        for (SubtitleText text : this.desTexts) {
            if (text.getLangCode().equals(langCode)) {
                return text;
            }
        }
        return null;
    }

    private void updateSrcText(String text) {
        this.srcText.updateText(text);
    }

    public void update(User translator, String text, String langCode) {
        SubtitleText current = getDesText(langCode);
        if (current != null) {
            current.updateText(text);
        } else {
            this.desTexts.add(new SubtitleText(langCode, text, translator));
        }
    }

    public void update(User creator, UpdateSubtitleDto dto) {
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
            updateSrcText(srcText);
        }
    }

    public boolean hasOtherAdminDesTexts(User admin) {
        return this.desTexts.stream().anyMatch(text -> !text.getTranslatorId().equals(admin.getId()));
    }
}
