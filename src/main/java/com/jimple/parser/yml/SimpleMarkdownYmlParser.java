package com.jimple.parser.yml;

import com.jimple.model.md.MarkdownProperties;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.YAMLException;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.Map;

public class SimpleMarkdownYmlParser implements YmlParser{
    @Override
    public @NotNull MarkdownProperties getProperties(String frontmatter) {
        if(frontmatter == null) throw new IllegalArgumentException("contents must not be null");

        if(!frontmatter.isEmpty()) {
            Yaml yaml = new Yaml();
            Map<String, Object> yamlMap;

            try {
                yamlMap = yaml.load(frontmatter);
            } catch (YAMLException e) {
                return new MarkdownProperties();
            }

            // publish
            if(!isPublish(yamlMap.get("publish"))) {
                return new MarkdownProperties();
            }

            String title = getTitle(yamlMap);
            LocalDate date = getDate(yamlMap);
            String description = getDescription(yamlMap);
            String thumbnailUrl = getThumbnailUrl(yamlMap);
            String path = getPath(yamlMap);

            return new MarkdownProperties(true, title, date, description, thumbnailUrl, path);
        } else {
            return new MarkdownProperties();
        }
    }

    private boolean isPublish(Object obj) {
        if(obj instanceof Boolean b) {
            return b;
        } else {
            return false;
        }
    }

    private String getTitle(Map<String, Object> map) {
        if(!map.containsKey("title")) {
            throw new IllegalArgumentException("title must be set");
        } else if(map.get("title") instanceof String s) {
            return s;
        }

        throw new IllegalArgumentException("title must be a string");
    }

    private LocalDate getDate(Map<String, Object> map) {
        Object tempDate = map.get("date");

        LocalDate date;

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

        return date;
    }

    private String getDescription(Map<String, Object> map) {
        if(!map.containsKey("description")) {
            return "";
        } else if(map.get("description") instanceof String s) {
            return s;
        }

        throw new IllegalArgumentException("description must be a string");
    }

    private String getThumbnailUrl(Map<String, Object> map) {
        if(map.get("thumbnailUrl") instanceof String s) {
            return s;
        }

        return "";
    }

    private String getPath(Map<String, Object> map) {
        if(map.get("path") instanceof String s) {
            return s;
        }

        return "";
    }
}