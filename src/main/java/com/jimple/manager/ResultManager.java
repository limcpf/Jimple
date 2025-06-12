package com.jimple.manager;

import com.jimple.collector.MarkdownFileMapper;
import com.jimple.finder.MarkdownFinder;
import com.jimple.generator.MarkdownGenerator;
import com.jimple.model.md.MarkdownFile;
import com.jimple.model.md.MarkdownProperties;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class ResultManager {
    private final MarkdownFinder finder;
    private final MarkdownFileMapper mapper;
    private final MarkdownGenerator generator;
    private final Path resultDir;

    public ResultManager(MarkdownFinder finder, MarkdownFileMapper mapper, MarkdownGenerator generator, Path resultDir) {
        this.finder = finder;
        this.mapper = mapper;
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
        List<Path> markdownFiles = finder.findAll(sourceDir);

        List<MarkdownFile> publishedItems = mapper.collectPublishedMarkdownFiles(markdownFiles);

        MarkdownFile mainPage = new MarkdownFile(
                new MarkdownProperties(true, "index", null),
                "",
                "index.html"
        );
        publishedItems.add(mainPage);

        for (MarkdownFile file : publishedItems) {
            String html;

            if("index.html".equals(file.path())) {
                html = generator.generateMainPage(file, publishedItems.getFirst());
            } else {
                html = generator.generateToHtml(file);
            }

            Document doc = Jsoup.parse(html);
            doc.outputSettings().prettyPrint(true);
            doc.outputSettings().indentAmount(4);

            saveHtmlFile(file, doc.outerHtml());
        }
    }

    private void saveHtmlFile(MarkdownFile item, String html) {
        Path targetPath = resultDir.resolve(item.path());

        try {
            Files.writeString(targetPath, html);
        } catch (IOException e) {
            throw new RuntimeException("HTML 파일을 저장할 수 없습니다: " + targetPath, e);
        }
    }
}