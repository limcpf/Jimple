package com.jimple.generator.converter;

import com.jimple.model.MarkdownProperties;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import java.time.format.DateTimeFormatter;

public class SimpleMd2HtmlConverter implements Md2HtmlConverter {
    private final Parser parser;
    private final HtmlRenderer renderer;

    public SimpleMd2HtmlConverter() {
        this.parser = Parser.builder().build();
        this.renderer = HtmlRenderer.builder().build();
    }

    @Override
    public String convertBodyToHtml(String markdown) {
        if (markdown == null || markdown.isEmpty()) {
            return "";
        }
        Node document = parser.parse(markdown);
        return renderer.render(document);
    }

    @Override
    public String convertHeaderToHtml(MarkdownProperties properties) {
        StringBuilder sb = new StringBuilder();
        sb.append("<header>\n");
        sb.append("\t<div class=\"post-info\">\n");
        sb.append("\t\t<span class=\"span\"></span>\n");
        if (properties.date() != null) {
            String formattedDate = properties.date().format(DateTimeFormatter.ISO_LOCAL_DATE);
            sb.append("\t\t<span class=\"date\">").append(formattedDate).append("</span>\n");
        }else {
            sb.append("\t\t<span class=\"span\"></span>\n");
        }
        sb.append("\t</div>\n");

        sb.append("  <h1>").append(properties.title()).append("</h1>\n");
        sb.append("</header>\n");
        return sb.toString();
    }
}
