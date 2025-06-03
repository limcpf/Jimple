package com.jimple.parser.extractor;

import java.nio.file.Path;

public interface MarkdownExtractor {
    String extractFrontMatter(String fullContents);
    String extractContent(String fullContents);
    String extractFullContents(Path path);
}
