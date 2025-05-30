package com.jimple.generator;

import com.jimple.model.MarkdownFile;

import java.nio.file.Path;

public interface MarkdownGenerator {
    String generateToHtml(MarkdownFile file);
}
