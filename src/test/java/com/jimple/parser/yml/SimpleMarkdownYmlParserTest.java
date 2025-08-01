package com.jimple.parser.yml;

import com.jimple.model.md.MarkdownProperties;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class SimpleMarkdownYmlParserTest {

    //===== 기본 검증 테스트 =====
    
    /**
     * null 프론트매터가 전달될 경우 예외를 발생시키는지 검증
     */
    @Test
    void testNullFrontmatterThrowsException() {
        SimpleMarkdownYmlParser parser = new SimpleMarkdownYmlParser();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> parser.getProperties(null));
        assertEquals("contents must not be null", exception.getMessage());
    }

    /**
     * 올바른 형식의 프론트매터가 정상적으로 파싱되는지 검증
     */
    @Test
    void testValidFrontmatter() {
        String frontmatter = """
                title: My Title
                publish: true
                date: 2023-10-10
                """;
        SimpleMarkdownYmlParser parser = new SimpleMarkdownYmlParser();
        MarkdownProperties properties = parser.getProperties(frontmatter);

        assertTrue(properties.publish());
        assertEquals("My Title", properties.title());
        assertEquals(LocalDate.of(2023, 10, 10), properties.date());
    }

    /**
     * 빈 프론트매터가 기본값으로 설정되는지 검증
     */
    @Test
    void testEmptyFrontmatter() {
        SimpleMarkdownYmlParser parser = new SimpleMarkdownYmlParser();
        MarkdownProperties properties = parser.getProperties("");

        assertFalse(properties.publish());
        assertEquals("", properties.title());
        assertNull(properties.date());
    }

    /**
     * 잘못된 YAML 형식일 경우 기본값이 반환되는지 검증
     */
    @Test
    void testInvalidYamlFormat() {
        String invalidFrontmatter = """
            title: "My Title
            publish: true
            date: 2023-10-10
            """;  // 따옴표가 닫히지 않음

        SimpleMarkdownYmlParser parser = new SimpleMarkdownYmlParser();
        MarkdownProperties properties = parser.getProperties(invalidFrontmatter);

        // 잘못된 YAML이므로 기본값이 반환되어야 함
        assertFalse(properties.publish());
        assertEquals("", properties.title());
        assertNull(properties.date());
    }

    /**
     * 모든 필드가 정상적으로 파싱되는지 검증
     */
    @Test
    void testAllFieldsParsing() {
        String frontmatter = """
            title: 완전한 예제
            publish: true
            date: 2023-10-10
            description: 이것은 전체 필드를 포함하는 예제입니다
            thumbnailUrl: https://example.com/image.jpg
            """;
        SimpleMarkdownYmlParser parser = new SimpleMarkdownYmlParser();
        MarkdownProperties properties = parser.getProperties(frontmatter);

        assertTrue(properties.publish());
        assertEquals("완전한 예제", properties.title());
        assertEquals(LocalDate.of(2023, 10, 10), properties.date());
        assertEquals("이것은 전체 필드를 포함하는 예제입니다", properties.description());
        assertEquals("https://example.com/image.jpg", properties.thumbnailUrl());
    }

    //===== title 필드 테스트 =====
    
    /**
     * publish가 true인데 title이 없을 경우 예외를 발생시키는지 검증
     */
    @Test
    void testPublishWithoutTitleThrowsException() {
        String frontmatter = """
                publish: true
                date: 2023-10-10
                """;

        SimpleMarkdownYmlParser parser = new SimpleMarkdownYmlParser();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> parser.getProperties(frontmatter));
        assertEquals("title must be set", exception.getMessage());
    }

    //===== date 필드 테스트 =====
    
    /**
     * 잘못된 날짜 형식이 현재 날짜로 설정되는지 검증
     */
    @Test
    void testInvalidDateThrowsException() {
        SimpleMarkdownYmlParser parser = new SimpleMarkdownYmlParser();
        String frontmatter = """
                title: My Title
                publish: true
                date: invalid-date-format
                """;

        MarkdownProperties properties = parser.getProperties(frontmatter);
        assertEquals(LocalDate.now(), properties.date());
    }

    /**
     * 다양한 날짜 형식이 처리되는지 검증
     */
    @Test
    void testDateParsingWithDifferentFormats() {
        String frontmatter = """
            title: My Title
            publish: true
            date: 2023/10/10
            """;
        SimpleMarkdownYmlParser parser = new SimpleMarkdownYmlParser();
        MarkdownProperties properties = parser.getProperties(frontmatter);

        // 날짜 형식이 파싱되지 않을 경우 현재 날짜로 설정되는지 확인
        assertEquals(LocalDate.now(), properties.date());
    }

    @Test
    void testDescriptionParsingWithValidValue() {
        String frontmatter = """
            title: My Title
            publish: true
            date: 2023-10-10
            description: 이것은 테스트 설명입니다
            """;
        SimpleMarkdownYmlParser parser = new SimpleMarkdownYmlParser();
        MarkdownProperties properties = parser.getProperties(frontmatter);

        assertEquals("이것은 테스트 설명입니다", properties.description());
    }

    @Test
    void testDescriptionParsingWithNonStringValue() {
        String frontmatter = """
            title: My Title
            publish: true
            date: 2023-10-10
            description: 123
            """;
        SimpleMarkdownYmlParser parser = new SimpleMarkdownYmlParser();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> parser.getProperties(frontmatter));
        assertEquals("description must be a string", exception.getMessage());
    }

    @Test
    void testPublishWithNonBooleanValue() {
        String frontmatter = """
            title: My Title
            publish: sss
            date: 2023-10-10
            """;
        SimpleMarkdownYmlParser parser = new SimpleMarkdownYmlParser();
        MarkdownProperties properties = parser.getProperties(frontmatter);

        // 문자열 "yes"는 boolean으로 인식되지 않아야 함
        assertFalse(properties.publish());
    }

    @Test
    void testThumbnailUrlParsing() {
        String frontmatter = """
            title: My Title
            publish: true
            date: 2023-10-10
            thumbnailUrl: https://example.com/image.jpg
            """;
        SimpleMarkdownYmlParser parser = new SimpleMarkdownYmlParser();
        MarkdownProperties properties = parser.getProperties(frontmatter);

        assertEquals("https://example.com/image.jpg", properties.thumbnailUrl());
        assertEquals("", properties.path()); // path가 없는 경우 빈 문자열
    }

    @Test
    void testMissingThumbnailUrl() {
        String frontmatter = """
            title: My Title
            publish: true
            date: 2023-10-10
            """;
        SimpleMarkdownYmlParser parser = new SimpleMarkdownYmlParser();
        MarkdownProperties properties = parser.getProperties(frontmatter);

        // thumbnailUrl이 없는 경우 빈 문자열이 반환되어야 함
        assertEquals("", properties.thumbnailUrl());
    }

    @Test
    void testInvalidThumbnailUrl() {
        String frontmatter = """
            title: My Title
            publish: true
            date: 2023-10-10
            thumbnailUrl: 12345
            """;
        SimpleMarkdownYmlParser parser = new SimpleMarkdownYmlParser();
        MarkdownProperties properties = parser.getProperties(frontmatter);

        // 문자열이 아닌 값은 빈 문자열로 처리되어야 함
        assertEquals("", properties.thumbnailUrl());
    }

    //===== path 필드 테스트 =====

    @Test
    void testPathParsingWithValidValue() {
        String frontmatter = """
            title: My Title
            publish: true
            date: 2023-10-10
            path: 개발/Java/스프링
            """;
        SimpleMarkdownYmlParser parser = new SimpleMarkdownYmlParser();
        MarkdownProperties properties = parser.getProperties(frontmatter);

        assertEquals("개발/Java/스프링", properties.path());
    }

    @Test
    void testMissingPath() {
        String frontmatter = """
            title: My Title
            publish: true
            date: 2023-10-10
            """;
        SimpleMarkdownYmlParser parser = new SimpleMarkdownYmlParser();
        MarkdownProperties properties = parser.getProperties(frontmatter);

        // path가 없는 경우 빈 문자열이 반환되어야 함
        assertEquals("", properties.path());
    }

    @Test
    void testPathWithSlashes() {
        String frontmatter = """
            title: My Title
            publish: true
            date: 2023-10-10
            path: /개발/Java/스프링/
            """;
        SimpleMarkdownYmlParser parser = new SimpleMarkdownYmlParser();
        MarkdownProperties properties = parser.getProperties(frontmatter);

        assertEquals("/개발/Java/스프링/", properties.path());
    }

    @Test
    void testInvalidPathType() {
        String frontmatter = """
            title: My Title
            publish: true
            date: 2023-10-10
            path: 12345
            """;
        SimpleMarkdownYmlParser parser = new SimpleMarkdownYmlParser();
        MarkdownProperties properties = parser.getProperties(frontmatter);

        // 문자열이 아닌 값은 빈 문자열로 처리되어야 함
        assertEquals("", properties.path());
    }

    @Test
    void testAllFieldsWithPath() {
        String frontmatter = """
            title: 완전한 예제
            publish: true
            date: 2023-10-10
            description: 이것은 전체 필드를 포함하는 예제입니다
            thumbnailUrl: https://example.com/image.jpg
            path: 개발/Java/스프링부트
            """;
        SimpleMarkdownYmlParser parser = new SimpleMarkdownYmlParser();
        MarkdownProperties properties = parser.getProperties(frontmatter);

        assertTrue(properties.publish());
        assertEquals("완전한 예제", properties.title());
        assertEquals(LocalDate.of(2023, 10, 10), properties.date());
        assertEquals("이것은 전체 필드를 포함하는 예제입니다", properties.description());
        assertEquals("https://example.com/image.jpg", properties.thumbnailUrl());
        assertEquals("개발/Java/스프링부트", properties.path());
    }
}