package com.jimple.model.category;

import com.jimple.model.md.MarkdownFile;

/**
 * 카테고리별 게시글 아이템을 나타내는 레코드 클래스
 * @param title 게시글 제목
 * @param path 게시글 경로 (HTML 파일 경로)
 * @param date 게시 날짜
 * @param categoryPath 카테고리 경로
 * @param description 게시글 설명
 */
public record CategoryPageItem(
        String title, 
        String path, 
        String date,
        String categoryPath,
        String description
) {
    /**
     * MarkdownFile로부터 CategoryPageItem 생성
     * @param file 마크다운 파일
     */
    public CategoryPageItem(MarkdownFile file) {
        this(
            file.properties().title(), 
            file.path(), 
            file.getPublishDate().toString(),
            file.properties().path(),
            file.properties().description()
        );
    }
}