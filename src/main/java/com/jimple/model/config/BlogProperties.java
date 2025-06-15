package com.jimple.model.config;

import com.jimple.model.Properties;

import java.util.ArrayList;
import java.util.List;

/**
 * 블로그 설정 정보를 담는 클래스
 */
public record BlogProperties(
        String title,
        String description,
        String logo,
        LayoutConfig layout,
        ProfileConfig profile,
        CustomizationConfig customization
) implements Properties {

    // 기본 생성자
    public BlogProperties() {
        this(
                BlogConfigDefaults.DEFAULT_TITLE,
                BlogConfigDefaults.DEFAULT_DESCRIPTION,
                "",
                new LayoutConfig(),
                new ProfileConfig(),
                new CustomizationConfig()
        );
    }

    // 레이아웃 설정
    public record LayoutConfig(
            String menuPosition,
            WelcomeConfig welcome,
            ColorConfig colors
    ) {
        // 기본 생성자
        public LayoutConfig() {
            this(
                    BlogConfigDefaults.DEFAULT_MENU_POSITION,
                    new WelcomeConfig(false, "", ""),
                    new ColorConfig()
            );
        }
    }

    public record WelcomeConfig(
            boolean show,
            String title,
            String comment
    ) { }

    // 색상 설정
    public record ColorConfig(
            String primary,
            String secondary,
            String background,
            String text,
            String headings,
            String links
    ) {
        // 기본 생성자
        public ColorConfig() {
            this(
                    BlogConfigDefaults.DEFAULT_PRIMARY_COLOR,
                    BlogConfigDefaults.DEFAULT_SECONDARY_COLOR,
                    BlogConfigDefaults.DEFAULT_BACKGROUND_COLOR,
                    BlogConfigDefaults.DEFAULT_TEXT_COLOR,
                    BlogConfigDefaults.DEFAULT_HEADINGS_COLOR,
                    BlogConfigDefaults.DEFAULT_LINKS_COLOR
            );
        }
    }

    // 프로필 설정
    public record ProfileConfig(
            String name,
            String role,
            String bio,
            String email,
            List<SocialLink> social
    ) {
        // 기본 생성자
        public ProfileConfig() {
            this(
                    BlogConfigDefaults.DEFAULT_PROFILE_NAME,
                    BlogConfigDefaults.DEFAULT_PROFILE_ROLE,
                    BlogConfigDefaults.DEFAULT_PROFILE_BIO,
                    BlogConfigDefaults.DEFAULT_PROFILE_EMAIL,
                    new ArrayList<>()
            );
        }
    }

    // 소셜 링크 정보
    public record SocialLink(
            String platform,
            String url
    ) {
        // 기본 생성자
        public SocialLink() {
            this("", "");
        }
    }

    // 추가 커스터마이징 옵션
    public record CustomizationConfig(
            boolean showDate,
            boolean enableComments,
            String footerText
    ) {
        // 기본 생성자
        public CustomizationConfig() {
            this(
                    BlogConfigDefaults.DEFAULT_SHOW_DATE,
                    BlogConfigDefaults.DEFAULT_ENABLE_COMMENTS,
                    BlogConfigDefaults.DEFAULT_FOOTER_TEXT
            );
        }
    }
}