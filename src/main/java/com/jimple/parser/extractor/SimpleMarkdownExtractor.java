package com.jimple.parser.extractor;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class SimpleMarkdownExtractor implements MarkdownExtractor {
    private static final String DELIMITER = "---";

   @Override
   public String extractFrontMatter(String fullContents) {        // 기본 널 체크와 BOM 처리
        if (fullContents == null || fullContents.isEmpty()) {
            return "";
        }
        String content = removeBOM(fullContents.trim());                // 빠른 방식으로 시작 확인
        if (!content.startsWith(DELIMITER)) {
             return "";
        }
        int secondDelimiterIndex = content.indexOf(DELIMITER, DELIMITER.length());
        if (secondDelimiterIndex == -1) {
            return "";
        }

        return content.substring(DELIMITER.length(), secondDelimiterIndex).trim();
    }

    @Override    public String extractContent(String fullContents) {
        if (fullContents == null || fullContents.isEmpty()) {
            return "";
        }
        String content = removeBOM(fullContents.trim());
        if (!content.startsWith(DELIMITER)) {
            return content;
        }
        int secondDelimiterIndex = content.indexOf(DELIMITER, DELIMITER.length());
        if (secondDelimiterIndex == -1) {
            return content;
        }
        return content.substring(secondDelimiterIndex + DELIMITER.length()).trim();
    }
    private String removeBOM(String content) {
        if (content.startsWith("\uFEFF")) {
            return content.substring(1);
        }
        return content;
    }
    @Override
    public String extractFullContents(Path path) {
        try {
            return Files.readString(path, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("파일을 읽는 중 오류가 발생했습니다: " + path, e);
        }
    }
}