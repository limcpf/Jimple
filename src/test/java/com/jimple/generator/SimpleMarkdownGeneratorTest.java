package com.jimple.generator;

import com.jimple.generator.converter.Md2HtmlConverter;
import com.jimple.model.config.BlogProperties;
import com.jimple.model.md.MarkdownFile;
import com.jimple.model.md.MarkdownProperties;
import com.jimple.parser.template.SimpleTemplateEngine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

public class SimpleMarkdownGeneratorTest {
    private Md2HtmlConverter mockConverter;
    private SimpleMarkdownGenerator generator;
    private SimpleTemplateEngine mockTemplateEngine;

    @BeforeEach
    public void setup() {
        mockConverter = Mockito.mock(Md2HtmlConverter.class);
        mockTemplateEngine = Mockito.mock(SimpleTemplateEngine.class);
        generator = new SimpleMarkdownGenerator(mockConverter, new BlogProperties(), mockTemplateEngine);
    }

    private MarkdownFile createMockMarkdownFile() {
        MarkdownProperties props = new MarkdownProperties(true, "Test Title", LocalDate.now());
        return new MarkdownFile(props, "Markdown content", "test.md");
    }

    @Test
    public void testGenerateToHtml_validOutput() throws IOException {
        // Arrange
        MarkdownFile mockFile = createMockMarkdownFile();
        when(mockConverter.convertBodyToHtml(mockFile.contents())).thenReturn("<p>Converted HTML</p>");
        when(mockTemplateEngine.loadTemplate("templates/article.html")).thenReturn("<html>{{content}}</html>");
        when(mockTemplateEngine.processTemplate(Mockito.anyString(), Mockito.anyMap())).thenReturn("<html><p>Converted HTML</p></html>");

        // Act
        String result = generator.generateToHtml(mockFile);

        // Assert
        assertTrue(result.contains("<html>"));
        assertTrue(result.contains("<p>Converted HTML</p>"));
    }

    @Test
    public void testGenerateToHtml_loadTemplateThrowsException() throws IOException {
        // Arrange
        MarkdownFile mockFile = createMockMarkdownFile();
        when(mockConverter.convertBodyToHtml(mockFile.contents())).thenReturn("<p>Converted HTML</p>");
        when(mockTemplateEngine.loadTemplate("templates/article.html")).thenThrow(new IOException("Template Error"));

        // Act
        String result = generator.generateToHtml(mockFile);

        // Assert
        assertTrue(result.contains("<!DOCTYPE html>"));
        assertTrue(result.contains("<p>Converted HTML</p>"));
    }

    @Test
    public void testGenerateToHtml_generateProfileSectionThrowsException() throws Exception {
        // Arrange
        MarkdownFile mockFile = createMockMarkdownFile();
        when(mockConverter.convertBodyToHtml(mockFile.contents())).thenReturn("<p>Converted HTML</p>");
        doThrow(new IOException("Profile Section Error")).when(mockTemplateEngine).loadTemplate("templates/profile.html");

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> generator.generateToHtml(mockFile));
        assertEquals("템플릿 로드 중 오류 발생", exception.getMessage());
    }
}