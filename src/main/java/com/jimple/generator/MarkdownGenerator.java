package com.jimple.generator;

import com.jimple.model.MarkdownFile;

public interface MarkdownGenerator {
    String generateToHtml(MarkdownFile file);
}
