package com.jimple.generator;

import com.jimple.model.md.MarkdownFile;

public interface MarkdownGenerator {
    String generateToHtml(MarkdownFile file);
    String generateMainPage(MarkdownFile file, MarkdownFile latestFile);
    String generateLatestArticle(MarkdownFile file);
}
