package com.jimple.parser.yml;

import com.jimple.model.MarkdownProperties;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class MarkdownYmlParserTest {

    /**
     * Tests the getProperties method of MarkdownYmlParser.
     * <p>
     * This method parses YAML front matter and converts it into a MarkdownProperties object.
     * It extracts the "title", "publish", and "date" fields from the YAML content.
     * <p>
     * The method throws an IllegalArgumentException if front matter is missing,
     * if the "title" is absent, or if the "date" is of an invalid format.
     */


    @Test
    void testNullFrontmatterThrowsException() {
        MarkdownYmlParser parser = new MarkdownYmlParser();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> parser.getProperties(null));
        assertEquals("contents must not be null", exception.getMessage());
    }

    @Test
    void testValidFrontmatter() {
        String frontmatter = """
                title: My Title
                publish: true
                date: 2023-10-10
                """;
        MarkdownYmlParser parser = new MarkdownYmlParser();
        MarkdownProperties properties = parser.getProperties(frontmatter);

        assertTrue(properties.publish());
        assertEquals("My Title", properties.title());
        assertEquals(LocalDate.of(2023, 10, 10), properties.date());
    }

    @Test
    void testEmptyFrontmatter() {
        MarkdownYmlParser parser = new MarkdownYmlParser();
        MarkdownProperties properties = parser.getProperties("");

        assertFalse(properties.publish());
        assertEquals("", properties.title());
        assertEquals(LocalDate.now(), properties.date());
    }

    @Test
    void testPublishWithoutTitleThrowsException() {
        String frontmatter = """
                publish: true
                date: 2023-10-10
                """;

        MarkdownYmlParser parser = new MarkdownYmlParser();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> parser.getProperties(frontmatter));
        assertEquals("title must be set", exception.getMessage());
    }

    @Test
    void testInvalidDateThrowsException() {
        MarkdownYmlParser parser = new MarkdownYmlParser();
        String frontmatter = """
                title: My Title
                publish: true
                date: invalid-date-format
                """;

        MarkdownProperties properties = parser.getProperties(frontmatter);
        assertEquals(LocalDate.now(), properties.date());
    }
}