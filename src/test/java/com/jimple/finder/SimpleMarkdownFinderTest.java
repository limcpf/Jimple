package com.jimple.finder;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

class SimpleMarkdownFinderTest {

    @TempDir
    Path tempDir;

    private final SimpleMarkdownFinder finder = new SimpleMarkdownFinder();

    @Test
    void findAll_ShouldThrowException_WhenRootDirIsNull() {
        assertThrows(IllegalArgumentException.class, () -> finder.findAll(null),
                "rootDir must not be null");
    }

    @Test
    void findAll_ShouldThrowException_WhenRootDirIsNotDirectory() throws IOException {
        Path file = Files.createFile(tempDir.resolve("test.txt"));
        assertThrows(IllegalArgumentException.class, () -> finder.findAll(file),
                "rootDir must be a directory");
    }

    @Test
    void findAll_ShouldReturnAllFiles() throws IOException {
        // 테스트 파일 생성
        Path file1 = Files.createFile(tempDir.resolve("test1.txt"));
//         파일 생성과 동시에 내용 쓰기
//        Path file1 = Files.createFile(tempDir.resolve("test1.txt"));
//        Files.writeString(file1, "여기에 원하는 내용을 입력하세요");

        Path file2 = Files.createFile(tempDir.resolve("test2.md"));
        Path subDir = Files.createDirectory(tempDir.resolve("subDir"));
        Path file3 = Files.createFile(subDir.resolve("test3.md"));

        List<Path> result = finder.findAll(tempDir);

        assertEquals(2, result.size());
        assertFalse(result.contains(file1));
        assertTrue(result.contains(file2));
        assertTrue(result.contains(file3));
    }

    @Test
    void findAll_ShouldReturnEmptyList_WhenDirectoryIsEmpty() {
        List<Path> result = finder.findAll(tempDir);

        assertTrue(result.isEmpty());
    }
}