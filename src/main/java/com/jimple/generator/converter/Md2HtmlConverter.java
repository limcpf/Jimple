package com.jimple.generator.converter;

import com.jimple.model.MarkdownProperties;

public interface Md2HtmlConverter {
    String convertHeaderToHtml(MarkdownProperties properties);
    String convertBodyToHtml(String markdown);
}