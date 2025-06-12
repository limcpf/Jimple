package com.jimple.generator;

import com.jimple.generator.converter.Md2HtmlConverter;
import com.jimple.model.config.BlogProperties;
import com.jimple.model.md.MarkdownFile;
import com.jimple.model.md.MarkdownProperties;
import com.jimple.parser.template.SimpleTemplateEngine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class SimpleMarkdownGeneratorTest {
    private SimpleMarkdownGenerator markdownGenerator;
    private Md2HtmlConverter mockConverter;
    private BlogProperties mockConfig;
    private SimpleTemplateEngine mockTemplateEngine;
    private MarkdownFile mockMarkdownFile;
    private MarkdownProperties mockMarkdownProperties;
    private BlogProperties.ProfileConfig mockProfileProperties;
    private BlogProperties.LayoutConfig mockLayoutProperties;

    @Nested
    @DisplayName("GenerateLatestArticle")
    class GenerateLatestArticleTests {

        @BeforeEach
        void setUp() {
            mockConverter = Mockito.mock(Md2HtmlConverter.class);
            mockConfig = Mockito.mock(BlogProperties.class);
            mockTemplateEngine = Mockito.mock(SimpleTemplateEngine.class);
            markdownGenerator = new SimpleMarkdownGenerator(mockConverter, mockConfig, mockTemplateEngine);

            mockMarkdownProperties = Mockito.mock(MarkdownProperties.class);
            mockMarkdownFile = new MarkdownFile(mockMarkdownProperties, "dummy content", "dummy/path");
            mockProfileProperties = Mockito.mock(BlogProperties.ProfileConfig.class);
            mockLayoutProperties = Mockito.mock(BlogProperties.LayoutConfig.class);

            when(mockConfig.profile()).thenReturn(mockProfileProperties);
            when(mockProfileProperties.name()).thenReturn("John Doe");
        }

        @Test
        void testGenerateLatestArticleValidMarkdownFile() throws IOException {
            when(mockTemplateEngine.loadTemplate("templates/latest-article.html"))
                    .thenReturn("<html><body>${titleString}</body></html>");
            when(mockMarkdownProperties.date()).thenReturn(LocalDate.now());
            when(mockMarkdownProperties.title()).thenReturn("Sample Title");
            when(mockMarkdownProperties.description()).thenReturn("Sample Description");
            when(mockConfig.profile().name()).thenReturn("John Doe");
            when(mockMarkdownProperties.thumbnailUrl()).thenReturn("dummy-thumbnail-url");
            when(mockTemplateEngine.processTemplate(eq("<html><body>${titleString}</body></html>"), anyMap()))
                    .thenReturn("<html><body>Sample Title</body></html>");

            String result = markdownGenerator.generateLatestArticle(mockMarkdownFile);

            assertNotNull(result);
            assertTrue(result.contains("Sample Title"));
        }

        @Test
        void testGenerateLatestArticleWithIOException() throws IOException {
            when(mockTemplateEngine.loadTemplate("templates/latest-article.html"))
                    .thenThrow(new IOException("Template load error"));

            String result = markdownGenerator.generateLatestArticle(mockMarkdownFile);

            assertEquals("", result);
        }
    }

    @Nested
    @DisplayName("GenerateToHtml")
    class GenerateToHtmlTests {
        @BeforeEach
        void setUp() {
            mockConverter = Mockito.mock(Md2HtmlConverter.class);
            mockConfig = Mockito.mock(BlogProperties.class);
            mockTemplateEngine = Mockito.mock(SimpleTemplateEngine.class);
            markdownGenerator = new SimpleMarkdownGenerator(mockConverter, mockConfig, mockTemplateEngine);

            mockMarkdownProperties = Mockito.mock(MarkdownProperties.class);
            mockMarkdownFile = new MarkdownFile(mockMarkdownProperties, "dummy content", "dummy/path");
            mockProfileProperties = Mockito.mock(BlogProperties.ProfileConfig.class);
            mockLayoutProperties = Mockito.mock(BlogProperties.LayoutConfig.class);

            when(mockConfig.profile()).thenReturn(mockProfileProperties);
            when(mockConfig.layout()).thenReturn(mockLayoutProperties);
            when(mockProfileProperties.name()).thenReturn("John Doe");

            setupConfigMocks(true);
        }

        @Test
        void testGenerateToHtml() throws IOException {
            // 마크다운 -> HTML 변환 결과 모킹
            when(mockConverter.convertBodyToHtml("dummy content")).thenReturn("<p>변환된 내용</p>");

            // 템플릿 로딩 모킹
            when(mockTemplateEngine.loadTemplate("templates/article.html"))
                    .thenReturn("<html><head><title>${title}</title></head><body>${content}</body></html>");

            // 템플릿 처리 모킹
            when(mockTemplateEngine.processTemplate(anyString(), anyMap()))
                    .thenReturn("<html><head><title>테스트 제목</title></head><body><p>변환된 내용</p></body></html>");

            // 필요한 마크다운 속성들 모킹
            when(mockMarkdownProperties.title()).thenReturn("테스트 제목");
            when(mockMarkdownProperties.date()).thenReturn(LocalDate.now());

            // 프로필 섹션 생성을 위한 설정
            when(mockTemplateEngine.loadTemplate("templates/profile.html")).thenReturn("<div>${name}</div>");
            when(mockProfileProperties.role()).thenReturn("개발자");
            when(mockProfileProperties.bio()).thenReturn("자기소개");
            when(mockProfileProperties.avatar()).thenReturn("avatar.jpg");

            // 테스트 실행
            String result = markdownGenerator.generateToHtml(mockMarkdownFile);

            // 검증
            assertNotNull(result);
            assertTrue(result.contains("테스트 제목"));
            assertTrue(result.contains("변환된 내용"));

            // 메서드 호출 검증
            verify(mockConverter).convertBodyToHtml("dummy content");
            verify(mockTemplateEngine).loadTemplate("templates/article.html");
            verify(mockTemplateEngine, times(2)).processTemplate(anyString(), anyMap());
        }

        @Test
        void testGenerateToHtmlWithTemplateLoadException() throws IOException {
            // 마크다운 -> HTML 변환 결과 모킹
            when(mockConverter.convertBodyToHtml("dummy content")).thenReturn("<p>변환된 내용</p>");

            // 템플릿 로딩 시 예외 발생 모킹
            when(mockTemplateEngine.loadTemplate("templates/article.html"))
                    .thenThrow(new IOException("템플릿 로드 오류"));

            // 필요한 마크다운 속성들 모킹
            when(mockMarkdownProperties.title()).thenReturn("테스트 제목");
            when(mockConfig.layout().menuPosition()).thenReturn("center");

            // 테스트 실행
            String result = markdownGenerator.generateToHtml(mockMarkdownFile);

            // 검증: 기본 템플릿으로 대체되었는지 확인
            assertNotNull(result);
            assertTrue(result.contains("<!DOCTYPE html><html><head><title>테스트 제목</title></head><body>"));
            assertTrue(result.contains("<p>변환된 내용</p>"));
        }
    }

    @Nested
    @DisplayName("GenerateMainPage")
    class GenerateMainPageTests {

        @BeforeEach
        void setUp() {
            mockConverter = Mockito.mock(Md2HtmlConverter.class);
            mockConfig = Mockito.mock(BlogProperties.class);
            mockTemplateEngine = Mockito.mock(SimpleTemplateEngine.class);
            markdownGenerator = new SimpleMarkdownGenerator(mockConverter, mockConfig, mockTemplateEngine);

            mockMarkdownProperties = Mockito.mock(MarkdownProperties.class);
            mockMarkdownFile = new MarkdownFile(mockMarkdownProperties, "dummy content", "dummy/path");
            mockProfileProperties = Mockito.mock(BlogProperties.ProfileConfig.class);
            mockLayoutProperties = Mockito.mock(BlogProperties.LayoutConfig.class);

            when(mockConfig.profile()).thenReturn(mockProfileProperties);
            when(mockProfileProperties.name()).thenReturn("John Doe");

            setupConfigMocks(true);
        }

        @Test
        void testGenerateMainPage() throws IOException {
            // 최신 파일 모킹
            MarkdownFile mockLatestFile = new MarkdownFile(mockMarkdownProperties, "최신 내용", "latest/path");

            // 메인 페이지 템플릿 모킹
            when(mockTemplateEngine.loadTemplate("templates/index.html"))
                    .thenReturn("<html><body>${title}${latestArticleHtml}</body></html>");

            // 최신 게시글 템플릿 모킹
            when(mockTemplateEngine.loadTemplate("templates/latest-article.html"))
                    .thenReturn("<div class='latest'>${titleString}</div>");

            // 최신 게시글 생성에 필요한 속성 모킹
            when(mockMarkdownProperties.date()).thenReturn(LocalDate.now());
            when(mockMarkdownProperties.title()).thenReturn("최신 제목");
            when(mockMarkdownProperties.description()).thenReturn("");
            when(mockMarkdownProperties.thumbnailUrl()).thenReturn("thumbnail.jpg");

            // 템플릿 처리 모킹
            when(mockTemplateEngine.processTemplate(anyString(), anyMap()))
                    .thenAnswer(invocation -> {
                        String template = invocation.getArgument(0);
                        if (template.contains("='latest'")) {
                            return "<div class='latest'>최신 제목</div>";
                        } else {
                            return "<html><body>환영 제목<div class='latest'>최신 제목</div></body></html>";
                        }
                    });

            // 테스트 실행
            String result = markdownGenerator.generateMainPage(mockMarkdownFile, mockLatestFile);

            // 검증
            assertNotNull(result);
            assertTrue(result.contains("환영 제목"));
            assertTrue(result.contains("최신 제목"));

            // 메서드 호출 검증
            verify(mockTemplateEngine).loadTemplate("templates/index.html");
            verify(mockTemplateEngine, atLeastOnce()).processTemplate(anyString(), anyMap());
        }

        @Test
        void testGenerateMainPageWhenWelcomeDisabled() throws IOException {
            // 환영 화면 비활성화 모킹
            setupConfigMocks(false);

            // 최신 파일 모킹
            MarkdownFile mockLatestFile = new MarkdownFile(mockMarkdownProperties, "최신 내용", "latest/path");

            // 테스트 실행
            String result = markdownGenerator.generateMainPage(mockMarkdownFile, mockLatestFile);

            // 검증: 빈 문자열이 반환되어야 함
            assertEquals("", result);

            // generateLatestArticle 메서드가 호출되지 않았는지 검증
            verify(mockTemplateEngine, never()).loadTemplate("templates/index.html");
        }

        @Test
        void testGenerateMainPageWithIOException() throws IOException {
            // 최신 파일 모킹
            MarkdownFile mockLatestFile = new MarkdownFile(mockMarkdownProperties, "최신 내용", "latest/path");

            // 환영 화면 활성화 모킹
            setupConfigMocks(true);

            // 템플릿 로드 시 예외 발생 모킹
            when(mockTemplateEngine.loadTemplate("templates/index.html"))
                    .thenThrow(new IOException("템플릿 로드 오류"));

            // 테스트 실행
            String result = markdownGenerator.generateMainPage(mockMarkdownFile, mockLatestFile);

            // 검증: 오류 발생 시 빈 문자열 반환
            assertEquals("", result);
        }
    }

    /**
     * 기본 설정 모킹을 위한 유틸리티 메서드
     */
    private void setupConfigMocks(boolean showWelcome) {
        // 레이아웃 설정
        var mockLayoutProperties = Mockito.mock(BlogProperties.LayoutConfig.class);
        var mockColorProperties = Mockito.mock(BlogProperties.ColorConfig.class);
        var mockWelcomeProperties = Mockito.mock(BlogProperties.WelcomeConfig.class);
        when(mockConfig.layout()).thenReturn(mockLayoutProperties);
        when(mockLayoutProperties.menuPosition()).thenReturn("top");
        when(mockLayoutProperties.colors()).thenReturn(mockColorProperties);
        when(mockColorProperties.primary()).thenReturn("#primary");
        when(mockColorProperties.background()).thenReturn("#background");
        when(mockColorProperties.text()).thenReturn("#text");
        when(mockColorProperties.headings()).thenReturn("#headings");
        when(mockColorProperties.links()).thenReturn("#links");

        when(mockLayoutProperties.welcome()).thenReturn(mockWelcomeProperties);
        when(mockWelcomeProperties.show()).thenReturn(showWelcome);
        when(mockWelcomeProperties.title()).thenReturn("환영 제목");
        when(mockWelcomeProperties.comment()).thenReturn("환영 코멘트");
        when(mockConfig.logo()).thenReturn("logo.png");

        // 블로그 기본 정보
        when(mockConfig.title()).thenReturn("테스트 블로그");
        when(mockConfig.description()).thenReturn("테스트 설명");

        // 커스터마이징 옵션
        var mockCustomizationProperties = Mockito.mock(BlogProperties.CustomizationConfig.class);
        when(mockConfig.customization()).thenReturn(mockCustomizationProperties);
        when(mockCustomizationProperties.showDate()).thenReturn(true);
        when(mockCustomizationProperties.footerText()).thenReturn("푸터 텍스트");
    }
}