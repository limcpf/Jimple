package com.jimple.parser.yml;

import com.jimple.model.MarkdownProperties;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.Yaml;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.Map;

public class MarkdownYmlParser implements YmlParser{
    @Override
    public @NotNull MarkdownProperties getProperties(String frontmatter) {
        if(frontmatter == null) throw new IllegalArgumentException("contents must not be null");

        if(!frontmatter.isEmpty()) {
            LocalDate date;

            Yaml yaml = new Yaml();
            Map<String, Object> yamlMap = yaml.load(frontmatter);



            boolean publish = false;

            Object publishObj = yamlMap.get("publish");
            if(publishObj instanceof Boolean b) {
                publish = b;
            }

            if(publish && !yamlMap.containsKey("title")) {
                throw new IllegalArgumentException("title must be set");
            } else if(!publish) {
                return new MarkdownProperties(false, "", LocalDate.now());
            }

            String title = yamlMap.get("title").toString();
            Object tempDate = yamlMap.get("date");
            
            if(tempDate instanceof Date d) {
                try {
                    date = d.toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate();
                } catch (DateTimeParseException e) {
                    throw new IllegalArgumentException("date must be in yyyy-MM-dd format");
                }
            } else {
                date = LocalDate.now();
            }

            return new MarkdownProperties(true, title, date);
        } else {
            return new MarkdownProperties(false, "", LocalDate.now());
        }
    }
}