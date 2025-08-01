package com.jimple.model.category;

import com.jimple.model.md.MarkdownFile;
import com.jimple.model.md.MarkdownProperties;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * CategoryInfo 클래스에 대한 단위 테스트
 */
class CategoryInfoTest {

    @Test
    void 기본_생성자_테스트() {
        // given & when
        CategoryInfo categoryInfo = new CategoryInfo();

        // then
        assertEquals("", categoryInfo.name());
        assertEquals("", categoryInfo.fullPath());
        assertEquals(0, categoryInfo.level());
        assertTrue(categoryInfo.posts().isEmpty());
        assertTrue(categoryInfo.subCategories().isEmpty());
    }

    @Test
    void hasPosts_게시글이_있는_경우_true_반환() {
        // given
        MarkdownFile file = new MarkdownFile(
                new MarkdownProperties(true, "테스트", LocalDate.now(), "", "", ""),
                "테스트 내용",
                "test.html"
        );
        CategoryInfo categoryInfo = new CategoryInfo("개발", "개발", 0, List.of(file), List.of());

        // when & then
        assertTrue(categoryInfo.hasPosts());
    }

    @Test
    void hasPosts_게시글이_없는_경우_false_반환() {
        // given
        CategoryInfo categoryInfo = new CategoryInfo("개발", "개발", 0, List.of(), List.of());

        // when & then
        assertFalse(categoryInfo.hasPosts());
    }

    @Test
    void hasSubCategories_하위_카테고리가_있는_경우_true_반환() {
        // given
        CategoryInfo subCategory = new CategoryInfo("Java", "개발/Java", 1, List.of(), List.of());
        CategoryInfo categoryInfo = new CategoryInfo("개발", "개발", 0, List.of(), List.of(subCategory));

        // when & then
        assertTrue(categoryInfo.hasSubCategories());
    }

    @Test
    void hasSubCategories_하위_카테고리가_없는_경우_false_반환() {
        // given
        CategoryInfo categoryInfo = new CategoryInfo("개발", "개발", 0, List.of(), List.of());

        // when & then
        assertFalse(categoryInfo.hasSubCategories());
    }

    @Test
    void getTotalPostCount_현재_카테고리의_게시글만_있는_경우() {
        // given
        MarkdownFile file1 = createMarkdownFile("테스트1");
        MarkdownFile file2 = createMarkdownFile("테스트2");
        CategoryInfo categoryInfo = new CategoryInfo("개발", "개발", 0, List.of(file1, file2), List.of());

        // when
        int totalCount = categoryInfo.getTotalPostCount();

        // then
        assertEquals(2, totalCount);
    }

    @Test
    void getTotalPostCount_하위_카테고리_게시글_포함() {
        // given
        MarkdownFile file1 = createMarkdownFile("테스트1");
        MarkdownFile file2 = createMarkdownFile("테스트2");
        MarkdownFile file3 = createMarkdownFile("테스트3");
        
        CategoryInfo subCategory = new CategoryInfo("Java", "개발/Java", 1, List.of(file2, file3), List.of());
        CategoryInfo categoryInfo = new CategoryInfo("개발", "개발", 0, List.of(file1), List.of(subCategory));

        // when
        int totalCount = categoryInfo.getTotalPostCount();

        // then
        assertEquals(3, totalCount); // 현재 카테고리 1개 + 하위 카테고리 2개
    }

    @Test
    void getTotalPostCount_중첩된_하위_카테고리_게시글_포함() {
        // given
        MarkdownFile file1 = createMarkdownFile("테스트1");
        MarkdownFile file2 = createMarkdownFile("테스트2");
        MarkdownFile file3 = createMarkdownFile("테스트3");
        MarkdownFile file4 = createMarkdownFile("테스트4");
        
        CategoryInfo deepSubCategory = new CategoryInfo("스프링", "개발/Java/스프링", 2, List.of(file4), List.of());
        CategoryInfo subCategory = new CategoryInfo("Java", "개발/Java", 1, List.of(file2, file3), List.of(deepSubCategory));
        CategoryInfo categoryInfo = new CategoryInfo("개발", "개발", 0, List.of(file1), List.of(subCategory));

        // when
        int totalCount = categoryInfo.getTotalPostCount();

        // then
        assertEquals(4, totalCount); // 현재 1개 + 하위 2개 + 깊은 하위 1개
    }

    @Test
    void getTotalPostCount_게시글이_없는_경우_0_반환() {
        // given
        CategoryInfo categoryInfo = new CategoryInfo("개발", "개발", 0, List.of(), List.of());

        // when
        int totalCount = categoryInfo.getTotalPostCount();

        // then
        assertEquals(0, totalCount);
    }

    private MarkdownFile createMarkdownFile(String title) {
        return new MarkdownFile(
                new MarkdownProperties(true, title, LocalDate.now(), "", "", ""),
                "테스트 내용",
                title.toLowerCase() + ".html"
        );
    }
}