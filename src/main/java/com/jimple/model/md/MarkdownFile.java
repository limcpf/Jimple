package com.jimple.model.md;

import java.time.LocalDate;

public record MarkdownFile(MarkdownProperties properties, String contents, String path) {
    public boolean isPublish() {
        return properties.publish();
    }
    public LocalDate getPublishDate() { return properties.date(); }
}
