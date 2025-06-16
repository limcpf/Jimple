package com.jimple.model.md;

public record MarkdownFile(MarkdownProperties properties, String contents, String path) {
    public boolean isPublish() {
        return properties.publish();
    }
}
