package com.jimple.model.list;

import com.jimple.model.md.MarkdownFile;

public record PostPageItem(String title, String path, String date) {
    public PostPageItem(MarkdownFile file) {
        this(file.properties().title(), file.path(), file.getPublishDate().toString());
    }
}
