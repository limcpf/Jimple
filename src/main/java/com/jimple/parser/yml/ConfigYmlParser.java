package com.jimple.parser.yml;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jimple.model.Properties;
import com.jimple.model.config.BlogProperties;

import java.io.IOException;

/**
 * 블로그 설정을 위한 YAML 파서
 */
public class ConfigYmlParser implements YmlParser {
    private final ObjectMapper yamlMapper;

    // 기본 생성자 추가
    public ConfigYmlParser(ObjectMapper yamlMapper) {
        // YAML 파싱을 위한 ObjectMapper 설정
        this.yamlMapper = yamlMapper;

        // 알 수 없는 속성이 있어도 에러 없이 무시
        this.yamlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        // Record 클래스 지원 및 Java 8 날짜/시간 API 지원 활성화
        this.yamlMapper.findAndRegisterModules();
    }

    @Override
    public Properties getProperties(String contents) {
        if (contents == null || contents.isEmpty()) {
            return new BlogProperties();
        }

        try {
            // YAML을 BlogProperties 객체로 직접 변환
            BlogProperties properties = yamlMapper.readValue(contents, BlogProperties.class);

            // null 체크 (Jackson이 null을 반환하는 경우는 거의 없지만 안전을 위해)
            return properties != null ? properties : new BlogProperties();
        } catch (IOException e) {
            return new BlogProperties(); // 오류 시 기본 설정 반환
        }
    }
}