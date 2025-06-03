package com.jimple.manager;

import com.jimple.collector.MarkdownCollector;
import com.jimple.generator.MarkdownGenerator;
import com.jimple.model.MarkdownFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class ResultManager {
    private final MarkdownCollector collector;
    private final MarkdownGenerator generator;
    private final Path resultDir;

    public ResultManager(MarkdownCollector collector, MarkdownGenerator generator, Path resultDir) {
        this.collector = collector;
        this.generator = generator;
        this.resultDir = resultDir;

        try {
            if (!Files.exists(resultDir)) {
                Files.createDirectories(resultDir);
            }
        } catch (IOException e) {
            throw new RuntimeException("결과 디렉토리를 생성할 수 없습니다: " + resultDir, e);
        }
    }

    public void processAndSaveResults(Path sourceDir) {
        List<MarkdownFile> publishedItems = collector.collectPublishedMarkdowns(sourceDir);

        for (MarkdownFile file : publishedItems) {
            String html = generator.generateToHtml(file);
            if (!html.isEmpty()) {
                saveHtmlFile(file, html);
            }
        }
    }

    private void saveHtmlFile(MarkdownFile item, String html) {
        String title = item.properties().title();
        String htmlFileName = title.replaceAll("\\s+", "-") + ".html";
        Path targetPath = resultDir.resolve(htmlFileName);

        try {
            Files.writeString(targetPath, html);
        } catch (IOException e) {
            throw new RuntimeException("HTML 파일을 저장할 수 없습니다: " + targetPath, e);
        }
    }
}