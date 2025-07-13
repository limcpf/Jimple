package com.jimple.generator;

import com.jimple.model.generator.GenerateType;
import com.jimple.model.md.MarkdownFile;

public interface MarkdownGenerator {
    String generateToHtml(MarkdownFile file);
    String generateToHtml(MarkdownFile file, GenerateType type);
    String generateMainPage(MarkdownFile file, MarkdownFile latestFile);
    String generateLatestArticle(MarkdownFile file);
}
