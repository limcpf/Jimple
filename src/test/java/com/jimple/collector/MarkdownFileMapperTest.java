
package com.jimple.collector;

import com.jimple.model.md.MarkdownFile;
import com.jimple.model.md.MarkdownProperties;
import com.jimple.parser.extractor.MarkdownExtractor;
import com.jimple.parser.yml.SimpleMarkdownYmlParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("MarkdownFileMapper 테스트")
class MarkdownFileMapperTest {

    @Mock
    private SimpleMarkdownYmlParser parser;

    @Mock
    private MarkdownExtractor extractor;

    private MarkdownFileMapper markdownFileMapper;

    @BeforeEach
    void setUp() {
        parser = mock(SimpleMarkdownYmlParser.class);
        extractor = mock(MarkdownExtractor.class);
        markdownFileMapper = new MarkdownFileMapper(parser, extractor);
    }

    @Test
    @DisplayName("생성자 - 정상적으로 의존성 주입")
    void constructor_ShouldInjectDependencies() {
        // given & when
        MarkdownFileMapper mapper = new MarkdownFileMapper(parser, extractor);

        // then
        assertNotNull(mapper);
    }

    @Test
    @DisplayName("생성자 - null parser로 생성시 NullPointerException 발생")
    void constructor_WithNullParser_ShouldThrowException() {
        // given & when & then
        assertThrows(NullPointerException.class, () ->
                new MarkdownFileMapper(null, extractor));
    }

    @Test
    @DisplayName("생성자 - null extractor로 생성시 NullPointerException 발생")
    void constructor_WithNullExtractor_ShouldThrowException() {
        // given & when & then
        assertThrows(NullPointerException.class, () ->
                new MarkdownFileMapper(parser, null));
    }

    @Test
    @DisplayName("generateMarkdownFile - 정상적인 마크다운 파일 생성")
    void generateMarkdownFile_WithValidPath_ShouldReturnMarkdownFile() {
        // given
        Path testPath = Paths.get("test.md");
        String fullContents = "---\ntitle: Test Title\npublish: true\n---\n# Content";
        String frontmatter = "title: Test Title\npublish: true";
        String content = "# Content";
        MarkdownProperties properties = new MarkdownProperties(true, "Test Title", null);

        when(extractor.extractFullContents(testPath)).thenReturn(fullContents);
        when(extractor.extractFrontMatter(fullContents)).thenReturn(frontmatter);
        when(extractor.extractContent(fullContents)).thenReturn(content);
        when(parser.getProperties(frontmatter)).thenReturn(properties);

        // when
        MarkdownFile result = markdownFileMapper.generateMarkdownFile(testPath);

        // then
        assertNotNull(result);
        assertEquals(properties, result.properties());
        assertEquals(content, result.contents());
        assertEquals("Test-Title.html", result.path());

        verify(extractor).extractFullContents(testPath);
        verify(extractor).extractFrontMatter(fullContents);
        verify(extractor).extractContent(fullContents);
        verify(parser).getProperties(frontmatter);
    }

    @Test
    @DisplayName("generateMarkdownFile - 공백이 포함된 제목 처리")
    void generateMarkdownFile_WithSpacesInTitle_ShouldReplaceWithHyphens() {
        // given
        Path testPath = Paths.get("test.md");
        String fullContents = "---\ntitle: My Great Article\n---\n# Content";
        String frontmatter = "title: My Great Article";
        String content = "# Content";
        MarkdownProperties properties = new MarkdownProperties(true, "My Great Article", null);

        when(extractor.extractFullContents(testPath)).thenReturn(fullContents);
        when(extractor.extractFrontMatter(fullContents)).thenReturn(frontmatter);
        when(extractor.extractContent(fullContents)).thenReturn(content);
        when(parser.getProperties(frontmatter)).thenReturn(properties);

        // when
        MarkdownFile result = markdownFileMapper.generateMarkdownFile(testPath);

        // then
        assertEquals("My-Great-Article.html", result.path());
    }

    @Test
    @DisplayName("generateMarkdownFile - 여러 공백 처리")
    void generateMarkdownFile_WithMultipleSpaces_ShouldReplaceWithSingleHyphen() {
        // given
        Path testPath = Paths.get("test.md");
        String fullContents = "---\ntitle: Title   With    Multiple     Spaces\n---\n# Content";
        String frontmatter = "title: Title   With    Multiple     Spaces";
        String content = "# Content";
        MarkdownProperties properties = new MarkdownProperties(true, "Title   With    Multiple     Spaces", null);

        when(extractor.extractFullContents(testPath)).thenReturn(fullContents);
        when(extractor.extractFrontMatter(fullContents)).thenReturn(frontmatter);
        when(extractor.extractContent(fullContents)).thenReturn(content);
        when(parser.getProperties(frontmatter)).thenReturn(properties);

        // when
        MarkdownFile result = markdownFileMapper.generateMarkdownFile(testPath);

        // then
        assertEquals("Title-With-Multiple-Spaces.html", result.path());
    }

