package com.jimple.model.category;

/**
 * 카테고리 요약 정보를 담는 레코드 클래스
 * JSON 응답에서 사용하기 위해 필요한 정보만 포함
 */
public record CategorySummary(
        String name,
        String fullPath,
        int postCount
) {
    
    /**
     * CategoryInfo로부터 CategorySummary 생성
     * @param categoryInfo 카테고리 정보
     * @return CategorySummary 객체
     */
    public static CategorySummary from(CategoryInfo categoryInfo) {
        return new CategorySummary(
                categoryInfo.name(),
                categoryInfo.fullPath(),
                categoryInfo.posts().size()
        );
    }
}