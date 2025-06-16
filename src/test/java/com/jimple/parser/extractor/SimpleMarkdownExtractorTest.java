package com.jimple.parser.extractor;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class SimpleMarkdownExtractorTest {
    final SimpleMarkdownExtractor extractor = new SimpleMarkdownExtractor();

    @Nested
    @DisplayName("ExtractContents")
    class ExtractContentsTests {
        @Test
        void testExtractContentWithValidFrontMatter() {
            // Arrange
            String markdownContent = "---\nkey: value\nanotherKey: anotherValue\n---\nSome content here";

            // Act
            String result = extractor.extractContent(markdownContent);

            // Assert
            assertEquals("Some content here", result);
        }

        @Test
        void testExtractContentWithNoDelimiters() {
            // Arrange
            String markdownContent = "This is just some content without any front matter";

            // Act
            String result = extractor.extractContent(markdownContent);

            // Assert
            assertEquals("This is just some content without any front matter", result);
        }

        @Test
        void testExtractContentWithOnlyOneDelimiter() {
            // Arrange
            String markdownContent = "---\nkey: value\nanotherKey: anotherValue";

            // Act
            String result = extractor.extractContent(markdownContent);

            // Assert
            assertEquals("---\nkey: value\nanotherKey: anotherValue", result);
        }

        @Test
        void testExtractContentWithEmptyContent() {
            // Arrange
            String markdownContent = "";

            // Act
            String result = extractor.extractContent(markdownContent);

            // Assert
            assertEquals("", result);
        }

        @Test
        void testExtractContentWithNullContent() {
            // Arrange

            // Act
            String result = extractor.extractContent(null);

            // Assert
            assertEquals("", result);
        }

        @Test
        void testExtractContentWithContentWithBOM() {
            // Arrange
            String markdownContent = "\uFEFF---\nkey: value\n---\nContent with BOM character";

            // Act
            String result = extractor.extractContent(markdownContent);

            // Assert
            assertEquals("Content with BOM character", result);
        }

        @Test
        void testExtractContentWithWhitespaceContent() {
            // Arrange
            String markdownContent = "   \n   ";

            // Act
            String result = extractor.extractContent(markdownContent);

            // Assert
            assertEquals("", result);
        }
    }

    @Nested
    @DisplayName("ExtractFullContents")
    class ExtractFullContentsTests {
        @Test
        void testExtractFullContentsWithValidFile(@TempDir Path tempDir) throws IOException {
            // Arrange
            Path testFile = tempDir.resolve("validFile.md");
            String expectedContent = "---\ntitle: 테스트\n---\n테스트 내용입니다.";
            Files.writeString(testFile, expectedContent, StandardCharsets.UTF_8);
            SimpleMarkdownExtractor extractor = new SimpleMarkdownExtractor();

            // Act
            String result = extractor.extractFullContents(testFile);

            // Assert
            assertEquals(expectedContent, result);
        }

        @Test
        void testExtractFullContentsWithEmptyFile(@TempDir Path tempDir) throws IOException {
            // Arrange
            Path emptyFile = tempDir.resolve("emptyFile.md");
            Files.createFile(emptyFile);
            SimpleMarkdownExtractor extractor = new SimpleMarkdownExtractor();

            // Act
            String result = extractor.extractFullContents(emptyFile);

            // Assert
            assertEquals("", result);
        }

        @Test
        void testExtractFullContentsWithNonexistentFile() {
            // Arrange
            Path nonExistentFile = Path.of("doesNotExist.md");
            SimpleMarkdownExtractor extractor = new SimpleMarkdownExtractor();

            // Act & Assert
            Exception exception = assertThrows(RuntimeException.class, () -> extractor.extractFullContents(nonExistentFile));

            assertTrue(exception.getMessage().contains("파일을 읽는 중 오류가 발생했습니다"));
            assertInstanceOf(IOException.class, exception.getCause());
        }

        @Test
        void testExtractFullContentsWithInvalidPermissions(@TempDir Path tempDir) throws IOException {
            try {
                // Arrange
                Path restrictedFile = tempDir.resolve("restrictedFile.md");
                String content = "접근 제한된 파일 내용";
                Files.writeString(restrictedFile, content, StandardCharsets.UTF_8);

                // 읽기 권한 제거 (소유자에게도 읽기 권한이 없음)
                Set<PosixFilePermission> permissions = PosixFilePermissions.fromString("--x------");
                Files.setPosixFilePermissions(restrictedFile, permissions);

                SimpleMarkdownExtractor extractor = new SimpleMarkdownExtractor();

                // Act & Assert
                Exception exception = assertThrows(RuntimeException.class, () -> extractor.extractFullContents(restrictedFile));

                assertTrue(exception.getMessage().contains("파일을 읽는 중 오류가 발생했습니다"));
                assertInstanceOf(IOException.class, exception.getCause());
            } catch (UnsupportedOperationException e) {
                // POSIX 파일 권한이 지원되지 않는 시스템(예: Windows)에서는 테스트를 건너뜁니다.
                System.out.println("이 테스트는 현재 파일 시스템에서 지원되지 않습니다.");
            }
        }

    }

    @Nested
    @DisplayName("OtherTest")
    class OtherTests {
        @Test
        void testBOMRemovalInFullWorkflow(@TempDir Path tempDir) throws IOException {
            // Arrange
            Path testFile = tempDir.resolve("fileWithBOM.md");
            String contentWithBOM = "\uFEFF---\ntitle: 테스트\n---\n본문 내용";
            Files.writeString(testFile, contentWithBOM, StandardCharsets.UTF_8);
            SimpleMarkdownExtractor extractor = new SimpleMarkdownExtractor();

            // Act - 전체 워크플로우 테스트
            String fullContents = extractor.extractFullContents(testFile);
            String frontMatter = extractor.extractFrontMatter(fullContents);
            String content = extractor.extractContent(fullContents);

            // Assert
            assertEquals("title: 테스트", frontMatter);
            assertEquals("본문 내용", content);
        }

        // 2. 경계 케이스 및 특수 상황 테스트
        @Test
        void testWithMultipleDelimiters() {
            // Arrange
            String markdownContent = "---\ntitle: 첫번째 프론트매터\n---\n첫번째 내용\n---\n두번째 내용\n---\n세번째 내용";
            SimpleMarkdownExtractor extractor = new SimpleMarkdownExtractor();

            // Act
            String frontMatter = extractor.extractFrontMatter(markdownContent);
            String content = extractor.extractContent(markdownContent);

            // Assert
            assertEquals("title: 첫번째 프론트매터", frontMatter);
            assertEquals("첫번째 내용\n---\n두번째 내용\n---\n세번째 내용", content);
        }

        @Test
        void testWithSpecialCharactersInFrontMatter() {
            // Arrange
            String markdownContent = "---\ntitle: 특수문자 테스트 !@#$%^&*()\nkey: 한글과 영어가 섞인 값 English and 한글\nemotion: 😀 😃 😄 😁\n---\n본문 내용";
            SimpleMarkdownExtractor extractor = new SimpleMarkdownExtractor();

            // Act
            String frontMatter = extractor.extractFrontMatter(markdownContent);
            String content = extractor.extractContent(markdownContent);

            // Assert
            assertEquals("title: 특수문자 테스트 !@#$%^&*()\nkey: 한글과 영어가 섞인 값 English and 한글\nemotion: 😀 😃 😄 😁", frontMatter);
            assertEquals("본문 내용", content);
        }

        @Test
        void testWithLargeFrontMatter() {
            // Arrange
            StringBuilder largeFrontMatterBuilder = new StringBuilder("---\n");
            for (int i = 0; i < 1000; i++) {
                largeFrontMatterBuilder.append("key").append(i).append(": value").append(i).append("\n");
            }
            largeFrontMatterBuilder.append("---\n본문 내용");

            String markdownContent = largeFrontMatterBuilder.toString();
            SimpleMarkdownExtractor extractor = new SimpleMarkdownExtractor();

            // Act
            String frontMatter = extractor.extractFrontMatter(markdownContent);
            String content = extractor.extractContent(markdownContent);

            // Assert
            assertTrue(frontMatter.startsWith("key0: value0"));
            assertTrue(frontMatter.contains("key500: value500"));
            assertTrue(frontMatter.endsWith("key999: value999"));
            assertEquals("본문 내용", content);
        }

        @Test
        void testWithLargeContent() {
            // Arrange
            StringBuilder largeContentBuilder = new StringBuilder("---\ntitle: 대용량 콘텐츠 테스트\n---\n");
            for (int i = 0; i < 10000; i++) {
                largeContentBuilder.append("이것은 대용량 콘텐츠 테스트를 위한 반복 텍스트입니다. 줄 번호: ").append(i).append("\n");
            }

            String markdownContent = largeContentBuilder.toString();
            SimpleMarkdownExtractor extractor = new SimpleMarkdownExtractor();

            // Act
            String frontMatter = extractor.extractFrontMatter(markdownContent);
            String content = extractor.extractContent(markdownContent);

            // Assert
            assertEquals("title: 대용량 콘텐츠 테스트", frontMatter);
            assertTrue(content.startsWith("이것은 대용량 콘텐츠 테스트를 위한 반복 텍스트입니다. 줄 번호: 0"));
            assertTrue(content.contains("이것은 대용량 콘텐츠 테스트를 위한 반복 텍스트입니다. 줄 번호: 5000"));
            assertTrue(content.endsWith("이것은 대용량 콘텐츠 테스트를 위한 반복 텍스트입니다. 줄 번호: 9999"));
        }
    }
}