package com.jimple.parser.template;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 간단한 템플릿 엔진 구현
 */
public class SimpleTemplateEngine {
    // 변수 치환 패턴: {{variableName}}
    private static final Pattern VARIABLE_PATTERN = Pattern.compile("\\{\\{([^}]+)}}");

    // 조건부 섹션 패턴: {{#condition}}...{{/condition}}
    private static final Pattern SECTION_PATTERN = Pattern.compile(
            "\\{\\{#([^}]+)}}([\\s\\S]*?)\\{\\{/\\1}}",
            Pattern.DOTALL
    );

    /**
     * 리소스 경로에서 템플릿을 로드
     *
     * @param resourcePath 리소스 경로
     * @return 템플릿 내용
     * @throws IOException 템플릿 로드 실패 시
     */
    public String loadTemplate(String resourcePath) throws IOException {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            if (is == null) {
                throw new IOException("템플릿 파일을 찾을 수 없습니다: " + resourcePath);
            }

            try (Scanner s = new Scanner(is, StandardCharsets.UTF_8)) {
                s.useDelimiter("\\A");
                return s.hasNext() ? s.next() : "";
            }
        }
    }

    /**
     * 템플릿에 데이터를 적용하여 결과 생성
     *
     * @param template 템플릿 문자열
     * @param data     적용할 데이터
     * @return 처리된 결과
     */
    public String processTemplate(String template, Map<String, Object> data) {
        String result = template;

        // 조건부 섹션 처리
        Matcher sectionMatcher = SECTION_PATTERN.matcher(result);
        StringBuilder sb = new StringBuilder();

        while (sectionMatcher.find()) {
            String conditionName = sectionMatcher.group(1);
            String sectionContent = sectionMatcher.group(2);

            Object conditionValue = data.get(conditionName);
            boolean condition = switch (conditionValue) {
                case Boolean b -> b;
                case String s -> !s.isEmpty();
                case Number number -> number.doubleValue() != 0;
                case null, default -> conditionValue != null;
            };

            String replacement = condition ? sectionContent : "";
            sectionMatcher.appendReplacement(sb, Matcher.quoteReplacement(replacement));
        }

        sectionMatcher.appendTail(sb);
        result = sb.toString();

        // 변수 치환
        Matcher matcher = VARIABLE_PATTERN.matcher(result);
        sb = new StringBuilder();

        while (matcher.find()) {
            String varName = matcher.group(1);
            Object value = data.get(varName);
            String replacement = (value != null) ? value.toString() : "";
            matcher.appendReplacement(sb, Matcher.quoteReplacement(replacement));
        }

        matcher.appendTail(sb);
        result = sb.toString();

        return result;
    }
}