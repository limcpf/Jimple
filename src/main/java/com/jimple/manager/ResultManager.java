package com.jimple.manager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.jimple.collector.MarkdownFileMapper;
import com.jimple.finder.MarkdownFinder;
import com.jimple.generator.MarkdownGenerator;
import com.jimple.model.category.CategoryInfo;
import com.jimple.model.category.CategoryPage;
import com.jimple.model.category.CategoryPageItem;
import com.jimple.model.category.CategorySummary;
import com.jimple.model.generator.GenerateType;
import com.jimple.model.list.PostPage;
import com.jimple.model.list.PostPageInfo;
import com.jimple.model.list.PostPageItem;
import com.jimple.model.md.MarkdownFile;
import com.jimple.model.md.MarkdownProperties;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ResultManager {
    private final MarkdownFinder finder;
    private final MarkdownFileMapper mapper;
    private final MarkdownGenerator generator;
    private final Path resultDir;
    private final CategoryManager categoryManager;

    public static ObjectMapper jsonMapper = new ObjectMapper();
    static {
        jsonMapper.registerModule(new JavaTimeModule());
    }

    public ResultManager(MarkdownFinder finder, MarkdownFileMapper mapper, MarkdownGenerator generator, Path resultDir) {
        this.finder = finder;
        this.mapper = mapper;
        this.generator = generator;
        this.resultDir = resultDir;
        this.categoryManager = new CategoryManager();

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

        this.processPostListJson(publishedItems);
        this.processCategoryJson(publishedItems);
        this.processPostHtml(publishedItems);
    }

    private void processPostHtml(List<MarkdownFile> markdownFiles) {
        MarkdownFile mainPage = new MarkdownFile(
                new MarkdownProperties(true, "index", LocalDate.now()),
                "",
                "index.html"
        );

        MarkdownFile listPage = new MarkdownFile(
                new MarkdownProperties(true, "list", LocalDate.now()),
                "",
                "list.html"
        );

        MarkdownFile archivePage = new MarkdownFile(
                new MarkdownProperties(true, "archive", LocalDate.now()),
                "",
                "archive.html"
        );

        markdownFiles.add(mainPage);
        markdownFiles.add(listPage);
        markdownFiles.add(archivePage);

        for (MarkdownFile file : markdownFiles) {
            String html = switch (file.path()) {
                case "index.html" -> generator.generateMainPage(file, markdownFiles.getFirst());
                case "list.html" -> generator.generateToHtml(file, GenerateType.LIST);
                case "archive.html" -> generator.generateArchivePage(file);
                default -> generator.generateToHtml(file);
            };

            Document doc = Jsoup.parse(html);
            doc.outputSettings().prettyPrint(true);
            doc.outputSettings().indentAmount(4);

            saveHtmlFile(file, doc.outerHtml());
        }
    }

    private void processPostListJson(List<MarkdownFile> markdownFiles) {
        int page = 1;
        byte count = 0;
        int countPost = markdownFiles.size();

        List<PostPageItem> postPageItems = new ArrayList<>();


        for (MarkdownFile file : markdownFiles) {
            count++;
            postPageItems.add(new PostPageItem(file));

            if(count > 4) {
                savePostListJsonFile(postPageItems, page, countPost);

                count = 0;
                page++;
                postPageItems = new ArrayList<>();
            }
        }

        if(count != 0) {
            savePostListJsonFile(postPageItems, page, countPost);
        }
    }

    private void processCategoryJson(List<MarkdownFile> markdownFiles) {
        // 모든 카테고리 경로 수집
        List<String> categoryPaths = categoryManager.getAllCategoryPaths(markdownFiles);
        
        // 루트 카테고리 추가 (빈 문자열)
        categoryPaths.add(0, "");
        
        // 각 카테고리별로 JSON 파일 생성
        for (String categoryPath : categoryPaths) {
            CategoryInfo categoryInfo = categoryManager.getCategoryInfo(markdownFiles, categoryPath);
            saveCategoryJsonFile(categoryInfo);
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

    private void savePostListJsonFile(List<PostPageItem> items, int page, int count) {
        Path targetPath = resultDir.resolve("post-list-" + page + ".json");

        int lastPage = (int) Math.ceil((double) count / 5);

        PostPageInfo postPageInfo = new PostPageInfo(page, lastPage, count, page < lastPage, page > 1);
        PostPage postPage = new PostPage(items, postPageInfo);

        try {
            Files.writeString(targetPath, jsonMapper.writeValueAsString(postPage));
        } catch (IOException e) {
            throw new RuntimeException("PostPage 파일을 저장할 수 없습니다: " + targetPath, e);
        }
    }

    private void saveCategoryJsonFile(CategoryInfo categoryInfo) {
        // 카테고리 경로를 파일명으로 변환
        String fileName = categoryInfo.fullPath().isEmpty() ? 
            "category-root.json" : 
            "category-" + categoryInfo.fullPath().replace("/", "-") + ".json";
        
        Path targetPath = resultDir.resolve(fileName);
        
        // CategoryPageItem 리스트 생성
        List<CategoryPageItem> categoryPageItems = categoryInfo.posts().stream()
            .map(CategoryPageItem::new)
            .toList();
        
        // 하위 카테고리 요약 정보 생성
        List<CategorySummary> subCategorySummaries = categoryInfo.subCategories().stream()
            .map(CategorySummary::from)
            .toList();
        
        // 페이지 정보 생성 (카테고리는 페이징 없이 모든 게시글 표시)
        PostPageInfo pageInfo = new PostPageInfo(1, 1, categoryInfo.posts().size(), false, false);
        
        // CategoryPage 생성
        CategoryPage categoryPage = new CategoryPage(
            categoryPageItems,
            subCategorySummaries,
            pageInfo,
            categoryInfo.fullPath(),
            categoryInfo.name()
        );
        
        try {
            Files.writeString(targetPath, jsonMapper.writeValueAsString(categoryPage));
        } catch (IOException e) {
            throw new RuntimeException("CategoryPage 파일을 저장할 수 없습니다: " + targetPath, e);
        }
    }
}