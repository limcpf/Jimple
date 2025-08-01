package com.jimple.model.category;

import com.jimple.model.list.PostPageInfo;

import java.util.List;

/**
 * 카테고리 페이지 정보를 나타내는 레코드 클래스
 * @param posts 카테고리에 속한 게시글 목록
 * @param subCategories 하위 카테고리 요약 목록
 * @param page 페이지 정보
 * @param categoryPath 현재 카테고리 경로
 * @param categoryName 현재 카테고리 이름
 */
public record CategoryPage(
        List<CategoryPageItem> posts,
        List<CategorySummary> subCategories,
        PostPageInfo page,
        String categoryPath,
        String categoryName
) {
}