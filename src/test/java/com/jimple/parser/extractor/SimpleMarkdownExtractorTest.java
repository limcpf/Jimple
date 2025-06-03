package com.jimple.parser.extractor;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SimpleMarkdownExtractorTest {

    /**
     * Tests for the extractFrontMatter method in SimpleMarkdownExtractor.
     * This method processes a markdown file's content to extract the
     * "front matter" section, which is typically enclosed between two
     * delimiter lines ("---").
     */

    @Test
    void testExtractFrontMatterWithValidFrontMatter() {
        // Arrange
        String markdownContent = "---\nkey: value\nanotherKey: anotherValue\n---\nSome content here";
        SimpleMarkdownExtractor extractor = new SimpleMarkdownExtractor();

        // Act
        String result = extractor.extractFrontMatter(markdownContent);

        // Assert
        assertEquals("key: value\nanotherKey: anotherValue", result);
    }

    @Test
    void testExtractFrontMatterWithNoDelimiters() {
        // Arrange
        String markdownContent = "key: value\nanotherKey: anotherValue\nSome content here";
        SimpleMarkdownExtractor extractor = new SimpleMarkdownExtractor();

        // Act
        String result = extractor.extractFrontMatter(markdownContent);

        // Assert
        assertEquals("", result);
    }

    @Test
    void testExtractFrontMatterWithOnlyOneDelimiter() {
        // Arrange
        String markdownContent = "---\nkey: value\nanotherKey: anotherValue";
        SimpleMarkdownExtractor extractor = new SimpleMarkdownExtractor();

        // Act
        String result = extractor.extractFrontMatter(markdownContent);

        // Assert
        assertEquals("", result);
    }

    @Test
    void testExtractFrontMatterWithEmptyContent() {
        // Arrange
        String markdownContent = "";
        SimpleMarkdownExtractor extractor = new SimpleMarkdownExtractor();

        // Act
        String result = extractor.extractFrontMatter(markdownContent);

        // Assert
        assertEquals("", result);
    }

    @Test
    void testExtractFrontMatterWithNullContent() {
        // Arrange
        String markdownContent = null;
        SimpleMarkdownExtractor extractor = new SimpleMarkdownExtractor();

        // Act
        String result = extractor.extractFrontMatter(markdownContent);

        // Assert
        assertEquals("", result);
    }

    @Test
    void testExtractFrontMatterWithContentWithoutBOM() {
        // Arrange
        String markdownContent = "\uFEFF---\nkey: value\n---\nContent without BOM";
        SimpleMarkdownExtractor extractor = new SimpleMarkdownExtractor();

        // Act
        String result = extractor.extractFrontMatter(markdownContent);

        // Assert
        assertEquals("key: value", result);
    }

    @Test
    void testExtractFrontMatterWithWhitespaceContent() {
        // Arrange
        String markdownContent = "   \n   ";
        SimpleMarkdownExtractor extractor = new SimpleMarkdownExtractor();

        // Act
        String result = extractor.extractFrontMatter(markdownContent);

        // Assert
        assertEquals("", result);
    }
}