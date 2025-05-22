package com.ndinhchien.m4y.domain.subtitle.type;

import java.time.Instant;

import com.ndinhchien.m4y.domain.user.entity.User;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(exclude = { "editedAt" })
@NoArgsConstructor
@Getter
public class SubtitleText {

    private String langCode;

    private String text;

    private Long translatorId;

    private Instant editedAt;

    public SubtitleText(String langCode, String text, User translator) {
        this.langCode = langCode;
        this.translatorId = translator.getId();
        this.text = text;
        this.editedAt = Instant.now();
    }

    public void updateText(String text) {
        this.text = text;
        this.editedAt = Instant.now();
    }
}
