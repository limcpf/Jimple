package com.jimple.generator.converter;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

public class SimpleMd2HtmlConverter implements Md2HtmlConverter {
    private final Parser parser;
    private final HtmlRenderer renderer;

    public SimpleMd2HtmlConverter() {
        this.parser = Parser.builder().build();
        this.renderer = HtmlRenderer.builder().build();
    }

    @Override
    public String convertToHtml(String markdown) {
        if (markdown == null || markdown.isEmpty()) {
            return "";
        }
        Node document = parser.parse(markdown);
        return renderer.render(document);
    }
}
