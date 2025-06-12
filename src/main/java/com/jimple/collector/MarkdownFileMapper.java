package com.jimple.collector;

import com.jimple.model.md.MarkdownFile;
import com.jimple.model.md.MarkdownProperties;
import com.jimple.parser.extractor.MarkdownExtractor;
import com.jimple.parser.yml.SimpleMarkdownYmlParser;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class MarkdownFileMapper {
    private final SimpleMarkdownYmlParser parser;
    private final MarkdownExtractor extractor;

    public MarkdownFileMapper(SimpleMarkdownYmlParser parser, MarkdownExtractor extractor) {
        this.parser = Objects.requireNonNull(parser);
        this.extractor = Objects.requireNonNull(extractor);
    }

    public List<MarkdownFile> collectPublishedMarkdownFiles(List<Path> markdownFiles) {
        return markdownFiles.stream()
                .map(this::generateMarkdownFile)
                .filter(MarkdownFile::isPublish)
                .collect(Collectors.toList());
    }

    public MarkdownFile generateMarkdownFile(Path path) {
        String fullContents = extractor.extractFullContents(path);
        String frontmatter = extractor.extractFrontMatter(fullContents);
        String contents = extractor.extractContent(fullContents);

        MarkdownProperties properties = parser.getProperties(frontmatter);
        String filePath = properties.title().replaceAll("\\s+", "-").concat(".html");

        return new MarkdownFile(properties, contents, filePath);
    }
}