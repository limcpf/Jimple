package com.jimple.model.category;

import com.jimple.model.md.MarkdownFile;

import java.util.List;

/**
 * 카테고리 정보를 나타내는 레코드 클래스
 * @param name 카테고리 이름
 * @param fullPath 전체 경로 ("/"로 구분)
 * @param level 카테고리 레벨 (루트=0, 하위 카테고리일수록 증가)
 * @param posts 해당 카테고리에 속한 게시글 목록
 * @param subCategories 하위 카테고리 목록
 */
public record CategoryInfo(
        String name,
        String fullPath,
        int level,
        List<MarkdownFile> posts,
        List<CategoryInfo> subCategories
) {
    
    /**
     * 루트 카테고리 생성자
     */
    public CategoryInfo() {
        this("", "", 0, List.of(), List.of());
    }
    
    /**
     * 카테고리에 게시글이 있는지 확인
     * @return 게시글이 있으면 true
     */
    public boolean hasPosts() {
        return !posts.isEmpty();
    }
    
    /**
     * 하위 카테고리가 있는지 확인
     * @return 하위 카테고리가 있으면 true
     */
    public boolean hasSubCategories() {
        return !subCategories.isEmpty();
    }
    
    /**
     * 전체 게시글 수 (하위 카테고리 포함)
     * @return 전체 게시글 수
     */
    public int getTotalPostCount() {
        int count = posts.size();
        for (CategoryInfo subCategory : subCategories) {
            count += subCategory.getTotalPostCount();
        }
        return count;
    }
}