package com.jimple.parser.yml;

import com.jimple.model.MarkdownProperties;
import com.jimple.parser.yml.MarkdownYmlParser;
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
    void testGetProperties_ValidInput_ReturnsMarkdownProperties() {
        // Arrange
        String frontmatter = "title: Sample Title\npublish: true\ndate: 2023-12-01";
        MarkdownYmlParser parser = new MarkdownYmlParser();

        // Act
        MarkdownProperties properties = parser.getProperties(frontmatter);

        // Assert
        assertNotNull(properties);
        assertEquals("Sample Title", properties.title());
        assertTrue(properties.publish());
        assertEquals(LocalDate.of(2023, 12, 1), properties.date());
    }

    @Test
    void testGetProperties_TitleMissing_ThrowsException() {
        // Arrange
        String frontmatter = "publish: true\ndate: 2023-12-01";
        MarkdownYmlParser parser = new MarkdownYmlParser();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> parser.getProperties(frontmatter));
        assertEquals("title must be set", exception.getMessage());
    }

    @Test
    void testGetProperties_InvalidDateFormat_ThrowsException() {
        // Arrange
        String frontmatter = "title: Sample Title\npublish: true\ndate: InvalidDate";
        MarkdownYmlParser parser = new MarkdownYmlParser();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> parser.getProperties(frontmatter));
        assertEquals("date must be in yyyy-MM-dd format", exception.getMessage());
    }

    @Test
    void testGetProperties_EmptyInput_ThrowsException() {
        // Arrange
        String frontmatter = "";
        MarkdownYmlParser parser = new MarkdownYmlParser();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> parser.getProperties(frontmatter));
        assertEquals("YAML Front Matter not found", exception.getMessage());
    }

    @Test
    void testGetProperties_NullInput_ThrowsException() {
        // Arrange
        String frontmatter = null;
        MarkdownYmlParser parser = new MarkdownYmlParser();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> parser.getProperties(frontmatter));
        assertEquals("contents must not be null", exception.getMessage());
    }

    @Test
    void testGetProperties_PublishMissing_DefaultsToFalse() {
        // Arrange
        String frontmatter = "title: Sample Title\ndate: 2023-12-01";
        MarkdownYmlParser parser = new MarkdownYmlParser();

        // Act
        MarkdownProperties properties = parser.getProperties(frontmatter);

        // Assert
        assertNotNull(properties);
        assertEquals("Sample Title", properties.title());
        assertFalse(properties.publish());
        assertEquals(LocalDate.of(2023, 12, 1), properties.date());
    }

    @Test
    void testGetProperties_NullDate_DefaultsToCurrentDate() {
        // Arrange
        String frontmatter = "title: Sample Title\npublish: true";
        MarkdownYmlParser parser = new MarkdownYmlParser();

        // Act
        MarkdownProperties properties = parser.getProperties(frontmatter);

        // Assert
        assertNotNull(properties);
        assertEquals("Sample Title", properties.title());
        assertTrue(properties.publish());
        assertEquals(LocalDate.now(), properties.date());
    }
}