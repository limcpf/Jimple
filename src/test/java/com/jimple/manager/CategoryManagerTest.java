package com.jimple.manager;

import com.jimple.model.category.CategoryInfo;
import com.jimple.model.md.MarkdownFile;
import com.jimple.model.md.MarkdownProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * CategoryManager 클래스에 대한 단위 테스트
 */
class CategoryManagerTest {

    private CategoryManager categoryManager;

    @BeforeEach
    void setUp() {
        categoryManager = new CategoryManager();
    }

    @Test
    void buildCategoryTree_빈_파일_목록인_경우_빈_루트_카테고리_반환() {
        // given
        List<MarkdownFile> files = List.of();

        // when
        CategoryInfo rootCategory = categoryManager.buildCategoryTree(files);

        // then
        assertEquals("", rootCategory.name());
        assertEquals("", rootCategory.fullPath());
        assertEquals(0, rootCategory.level());
        assertTrue(rootCategory.posts().isEmpty());
        assertTrue(rootCategory.subCategories().isEmpty());
    }

    @Test
    void buildCategoryTree_path가_없는_파일들은_루트_카테고리에_추가() {
        // given
        MarkdownFile file1 = createMarkdownFile("테스트1", "");
        MarkdownFile file2 = createMarkdownFile("테스트2", "");
        List<MarkdownFile> files = List.of(file1, file2);

        // when
        CategoryInfo rootCategory = categoryManager.buildCategoryTree(files);

        // then
        assertEquals(2, rootCategory.posts().size());
        assertTrue(rootCategory.subCategories().isEmpty());
    }

    @Test
    void buildCategoryTree_단일_카테고리_경로_정상_처리() {
        // given
        MarkdownFile file1 = createMarkdownFile("Java 기초", "개발");
        MarkdownFile file2 = createMarkdownFile("Spring 기초", "개발");
        List<MarkdownFile> files = List.of(file1, file2);

        // when
        CategoryInfo rootCategory = categoryManager.buildCategoryTree(files);

        // then
        assertEquals(1, rootCategory.subCategories().size());
        
        CategoryInfo devCategory = rootCategory.subCategories().get(0);
        assertEquals("개발", devCategory.name());
        assertEquals("개발", devCategory.fullPath());
        assertEquals(0, devCategory.level()); // PathSegment의 level은 0부터 시작
        assertEquals(2, devCategory.posts().size());
    }

    @Test
    void buildCategoryTree_중첩_카테고리_경로_정상_처리() {
        // given
        MarkdownFile file1 = createMarkdownFile("Java 기초", "개발/Java");
        MarkdownFile file2 = createMarkdownFile("Spring 기초", "개발/Java/Spring");
        MarkdownFile file3 = createMarkdownFile("Python 기초", "개발/Python");
        List<MarkdownFile> files = List.of(file1, file2, file3);

        // when
        CategoryInfo rootCategory = categoryManager.buildCategoryTree(files);

        // then
        assertEquals(1, rootCategory.subCategories().size());
        
        CategoryInfo devCategory = rootCategory.subCategories().get(0);
        assertEquals("개발", devCategory.name());
        assertEquals(2, devCategory.subCategories().size());
        assertTrue(devCategory.posts().isEmpty()); // 개발 카테고리 자체에는 게시글 없음 (모든 파일이 하위 카테고리에 속함)
        
        // Java 카테고리 확인
        CategoryInfo javaCategory = devCategory.subCategories().stream()
                .filter(cat -> cat.name().equals("Java"))
                .findFirst()
                .orElseThrow();
        assertEquals("개발/Java", javaCategory.fullPath());
        assertEquals(1, javaCategory.posts().size()); // Java 기초 1개
        assertEquals(1, javaCategory.subCategories().size()); // Spring 하위 카테고리
        
        // Spring 카테고리 확인
        CategoryInfo springCategory = javaCategory.subCategories().get(0);
        assertEquals("Spring", springCategory.name());
        assertEquals("개발/Java/Spring", springCategory.fullPath());
        assertEquals(1, springCategory.posts().size()); // Spring 기초 1개
        
        // Python 카테고리 확인
        CategoryInfo pythonCategory = devCategory.subCategories().stream()
                .filter(cat -> cat.name().equals("Python"))
                .findFirst()
                .orElseThrow();
        assertEquals("개발/Python", pythonCategory.fullPath());
        assertEquals(1, pythonCategory.posts().size()); // Python 기초 1개
    }

