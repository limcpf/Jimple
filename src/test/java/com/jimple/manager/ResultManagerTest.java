package com.jimple.manager;

import com.jimple.collector.MarkdownCollector;
import com.jimple.generator.MarkdownGenerator;
import com.jimple.model.MarkdownFile;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ResultManagerTest {

    /**
     * Tests for the processAndSaveResults method in the ResultManager class.
     * This method collects published markdown files, converts them into HTML using a generator,
     * and saves them to the specified result directory.
     */

    @Test
    void shouldProcessAndSaveHtmlSuccessfully() throws IOException {
        // Arrange
        Path sourceDir = Files.createTempDirectory("sourceDir");
        Path resultDir = Files.createTempDirectory("resultDir");

        MarkdownCollector collectorMock = mock(MarkdownCollector.class);
        MarkdownGenerator generatorMock = mock(MarkdownGenerator.class);

        MarkdownFile markdownFile = new MarkdownFile(null, "# Test", sourceDir.resolve("test.md").toString());
        when(collectorMock.collectPublishedMarkdowns(sourceDir)).thenReturn(List.of(markdownFile));

        String htmlContent = "<html><body>Test Content</body></html>";
        when(generatorMock.generateToHtml(markdownFile)).thenReturn(htmlContent);

        Files.writeString(sourceDir.resolve("test.md"), "# Test");

        ResultManager resultManager = new ResultManager(collectorMock, generatorMock, resultDir);

        // Act
        resultManager.processAndSaveResults(sourceDir);

        // Assert
        Path expectedHtmlFile = resultDir.resolve("test.html");
        assertTrue(Files.exists(expectedHtmlFile));
        assertEquals(htmlContent, Files.readString(expectedHtmlFile));

        verify(collectorMock).collectPublishedMarkdowns(sourceDir);
        verify(generatorMock).generateToHtml(markdownFile);
    }

    @Test
    void shouldNotSaveHtmlWhenGeneratedContentIsEmpty() throws IOException {
        // Arrange
        Path sourceDir = Files.createTempDirectory("sourceDir");
        Path resultDir = Files.createTempDirectory("resultDir");

        MarkdownCollector collectorMock = mock(MarkdownCollector.class);
        MarkdownGenerator generatorMock = mock(MarkdownGenerator.class);

        MarkdownFile markdownFile = new MarkdownFile(null, "# Test", sourceDir.resolve("test.md").toString());
        when(collectorMock.collectPublishedMarkdowns(sourceDir)).thenReturn(List.of(markdownFile));

        when(generatorMock.generateToHtml(markdownFile)).thenReturn("");

        Files.writeString(sourceDir.resolve("empty.md"), "# Empty");

        ResultManager resultManager = new ResultManager(collectorMock, generatorMock, resultDir);

        // Act
        resultManager.processAndSaveResults(sourceDir);

        // Assert
        Path expectedHtmlFile = resultDir.resolve("empty.html");
        assertFalse(Files.exists(expectedHtmlFile));

        verify(collectorMock).collectPublishedMarkdowns(sourceDir);
        verify(generatorMock).generateToHtml(markdownFile);
    }

    @Test
    void shouldHandleNoMarkdownFilesGracefully() throws IOException {
        // Arrange
        Path sourceDir = Files.createTempDirectory("sourceDir");
        Path resultDir = Files.createTempDirectory("resultDir");

        MarkdownCollector collectorMock = mock(MarkdownCollector.class);
        MarkdownGenerator generatorMock = mock(MarkdownGenerator.class);

        when(collectorMock.collectPublishedMarkdowns(sourceDir)).thenReturn(Collections.emptyList());

        ResultManager resultManager = new ResultManager(collectorMock, generatorMock, resultDir);

        // Act
        resultManager.processAndSaveResults(sourceDir);

        // Assert
        verify(collectorMock).collectPublishedMarkdowns(sourceDir);
        verifyNoInteractions(generatorMock);
    }

    @Test
    void shouldThrowRuntimeExceptionForInvalidResultDir() {
        // Arrange
        Path invalidResultDir = Path.of("/invalid/path");
        MarkdownCollector collectorMock = mock(MarkdownCollector.class);
        MarkdownGenerator generatorMock = mock(MarkdownGenerator.class);

        // Act and Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> new ResultManager(collectorMock, generatorMock, invalidResultDir));
        assertTrue(exception.getMessage().contains("결과 디렉토리를 생성할 수 없습니다"));
    }
}