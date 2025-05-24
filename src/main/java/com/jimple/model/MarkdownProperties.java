package com.jimple.model;

import java.time.LocalDate;

public record MarkdownProperties(
        boolean publish,
        String title,
        LocalDate date
) implements Properties{
    @Override
    public String toString() {
        return "MarkdownProperties{" +
                "publish=" + publish +
                ", title='" + title + '\'' +
                ", date=" + date +
                '}';
    }
}
