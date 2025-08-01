package com.jimple.model.md;

import com.jimple.model.Properties;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;

public record MarkdownProperties(
        boolean publish,
        String title,
        LocalDate date,
        String description,
        String thumbnailUrl,
        String path
) implements Properties {

    public MarkdownProperties() {
        this(false, "", null, "", "", "");
    }

    public MarkdownProperties(boolean publish, String title, LocalDate date) {
        this(publish, title, date, "", "", "");
    }

    @Override
    public @NotNull String toString() {
        return "MarkdownProperties{" +
                "publish=" + publish +
                ", title='" + title + '\'' +
                ", date=" + date +
                ", thumbnailUrl='" + thumbnailUrl + '\'' +
                ", path='" + path + '\'' +
                ", description=\n" + description +
                '}';
    }
}
