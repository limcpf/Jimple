package com.jimple.collector;

import com.jimple.finder.MarkdownFinder;
import com.jimple.model.MarkdownFile;
import com.jimple.model.MarkdownProperties;
import com.jimple.parser.extractor.MarkdownExtractor;
import com.jimple.parser.yml.MarkdownYmlParser;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class MarkdownCollector {
    private final MarkdownFinder finder;
    private final MarkdownYmlParser parser;
    private final MarkdownExtractor extractor;

    public MarkdownCollector(MarkdownFinder finder, MarkdownYmlParser parser, MarkdownExtractor extractor) {
        this.finder = finder;
        this.parser = parser;
        this.extractor = extractor;
    }

    public List<MarkdownFile> collectPublishedMarkdowns(Path rootDir) {
        List<Path> markdownFiles = finder.findAll(rootDir);

        return markdownFiles.stream()
                .map(path -> {
                    String fullContents = extractor.extractFullContents(path);
                    String frontmatter = extractor.extractFrontMatter(fullContents);
                    String contents = extractor.extractContent(fullContents);
                    MarkdownProperties properties = parser.getProperties(frontmatter);

                    return new MarkdownFile(properties, contents, path.toString());
                })
                .filter(item -> item.isPublish())
                .collect(Collectors.toList());
    }
}