package com.jimple.parser.yml;

import com.jimple.model.MarkdownProperties;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.Yaml;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.Map;

public class MarkdownYmlParser implements YmlParser{
    @Override
    public @NotNull MarkdownProperties getProperties(String contents) {
        if(contents == null) throw new IllegalArgumentException("contents must not be null");

        String yamlContent = extractYamlFrontMatter(contents);

        if(!yamlContent.isEmpty()) {
            boolean publish = false;
            String title = "";
            LocalDate date = LocalDate.now();

            Yaml yaml = new Yaml();
            Map<String, Object> yamlMap = yaml.load(yamlContent);
            
            if(!yamlMap.containsKey("title")) {
                throw new IllegalArgumentException("title must be set");
            }
            
            publish = "true".equals(yamlMap.get("publish"));
            title = yamlMap.get("title").toString();
            Object tempDate = yamlMap.get("date");
            
            if(tempDate instanceof Date d) {
                try {
                    date = d.toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate();
                } catch (DateTimeParseException e) {
                    throw new IllegalArgumentException("date must be in yyyy-MM-dd format");
                }
            }

            return new MarkdownProperties(publish, title, date);
        } else {
            throw new IllegalArgumentException("YAML Front Matter not found");
        }
    }

    /**
     * Markdown 텍스트에서 YAML Front Matter(문서 맨 앞의 --- 블록)를 추출합니다.
     * - BOM이나 빈 줄을 건너뛰고 가장 먼저 나타나는 '---' 블록만 처리
     * - 다양한 줄바꿈(\n, \r\n, \r) 지원
     * - 블록이 닫히지 않으면 null 반환
     *
     * @param contents 전체 Markdown 텍스트
     * @return Front Matter 내용 (--- 사이의 텍스트), 없으면 null
     */
    public @NotNull String extractYamlFrontMatter(String contents) {
        if (contents == null || contents.isEmpty()) {
            return "";
        }

        try (BufferedReader reader = new BufferedReader(new StringReader(contents))) {
            StringBuilder yaml = new StringBuilder();
            String line;
            boolean inBlock = false;

            // 1) 처음에 BOM 또는 빈 줄 건너뛰기
            while ((line = reader.readLine()) != null) {
                String trimmed = line.trim();
                // BOM 제거 (U+FEFF)
                if (!inBlock && trimmed.startsWith("\uFEFF")) {
                    trimmed = trimmed.substring(1);
                }
                if (!inBlock) {
                    if (trimmed.isEmpty()) {
                        continue;       // 빈 줄 무시
                    }
                    if ("---".equals(trimmed)) {
                        inBlock = true; // 블록 시작
                    } else {
                        // 문서 맨 앞에 --- 없으면 Front Matter 없음
                        return "";
                    }
                } else {
                    // 2) 블록 안: 닫는 --- 만나는지 확인
                    if ("---".equals(trimmed)) {
                        return yaml.toString().trim();
                    }
                    yaml.append(line).append("\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }
}
