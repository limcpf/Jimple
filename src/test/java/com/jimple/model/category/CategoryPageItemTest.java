package com.jimple.model.category;

import com.jimple.model.md.MarkdownFile;
import com.jimple.model.md.MarkdownProperties;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * CategoryPageItem 클래스에 대한 단위 테스트
 */
class CategoryPageItemTest {

    @Test
    void MarkdownFile로부터_CategoryPageItem_생성_테스트() {
        // given
        LocalDate testDate = LocalDate.of(2025, 7, 30);
        MarkdownProperties properties = new MarkdownProperties(
                true,
                "테스트 제목",
                testDate,
                "테스트 설명",
                "https://example.com/thumbnail.jpg",
                "개발/Java"
        );
        MarkdownFile file = new MarkdownFile(properties, "테스트 내용", "test.html");

        // when
        CategoryPageItem item = new CategoryPageItem(file);

        // then
        assertEquals("테스트 제목", item.title());
        assertEquals("test.html", item.path());
        assertEquals("2025-07-30", item.date());
        assertEquals("개발/Java", item.categoryPath());
        assertEquals("테스트 설명", item.description());
    }

    @Test
    void 빈_description이_있는_MarkdownFile로부터_생성_테스트() {
        // given
        LocalDate testDate = LocalDate.of(2025, 7, 30);
        MarkdownProperties properties = new MarkdownProperties(
                true,
                "테스트 제목",
                testDate,
                "", // 빈 설명
                "",
                "개발"
        );
        MarkdownFile file = new MarkdownFile(properties, "테스트 내용", "test.html");

        // when
        CategoryPageItem item = new CategoryPageItem(file);

        // then
        assertEquals("테스트 제목", item.title());
        assertEquals("test.html", item.path());
        assertEquals("2025-07-30", item.date());
        assertEquals("개발", item.categoryPath());
        assertEquals("", item.description());
    }

    @Test
    void 빈_categoryPath가_있는_MarkdownFile로부터_생성_테스트() {
        // given
        LocalDate testDate = LocalDate.of(2025, 7, 30);
        MarkdownProperties properties = new MarkdownProperties(
                true,
                "테스트 제목",
                testDate,
                "테스트 설명",
                "",
                "" // 빈 카테고리 경로
        );
        MarkdownFile file = new MarkdownFile(properties, "테스트 내용", "test.html");

        // when
        CategoryPageItem item = new CategoryPageItem(file);

        // then
        assertEquals("테스트 제목", item.title());
        assertEquals("test.html", item.path());
        assertEquals("2025-07-30", item.date());
        assertEquals("", item.categoryPath());
        assertEquals("테스트 설명", item.description());
    }

    @Test
    void 직접_생성자로_CategoryPageItem_생성_테스트() {
        // given & when
        CategoryPageItem item = new CategoryPageItem(
                "직접생성 제목",
                "direct.html",
                "2025-07-30",
                "개발/Java/스프링",
                "직접생성 설명"
        );

        // then
        assertEquals("직접생성 제목", item.title());
        assertEquals("direct.html", item.path());
        assertEquals("2025-07-30", item.date());
        assertEquals("개발/Java/스프링", item.categoryPath());
        assertEquals("직접생성 설명", item.description());
    }
}