package com.ndinhchien.m4y.domain.subtitle.dto;

import java.util.List;

public class SubtitleResponseDto {

    public static interface ISubtitle {
        Long getId();
        String getProjectSrcUrl();
        Integer getStartAt();
        Integer getEndAt();
        Object getSrcText();
        List<Object> getDesTexts();
        Long getCreatorId();
    }
}
