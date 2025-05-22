package com.ndinhchien.m4y.global.dto;

import java.util.List;

import org.springframework.data.domain.Page;

import lombok.Getter;

@Getter
public class PageDto<T> {
    private List<T> content;
    private int pageSize;
    private int pageNumber;
    private int maxPage;
    private int numberOfElements;

    public PageDto(Page<?> page) {
        this.content = (List<T>) page.getContent();
        this.pageSize = page.getSize();
        this.pageNumber = page.getNumber();
        this.numberOfElements = page.getNumberOfElements();

    }
}
