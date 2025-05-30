package com.jimple.generator;

import com.jimple.generator.converter.Md2HtmlConverter;
import com.jimple.model.MarkdownFile;
import com.jimple.model.MarkdownProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class SimpleMarkdownGeneratorTest {

    private Md2HtmlConverter mockConverter;
    private SimpleMarkdownGenerator generator;

    @BeforeEach
    public void setup() {
        mockConverter = Mockito.mock(Md2HtmlConverter.class);
        generator = new SimpleMarkdownGenerator(mockConverter);
    }

    @Test
    public void generateToHtml_shouldReturnEmptyString_whenFileIsNull() {
        String result = generator.generateToHtml(null);
        assertEquals("", result);
    }

    @Test
    public void generateToHtml_shouldReturnEmptyString_whenFileIsNotPublished() {
        MarkdownFile mockFile = Mockito.mock(MarkdownFile.class);
        when(mockFile.isPublish()).thenReturn(false);

        String result = generator.generateToHtml(mockFile);
        assertEquals("", result);
    }

    @Test
    public void generateToHtml_shouldCombineHeaderAndBody_whenFileIsValid() {
        MarkdownProperties mockProperties = Mockito.mock(MarkdownProperties.class);
        MarkdownFile mockFile = Mockito.mock(MarkdownFile.class);

        when(mockFile.isPublish()).thenReturn(true);
        when(mockFile.properties()).thenReturn(mockProperties);
        when(mockFile.contents()).thenReturn("Test content");
        when(mockConverter.convertHeaderToHtml(mockProperties)).thenReturn("<header>Header</header>");
        when(mockConverter.convertBodyToHtml("Test content")).thenReturn("<p>Test content</p>");

        String result = generator.generateToHtml(mockFile);

        assertEquals("<header>Header</header><p>Test content</p>", result);
    }
}