    @Test
    @DisplayName("collectPublishedMarkdownFiles - 게시된 파일들만 필터링")
    void collectPublishedMarkdownFiles_ShouldFilterPublishedFiles() {
        // given
        Path path1 = Paths.get("published.md");
        Path path2 = Paths.get("draft.md");
        Path path3 = Paths.get("another-published.md");
        List<Path> paths = Arrays.asList(path1, path2, path3);

        // Mock for published file 1
        setupMockForPath(path1, "Published Title", true, "Published content");

        // Mock for draft file
        setupMockForPath(path2, "Draft Title", false, "Draft content");

        // Mock for published file 2
        setupMockForPath(path3, "Another Published", true, "Another content");

        // when
        List<MarkdownFile> result = markdownFileMapper.collectPublishedMarkdownFiles(paths);

        // then
        assertEquals(2, result.size());
        assertEquals("Published-Title.html", result.get(0).path());
        assertEquals("Another-Published.html", result.get(1).path());

        // Verify all paths were processed
        verify(extractor, times(3)).extractFullContents(any(Path.class));
    }

    @Test
    @DisplayName("collectPublishedMarkdownFiles - 빈 리스트 처리")
    void collectPublishedMarkdownFiles_WithEmptyList_ShouldReturnEmptyList() {
        // given
        List<Path> emptyPaths = Arrays.asList();

        // when
        List<MarkdownFile> result = markdownFileMapper.collectPublishedMarkdownFiles(emptyPaths);

        // then
        assertTrue(result.isEmpty());
        verifyNoInteractions(extractor);
        verifyNoInteractions(parser);
    }

    @Test
    @DisplayName("collectPublishedMarkdownFiles - 모든 파일이 미게시 상태")
    void collectPublishedMarkdownFiles_WithAllDraftFiles_ShouldReturnEmptyList() {
        // given
        Path path1 = Paths.get("draft1.md");
        Path path2 = Paths.get("draft2.md");
        List<Path> paths = Arrays.asList(path1, path2);

        setupMockForPath(path1, "Draft 1", false, "Draft content 1");
        setupMockForPath(path2, "Draft 2", false, "Draft content 2");

        // when
        List<MarkdownFile> result = markdownFileMapper.collectPublishedMarkdownFiles(paths);

        // then
        assertTrue(result.isEmpty());
        verify(extractor, times(2)).extractFullContents(any(Path.class));
    }

    @Test
    @DisplayName("collectPublishedMarkdownFiles - null 리스트 처리")
    void collectPublishedMarkdownFiles_WithNullList_ShouldThrowException() {
        // given & when & then
        assertThrows(NullPointerException.class, () ->
                markdownFileMapper.collectPublishedMarkdownFiles(null));
    }

    @Test
    @DisplayName("generateMarkdownFile - extractor 예외 발생시 전파")
    void generateMarkdownFile_WhenExtractorThrowsException_ShouldPropagateException() {
        // given
        Path testPath = Paths.get("test.md");
        when(extractor.extractFullContents(testPath))
                .thenThrow(new RuntimeException("Extract failed"));

        // when & then
        assertThrows(RuntimeException.class, () ->
                markdownFileMapper.generateMarkdownFile(testPath));
    }

    @Test
    @DisplayName("generateMarkdownFile - parser 예외 발생시 전파")
    void generateMarkdownFile_WhenParserThrowsException_ShouldPropagateException() {
        // given
        Path testPath = Paths.get("test.md");
        String fullContents = "---\ntitle: Test\n---\nContent";
        String frontmatter = "title: Test";

        when(extractor.extractFullContents(testPath)).thenReturn(fullContents);
        when(extractor.extractFrontMatter(fullContents)).thenReturn(frontmatter);
        when(extractor.extractContent(fullContents)).thenReturn("Content");
        when(parser.getProperties(frontmatter))
                .thenThrow(new RuntimeException("Parse failed"));

        // when & then
        assertThrows(RuntimeException.class, () ->
                markdownFileMapper.generateMarkdownFile(testPath));
    }

    @Test
    @DisplayName("generateMarkdownFile - 특수문자가 포함된 제목 처리")
    void generateMarkdownFile_WithSpecialCharactersInTitle_ShouldOnlyReplaceSpaces() {
        // given
        Path testPath = Paths.get("test.md");
        String fullContents = "---\ntitle: Title-with_special@chars#\n---\n# Content";
        String frontmatter = "title: Title-with_special@chars#";
        String content = "# Content";
        MarkdownProperties properties = new MarkdownProperties(true, "Title-with_special@chars#", null);

        when(extractor.extractFullContents(testPath)).thenReturn(fullContents);
        when(extractor.extractFrontMatter(fullContents)).thenReturn(frontmatter);
        when(extractor.extractContent(fullContents)).thenReturn(content);
        when(parser.getProperties(frontmatter)).thenReturn(properties);

        // when
        MarkdownFile result = markdownFileMapper.generateMarkdownFile(testPath);

        // then
        assertEquals("Title-with_special@chars#.html", result.path());
    }

    private void setupMockForPath(Path path, String title, boolean isPublished, String content) {
        String fullContents = "---\ntitle: " + title + "\npublish: " + isPublished + "\n---\n" + content;
        String frontmatter = "title: " + title + "\npublish: " + isPublished;
        MarkdownProperties properties = new MarkdownProperties(isPublished, title, null);

        when(extractor.extractFullContents(path)).thenReturn(fullContents);
        when(extractor.extractFrontMatter(fullContents)).thenReturn(frontmatter);
        when(extractor.extractContent(fullContents)).thenReturn(content);
        when(parser.getProperties(frontmatter)).thenReturn(properties);
    }
}