    @Test
    void getCategoryInfo_루트_카테고리_조회() {
        // given
        MarkdownFile file1 = createMarkdownFile("루트 게시글", "");
        MarkdownFile file2 = createMarkdownFile("개발 게시글", "개발");
        List<MarkdownFile> files = List.of(file1, file2);

        // when
        CategoryInfo categoryInfo = categoryManager.getCategoryInfo(files, "");

        // then
        assertEquals("", categoryInfo.name());
        assertEquals("", categoryInfo.fullPath());
        assertEquals(1, categoryInfo.posts().size());
        assertEquals(1, categoryInfo.subCategories().size());
    }

    @Test
    void getCategoryInfo_특정_카테고리_조회() {
        // given
        MarkdownFile file1 = createMarkdownFile("Java 기초", "개발/Java");
        MarkdownFile file2 = createMarkdownFile("Spring 기초", "개발/Java/Spring");
        List<MarkdownFile> files = List.of(file1, file2);

        // when
        CategoryInfo categoryInfo = categoryManager.getCategoryInfo(files, "개발/Java");

        // then
        assertEquals("Java", categoryInfo.name());
        assertEquals("개발/Java", categoryInfo.fullPath());
        assertEquals(1, categoryInfo.posts().size());
        assertEquals(1, categoryInfo.subCategories().size());
    }

    @Test
    void getCategoryInfo_존재하지_않는_카테고리_조회시_null_반환() {
        // given
        MarkdownFile file1 = createMarkdownFile("Java 기초", "개발/Java");
        List<MarkdownFile> files = List.of(file1);

        // when
        CategoryInfo categoryInfo = categoryManager.getCategoryInfo(files, "개발/Python");

        // then
        assertNull(categoryInfo);
    }

    @Test
    void getAllCategoryPaths_모든_카테고리_경로_반환() {
        // given
        MarkdownFile file1 = createMarkdownFile("Java 기초", "개발/Java");
        MarkdownFile file2 = createMarkdownFile("Spring 기초", "개발/Java/Spring");
        MarkdownFile file3 = createMarkdownFile("Python 기초", "개발/Python");
        MarkdownFile file4 = createMarkdownFile("루트 게시글", ""); // path 없음
        List<MarkdownFile> files = List.of(file1, file2, file3, file4);

        // when
        List<String> categoryPaths = categoryManager.getAllCategoryPaths(files);

        // then
        assertEquals(4, categoryPaths.size());
        assertTrue(categoryPaths.contains("개발"));
        assertTrue(categoryPaths.contains("개발/Java"));
        assertTrue(categoryPaths.contains("개발/Java/Spring"));
        assertTrue(categoryPaths.contains("개발/Python"));
        
        // 정렬되어 있어야 함
        assertEquals("개발", categoryPaths.get(0));
        assertEquals("개발/Java", categoryPaths.get(1));
        assertEquals("개발/Java/Spring", categoryPaths.get(2));
        assertEquals("개발/Python", categoryPaths.get(3));
    }

    @Test
    void getAllCategoryPaths_path가_없는_파일들만_있는_경우_빈_목록_반환() {
        // given
        MarkdownFile file1 = createMarkdownFile("테스트1", "");
        MarkdownFile file2 = createMarkdownFile("테스트2", null);
        List<MarkdownFile> files = List.of(file1, file2);

        // when
        List<String> categoryPaths = categoryManager.getAllCategoryPaths(files);

        // then
        assertTrue(categoryPaths.isEmpty());
    }

    @Test
    void buildCategoryTree_카테고리_정렬_확인() {
        // given
        MarkdownFile file1 = createMarkdownFile("Z 게시글", "Z카테고리");
        MarkdownFile file2 = createMarkdownFile("A 게시글", "A카테고리");
        MarkdownFile file3 = createMarkdownFile("M 게시글", "M카테고리");
        List<MarkdownFile> files = List.of(file1, file2, file3);

        // when
        CategoryInfo rootCategory = categoryManager.buildCategoryTree(files);

        // then
        assertEquals(3, rootCategory.subCategories().size());
        
        // 카테고리가 이름순으로 정렬되어 있어야 함
        assertEquals("A카테고리", rootCategory.subCategories().get(0).name());
        assertEquals("M카테고리", rootCategory.subCategories().get(1).name());
        assertEquals("Z카테고리", rootCategory.subCategories().get(2).name());
    }

    private MarkdownFile createMarkdownFile(String title, String path) {
        MarkdownProperties properties = new MarkdownProperties(
                true,
                title,
                LocalDate.now(),
                "테스트 설명",
                "",
                path
        );
        return new MarkdownFile(properties, "테스트 내용", title.toLowerCase().replace(" ", "-") + ".html");
    }
}