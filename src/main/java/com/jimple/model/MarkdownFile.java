package com.jimple.model;

public record MarkdownFile(MarkdownProperties properties, String contents, String path) {

    public String getTitle() {
        return properties.title();
    }

    public String getDate() {
        return properties.date().toString();
    }

    public boolean isPublish() {
        return properties.publish();
    }
}
