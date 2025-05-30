package com.jimple.collector;

import com.jimple.finder.MarkdownFinder;
import com.jimple.model.MarkdownFile;
import com.jimple.model.MarkdownProperties;
import com.jimple.parser.extractor.MarkdownExtractor;
import com.jimple.parser.yml.MarkdownYmlParser;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class MarkdownCollectorTest {

    /**
     * Tests for the collectPublishedMarkdowns method in the MarkdownCollector class.
     * <p>
     * The method is responsible for collecting Markdown files from a root directory,
     * extracting their properties and content, and filtering only those files that
     * are marked as publishable. This test class ensures individual functionalities
     * of the method by thoroughly mocking its dependencies.
     */

    @Test
    void testCollectPublishedMarkdownsWithNoMarkdownFiles() {
        MarkdownFinder mockFinder = mock(MarkdownFinder.class);
        MarkdownYmlParser mockParser = mock(MarkdownYmlParser.class);
        MarkdownExtractor mockExtractor = mock(MarkdownExtractor.class);
        MarkdownCollector collector = new MarkdownCollector(mockFinder, mockParser, mockExtractor);

        Path rootDir = Path.of("/test-dir");
        when(mockFinder.findAll(rootDir)).thenReturn(List.of());

        List<MarkdownFile> result = collector.collectPublishedMarkdowns(rootDir);

        assertEquals(0, result.size());
        verify(mockFinder, times(1)).findAll(rootDir);
        verifyNoInteractions(mockParser, mockExtractor);
    }

    @Test
    void testCollectPublishedMarkdownsWithNonPublishedFiles() {
        MarkdownFinder mockFinder = mock(MarkdownFinder.class);
        MarkdownYmlParser mockParser = mock(MarkdownYmlParser.class);
        MarkdownExtractor mockExtractor = mock(MarkdownExtractor.class);
        MarkdownCollector collector = new MarkdownCollector(mockFinder, mockParser, mockExtractor);

        Path mockPath1 = Path.of("/test-dir/file1.md");

        Path rootDir = Path.of("/test-dir");
        when(mockFinder.findAll(rootDir)).thenReturn(List.of(mockPath1));
        when(mockExtractor.extractFullContents(mockPath1)).thenReturn("frontmatter: content");
        when(mockExtractor.extractFrontMatter("frontmatter: content")).thenReturn("frontmatter");
        when(mockExtractor.extractContent("frontmatter: content")).thenReturn("content");

        MarkdownProperties mockProperties = mock(MarkdownProperties.class);
        when(mockParser.getProperties("frontmatter")).thenReturn(mockProperties);
        when(mockProperties.publish()).thenReturn(false);

        List<MarkdownFile> result = collector.collectPublishedMarkdowns(rootDir);

        assertEquals(0, result.size());
        verify(mockFinder, times(1)).findAll(rootDir);
        verify(mockExtractor, times(1)).extractFullContents(mockPath1);
        verify(mockExtractor, times(1)).extractFrontMatter("frontmatter: content");
        verify(mockExtractor, times(1)).extractContent("frontmatter: content");
        verify(mockParser, times(1)).getProperties("frontmatter");
    }

    @Test
    void testCollectPublishedMarkdownsWithPublishedFiles() {
        MarkdownFinder mockFinder = mock(MarkdownFinder.class);
        MarkdownYmlParser mockParser = mock(MarkdownYmlParser.class);
        MarkdownExtractor mockExtractor = mock(MarkdownExtractor.class);
        MarkdownCollector collector = new MarkdownCollector(mockFinder, mockParser, mockExtractor);

        Path mockPath1 = Path.of("/test-dir/file1.md");
        Path mockPath2 = Path.of("/test-dir/file2.md");

        Path rootDir = Path.of("/test-dir");
        when(mockFinder.findAll(rootDir)).thenReturn(List.of(mockPath1, mockPath2));

        when(mockExtractor.extractFullContents(mockPath1)).thenReturn("frontmatter1: content1");
        when(mockExtractor.extractFullContents(mockPath2)).thenReturn("frontmatter2: content2");

        when(mockExtractor.extractFrontMatter("frontmatter1: content1")).thenReturn("frontmatter1");
        when(mockExtractor.extractFrontMatter("frontmatter2: content2")).thenReturn("frontmatter2");

        when(mockExtractor.extractContent("frontmatter1: content1")).thenReturn("content1");
        when(mockExtractor.extractContent("frontmatter2: content2")).thenReturn("content2");

        MarkdownProperties mockProperties1 = mock(MarkdownProperties.class);
        MarkdownProperties mockProperties2 = mock(MarkdownProperties.class);

        when(mockParser.getProperties("frontmatter1")).thenReturn(mockProperties1);
        when(mockParser.getProperties("frontmatter2")).thenReturn(mockProperties2);

        when(mockProperties1.publish()).thenReturn(true);
        when(mockProperties2.publish()).thenReturn(true);

        MarkdownFile expectedFile1 = new MarkdownFile(mockProperties1, "content1", mockPath1.toString());
        MarkdownFile expectedFile2 = new MarkdownFile(mockProperties2, "content2", mockPath2.toString());

        List<MarkdownFile> result = collector.collectPublishedMarkdowns(rootDir);

        assertEquals(2, result.size());
        assertEquals(expectedFile1.getPath(), result.get(0).getPath());
        assertEquals(expectedFile2.getPath(), result.get(1).getPath());

        verify(mockFinder, times(1)).findAll(rootDir);
        verify(mockExtractor, times(1)).extractFullContents(mockPath1);
        verify(mockExtractor, times(1)).extractFullContents(mockPath2);
        verify(mockExtractor, times(1)).extractFrontMatter("frontmatter1: content1");
        verify(mockExtractor, times(1)).extractFrontMatter("frontmatter2: content2");
        verify(mockExtractor, times(1)).extractContent("frontmatter1: content1");
        verify(mockExtractor, times(1)).extractContent("frontmatter2: content2");
        verify(mockParser, times(1)).getProperties("frontmatter1");
        verify(mockParser, times(1)).getProperties("frontmatter2");
    }
}