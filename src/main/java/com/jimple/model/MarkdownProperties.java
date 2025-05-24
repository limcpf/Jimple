package com.jimple.model;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;

public record MarkdownProperties(
        boolean publish,
        String title,
        LocalDate date
) implements Properties{
    @Override
    public @NotNull String toString() {
        return "MarkdownProperties{" +
                "publish=" + publish +
                ", title='" + title + '\'' +
                ", date=" + date +
                '}';
    }
}
