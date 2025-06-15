package com.jimple.model.config;

import java.time.Year;

/**
 * 블로그 설정의 기본값을 정의하는 클래스
 */
public class BlogConfigDefaults {
    // 블로그 기본 정보
    public static final String DEFAULT_TITLE = "Jimple Blog";
    public static final String DEFAULT_DESCRIPTION = "Markdown to HTML Blog";

    // 레이아웃 기본값
    public static final String DEFAULT_MENU_POSITION = "top";

    // 색상 테마 기본값
    public static final String DEFAULT_PRIMARY_COLOR = "#4a86e8";
    public static final String DEFAULT_SECONDARY_COLOR = "#63c5da";
    public static final String DEFAULT_BACKGROUND_COLOR = "#ffffff";
    public static final String DEFAULT_TEXT_COLOR = "#333333";
    public static final String DEFAULT_HEADINGS_COLOR = "#222222";
    public static final String DEFAULT_LINKS_COLOR = "#0366d6";

    // 프로필 기본값
    public static final String DEFAULT_PROFILE_NAME = "블로그 작성자";
    public static final String DEFAULT_PROFILE_ROLE = "";
    public static final String DEFAULT_PROFILE_BIO = "";
    public static final String DEFAULT_PROFILE_EMAIL = "example@email.com";

    // 추가 설정 기본값
    public static final boolean DEFAULT_SHOW_DATE = true;
    public static final boolean DEFAULT_ENABLE_COMMENTS = false;
    public static final String DEFAULT_FOOTER_TEXT = "© " +
            Year.now().getValue() + " Powered by Jimple";

    // 유틸리티 클래스는 인스턴스화를 방지
    private BlogConfigDefaults() {
        throw new AssertionError("유틸리티 클래스는 인스턴스화할 수 없습니다");
    }
}