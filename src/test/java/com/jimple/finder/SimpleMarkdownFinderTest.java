package com.jimple.finder;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.List;
import java.util.Set;

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
        // 파일 생성과 동시에 내용 쓰기

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

    @Test
    void findAll_ShouldThrowException_WhenRootDirIsNotReadable() throws IOException {
        Path noReadDir = Files.createDirectory(tempDir.resolve("noRead"));

        // POSIX 퍼미션을 지원하는 파일 시스템에서만 테스트 수행
        Assumptions.assumeTrue(Files.getFileAttributeView(noReadDir, java.nio.file.attribute.PosixFileAttributeView.class) != null);

        // 읽기 권한 제거
        Set<PosixFilePermission> perms = PosixFilePermissions.fromString("---------");
        Files.setPosixFilePermissions(noReadDir, perms);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> finder.findAll(noReadDir));
        assertEquals("rootDir must be readable", ex.getMessage());
    }

    @Test
    void findAll_ShouldIncludeFiles_WithUppercaseOrMixedCaseExtensions() throws IOException {
        Path md1 = Files.createFile(tempDir.resolve("README.MD"));
        Path md2 = Files.createFile(tempDir.resolve("ChangeLog.mD"));
        Path txt = Files.createFile(tempDir.resolve("note.txt"));

        List<Path> result = finder.findAll(tempDir);

        assertEquals(2, result.size());
        assertTrue(result.contains(md1));
        assertTrue(result.contains(md2));
        assertFalse(result.contains(txt));
    }

    @Test
    void findAll_ShouldPropagateIOException_AsRuntimeException() throws IOException {
        try (MockedStatic<Files> filesMock = Mockito.mockStatic(Files.class, Mockito.CALLS_REAL_METHODS)) {
            // Files.isDirectory, isReadable 는 정상 동작하도록 유지
            filesMock.when(() -> Files.isDirectory(tempDir)).thenReturn(true);
            filesMock.when(() -> Files.isReadable(tempDir)).thenReturn(true);
            // Files.walk 호출 시 IOException 발생하도록 설정
            filesMock.when(() -> Files.walk(tempDir)).thenThrow(new IOException("simulated I/O error"));

            RuntimeException ex = assertThrows(RuntimeException.class, () -> finder.findAll(tempDir));
            assertTrue(ex.getCause() instanceof IOException);
            assertEquals("Error walking directory", ex.getMessage());
        }
    }

}