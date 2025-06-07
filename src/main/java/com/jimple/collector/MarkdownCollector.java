package com.jimple.collector;

import com.jimple.finder.MarkdownFinder;
import com.jimple.model.md.MarkdownFile;
import com.jimple.model.md.MarkdownProperties;
import com.jimple.parser.extractor.MarkdownExtractor;
import com.jimple.parser.yml.SimpleMarkdownYmlParser;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class MarkdownCollector {
    private final MarkdownFinder finder;
    private final SimpleMarkdownYmlParser parser;
    private final MarkdownExtractor extractor;

    public MarkdownCollector(MarkdownFinder finder, SimpleMarkdownYmlParser parser, MarkdownExtractor extractor) {
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
                .filter(MarkdownFile::isPublish)
                .collect(Collectors.toList());
    }
}