package com.jimple.model;

public record MarkdownFile(MarkdownProperties properties, String contents, String path) {
    public boolean isPublish() {
        return properties.publish();
    }
}
