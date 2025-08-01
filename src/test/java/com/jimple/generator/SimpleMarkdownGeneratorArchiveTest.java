package com.jimple.generator;

import com.jimple.generator.converter.Md2HtmlConverter;
import com.jimple.model.config.BlogProperties;
import com.jimple.model.md.MarkdownFile;
import com.jimple.model.md.MarkdownProperties;
import com.jimple.parser.template.SimpleTemplateEngine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * SimpleMarkdownGenerator의 generateArchivePage 메서드에 대한 단위 테스트
 */
class SimpleMarkdownGeneratorArchiveTest {

    private SimpleMarkdownGenerator markdownGenerator;
    private Md2HtmlConverter mockConverter;
    private BlogProperties mockConfig;
    private SimpleTemplateEngine mockTemplateEngine;
    private MarkdownFile testArchiveFile;

    @BeforeEach
    void setUp() {
        mockConverter = mock(Md2HtmlConverter.class);
        mockConfig = mock(BlogProperties.class);
        mockTemplateEngine = mock(SimpleTemplateEngine.class);
        
        // Mock 설정
        BlogProperties.ProfileConfig mockProfile = mock(BlogProperties.ProfileConfig.class);
        BlogProperties.LayoutConfig mockLayout = mock(BlogProperties.LayoutConfig.class);
        BlogProperties.WelcomeConfig mockWelcome = mock(BlogProperties.WelcomeConfig.class);
        BlogProperties.ColorConfig mockColors = mock(BlogProperties.ColorConfig.class);
        BlogProperties.CustomizationConfig mockCustomization = mock(BlogProperties.CustomizationConfig.class);
        
        when(mockConfig.profile()).thenReturn(mockProfile);
        when(mockConfig.layout()).thenReturn(mockLayout);
        when(mockConfig.customization()).thenReturn(mockCustomization);
        when(mockConfig.title()).thenReturn("테스트 블로그");
        when(mockConfig.description()).thenReturn("테스트 설명");
        when(mockConfig.logo()).thenReturn("test-logo.png");
        
        when(mockLayout.title()).thenReturn("테스트 블로그");
        when(mockLayout.welcome()).thenReturn(mockWelcome);
        when(mockLayout.colors()).thenReturn(mockColors);
        
        when(mockWelcome.show()).thenReturn(true);
        when(mockWelcome.title()).thenReturn("환영합니다");
        when(mockWelcome.comment()).thenReturn("테스트 블로그입니다");
        
        when(mockColors.primary()).thenReturn("#4a86e8");
        when(mockColors.secondary()).thenReturn("#63c5da");
        when(mockColors.background()).thenReturn("#ffffff");
        when(mockColors.text()).thenReturn("#333333");
        when(mockColors.headings()).thenReturn("#222222");
        when(mockColors.links()).thenReturn("#0366d6");
        
        when(mockCustomization.showDate()).thenReturn(true);
        when(mockCustomization.footerText()).thenReturn("© 2025 테스트 블로그");
        
        when(mockProfile.name()).thenReturn("테스트 사용자");
        when(mockProfile.role()).thenReturn("개발자");
        when(mockProfile.bio()).thenReturn("테스트 바이오");

        markdownGenerator = new SimpleMarkdownGenerator(mockConverter, mockConfig, mockTemplateEngine);

        // 테스트용 아카이브 파일 생성
        MarkdownProperties archiveProperties = new MarkdownProperties(
                true,
                "archive",
                LocalDate.now(),
                "아카이브 페이지",
                "",
                ""
        );
        testArchiveFile = new MarkdownFile(archiveProperties, "", "archive.html");
    }

    @Test
    void generateArchivePage_정상적인_템플릿_로드시_HTML_반환() throws IOException {
        // given
        String expectedTemplate = "<!DOCTYPE html><html><body>{{blogTitle}} - Archive</body></html>";
        String expectedHtml = "<!DOCTYPE html><html><body>테스트 블로그 - Archive</body></html>";
        
        when(mockTemplateEngine.loadTemplate("templates/archive.html")).thenReturn(expectedTemplate);
        when(mockTemplateEngine.processTemplate(eq(expectedTemplate), any())).thenReturn(expectedHtml);

        // when
        String result = markdownGenerator.generateArchivePage(testArchiveFile);

        // then
        assertEquals(expectedHtml, result);
        verify(mockTemplateEngine).loadTemplate("templates/archive.html");
        verify(mockTemplateEngine).processTemplate(eq(expectedTemplate), any());
    }

    @Test
    void generateArchivePage_템플릿_로드_실패시_빈문자열_반환() throws IOException {
        // given
        when(mockTemplateEngine.loadTemplate("templates/archive.html"))
                .thenThrow(new IOException("템플릿 파일을 찾을 수 없습니다"));

        // when
        String result = markdownGenerator.generateArchivePage(testArchiveFile);

        // then
        assertEquals("", result);
        verify(mockTemplateEngine).loadTemplate("templates/archive.html");
        verify(mockTemplateEngine, never()).processTemplate(any(), any());
    }

    @Test
    void generateArchivePage_템플릿_처리_실패시_빈문자열_반환() throws IOException {
        // given
        String template = "<!DOCTYPE html><html><body>{{blogTitle}}</body></html>";
        when(mockTemplateEngine.loadTemplate("templates/archive.html")).thenReturn(template);
        when(mockTemplateEngine.processTemplate(eq(template), any()))
                .thenThrow(new RuntimeException("템플릿 처리 실패"));

        // when & then
        assertDoesNotThrow(() -> {
            String result = markdownGenerator.generateArchivePage(testArchiveFile);
            assertEquals("", result);
        });
        
        verify(mockTemplateEngine).loadTemplate("templates/archive.html");
        verify(mockTemplateEngine).processTemplate(eq(template), any());
    }

    @Test
    void generateArchivePage_템플릿_데이터_올바르게_전달() throws IOException {
        // given
        String template = "<!DOCTYPE html><html><body>{{blogTitle}}</body></html>";
        String processedHtml = "<!DOCTYPE html><html><body>테스트 블로그</body></html>";
        
        when(mockTemplateEngine.loadTemplate("templates/archive.html")).thenReturn(template);
        when(mockTemplateEngine.processTemplate(eq(template), any())).thenReturn(processedHtml);

        // when
        String result = markdownGenerator.generateArchivePage(testArchiveFile);

        // then
        assertNotNull(result);
        assertEquals(processedHtml, result);
        
        // 템플릿 데이터가 올바르게 전달되었는지 확인
        verify(mockTemplateEngine).processTemplate(eq(template), argThat(data -> {
            @SuppressWarnings("unchecked")
            var dataMap = (java.util.Map<String, Object>) data;
            return "테스트 블로그".equals(dataMap.get("blogTitle")) &&
                   "테스트 설명".equals(dataMap.get("blogDescription")) &&
                   "테스트 블로그".equals(dataMap.get("topTitle"));
        }));
    }
}