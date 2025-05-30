package com.jimple.generator;

import com.jimple.generator.converter.Md2HtmlConverter;
import com.jimple.model.MarkdownFile;

public class SimpleMarkdownGenerator implements MarkdownGenerator {
    private final Md2HtmlConverter converter;

    public SimpleMarkdownGenerator(Md2HtmlConverter converter) {
        this.converter = converter;
    }

    @Override
    public String generateToHtml(MarkdownFile file) {
        if (file == null || !file.isPublish()) {
            return "";
        }

        String headerHtml = converter.convertHeaderToHtml(file.properties());
        String contentHtml = converter.convertBodyToHtml(file.contents());

        return headerHtml + contentHtml;
    }
}