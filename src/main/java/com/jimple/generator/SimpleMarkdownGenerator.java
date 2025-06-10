package com.jimple.generator;

import com.jimple.generator.converter.Md2HtmlConverter;
import com.jimple.model.config.BlogProperties;
import com.jimple.model.md.MarkdownFile;
import com.jimple.parser.template.SimpleTemplateEngine;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SimpleMarkdownGenerator implements MarkdownGenerator {
    private final Md2HtmlConverter converter;
    private final BlogProperties config;
    private final SimpleTemplateEngine templateEngine;

    public SimpleMarkdownGenerator(Md2HtmlConverter converter, BlogProperties config, SimpleTemplateEngine templateEngine) {
        this.converter = converter;
        this.templateEngine = templateEngine;
        this.config = config;
    }

    @Override
    public String generateMainPage(MarkdownFile file) {
        String result = "";

        if(config.layout().welcome().show()) {
            // 프로필 템플릿 로드
            try {
                String mainPageTemplate = templateEngine.loadTemplate("templates/index.html");

                // 프로필 데이터 준비
                Map<String, Object> mainPageData = new HashMap<>();
                mainPageData = prepareTemplateData(file, mainPageTemplate);
                mainPageData.put("logo", config.logo());
                mainPageData.put("title", config.layout().welcome().title());
                mainPageData.put("comment", config.layout().welcome().comment());

                // 템플릿 적용
                result = templateEngine.processTemplate(mainPageTemplate, mainPageData);
            } catch (IOException e) {
                result = "";
            }
        }

        return result;
    }

    @Override
    public String generateToHtml(MarkdownFile file) {
        // 마크다운을 HTML로 변환
        String htmlContent = converter.convertBodyToHtml(file.contents());

        // 템플릿 데이터 준비
        Map<String, Object> templateData = prepareTemplateData(file, htmlContent);

        // HTML 페이지 템플릿 적용
        return applyTemplate(templateData);
    }

    /**
     * 템플릿 데이터 준비
     */
    private Map<String, Object> prepareTemplateData(MarkdownFile file, String htmlContent) {
        Map<String, Object> data = new HashMap<>();

        // 문서 메타데이터
        data.put("title", file.properties().title());
        data.put("date", file.properties().date());
        data.put("content", htmlContent);

        // 블로그 기본 정보
        data.put("blogTitle", config.title());
        data.put("blogDescription", config.description());

        // 레이아웃 정보
        data.put("menuPosition", config.layout().menuPosition());

        // 색상 정보
        data.put("primaryColor", config.layout().colors().primary());
        data.put("backgroundColor", config.layout().colors().background());
        data.put("textColor", config.layout().colors().text());
        data.put("headingColor", config.layout().colors().headings());
        data.put("linkColor", config.layout().colors().links());

        // 커스터마이징 옵션
        data.put("showDate", config.customization().showDate());
        data.put("footerText", config.customization().footerText());

        // 메뉴 및 프로필 섹션 생성
        try {
            data.put("mainMenu", generateMenuSection());
            data.put("profile", generateProfileSection(config));
        } catch (IOException e) {
            throw new RuntimeException("템플릿 로드 중 오류 발생");
        }

        return data;
    }

    /**
     * 템플릿을 적용하여 HTML 페이지 생성
     */
    private String applyTemplate(Map<String, Object> data) {
        try {
            String template = templateEngine.loadTemplate("templates/article.html");
            return templateEngine.processTemplate(template, data);
        } catch (IOException e) {
            // 기본 템플릿으로 대체
            return String.format(
                    "<!DOCTYPE html><html><head><title>%s</title></head><body>%s</body></html>",
                    data.get("title"),
                    data.get("content")
            );
        }
    }

    /**
     * 메뉴 섹션 생성
     */
    private String generateMenuSection() {
        // 실제 구현에서는 카테고리나 태그 등을 기반으로 메뉴 생성
        return """
            <ul class="nav-list">
                <li><a href="index.html">홈</a></li>
                <li><a href="about.html">소개</a></li>
                <li><a href="archive.html">리스트</a></li>
            </ul>
        """;
    }

    /**
     * 프로필 섹션 생성
     */
    private String generateProfileSection(BlogProperties blogProps) throws IOException {
        // 프로필 템플릿 로드
        String profileTemplate = templateEngine.loadTemplate("templates/profile.html");

        // 프로필 데이터 준비
        Map<String, Object> profileData = new HashMap<>();
        profileData.put("name", blogProps.profile().name());
        profileData.put("role", blogProps.profile().role());
        profileData.put("bio", blogProps.profile().bio());
        profileData.put("avatar", blogProps.profile().avatar());
        profileData.put("socialLinks", blogProps.profile().social());

        // 템플릿 적용
        return templateEngine.processTemplate(profileTemplate, profileData);
    }
}