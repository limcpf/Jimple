package com.jimple.generator.converter;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SimpleMd2HtmlConverterTest {

    /**
     * Tests for the SimpleMd2HtmlConverter class.
     * <p>
     * The convertToHtml method in this class is responsible for converting a
     * given Markdown string into its HTML representation. It returns an empty
     * string if the input is null or empty. The class leverages the CommonMark library
     * for parsing and rendering Markdown text.
     */

    @Test
    void convertToHtml_shouldReturnEmptyString_whenInputIsNull() {
        // Arrange
        SimpleMd2HtmlConverter converter = new SimpleMd2HtmlConverter();

        // Act
        String result = converter.convertToHtml(null);

        // Assert
        assertEquals("", result);
    }

    @Test
    void convertToHtml_shouldReturnEmptyString_whenInputIsEmpty() {
        // Arrange
        SimpleMd2HtmlConverter converter = new SimpleMd2HtmlConverter();

        // Act
        String result = converter.convertToHtml("");

        // Assert
        assertEquals("", result);
    }

    @Test
    void convertToHtml_shouldConvertMarkdownHeadingToHtml() {
        // Arrange
        SimpleMd2HtmlConverter converter = new SimpleMd2HtmlConverter();
        String markdown = "# Heading";

        // Act
        String result = converter.convertToHtml(markdown);

        // Assert
        assertEquals("<h1>Heading</h1>\n", result);
    }

    @Test
    void convertToHtml_shouldConvertMarkdownListToHtml() {
        // Arrange
        SimpleMd2HtmlConverter converter = new SimpleMd2HtmlConverter();
        String markdown = "- Item 1\n- Item 2\n- Item 3";

        // Act
        String result = converter.convertToHtml(markdown);

        // Assert
        assertEquals("<ul>\n<li>Item 1</li>\n<li>Item 2</li>\n<li>Item 3</li>\n</ul>\n", result);
    }

    @Test
    void convertToHtml_shouldConvertBoldTextToHtml() {
        // Arrange
        SimpleMd2HtmlConverter converter = new SimpleMd2HtmlConverter();
        String markdown = "**Bold Text**";

        // Act
        String result = converter.convertToHtml(markdown);

        // Assert
        assertEquals("<p><strong>Bold Text</strong></p>\n", result);
    }

    @Test
    void convertToHtml_shouldConvertItalicTextToHtml() {
        // Arrange
        SimpleMd2HtmlConverter converter = new SimpleMd2HtmlConverter();
        String markdown = "*Italic Text*";

        // Act
        String result = converter.convertToHtml(markdown);

        // Assert
        assertEquals("<p><em>Italic Text</em></p>\n", result);
    }

    @Test
    void convertToHtml_shouldHandleMixedMarkdownElements() {
        // Arrange
        SimpleMd2HtmlConverter converter = new SimpleMd2HtmlConverter();
        String markdown = "# Title\n\nThis is **bold** and *italic* text.\n\n- List item 1\n- List item 2";

        // Act
        String result = converter.convertToHtml(markdown);

        // Assert
        assertEquals("<h1>Title</h1>\n<p>This is <strong>bold</strong> and <em>italic</em> text.</p>\n<ul>\n<li>List item 1</li>\n<li>List item 2</li>\n</ul>\n", result);
    }
}