package com.jimple.manager;

import com.jimple.collector.MarkdownFileMapper;
import com.jimple.finder.MarkdownFinder;
import com.jimple.generator.MarkdownGenerator;
import com.jimple.model.md.MarkdownFile;
import com.jimple.model.md.MarkdownProperties;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * ResultManager 클래스의 테스트를 수행하는 클래스
 * ResultManager는 마크다운 파일을 찾고, HTML로 변환하여 결과 디렉토리에 저장하는 역할을 함
 */
class ResultManagerTest {
    // 테스트 대상 클래스와 의존성 객체들
    private ResultManager resultManager;
    private MarkdownFinder mockFinder;        // 마크다운 파일을 찾는 인터페이스
    private MarkdownFileMapper mockMapper;    // 마크다운 파일을 변환하는 클래스
    private MarkdownGenerator mockGenerator;  // HTML 생성을 담당하는 클래스
    private Path mockResultDir;               // 결과물이 저장될 디렉토리 경로

    /**
     * 각 테스트 실행 전에 호출되어 테스트 환경을 설정
     * 모든 의존성 객체를 모킹하고 ResultManager 인스턴스를 생성
     */
    @BeforeEach
    void setUp() {
        mockFinder = mock(MarkdownFinder.class);
        mockMapper = mock(MarkdownFileMapper.class);
        mockGenerator = mock(MarkdownGenerator.class);
        mockResultDir = mock(Path.class);

        // Files 클래스의 정적 메서드를 모킹하여 exists 메서드가 true를 반환하도록 설정
        try (MockedStatic<Files> mockedFiles = Mockito.mockStatic(Files.class)) {
            mockedFiles.when(() -> Files.exists(any(Path.class))).thenReturn(true);

            // ResultManager 인스턴스 생성
            resultManager = new ResultManager(mockFinder, mockMapper, mockGenerator, mockResultDir);
        }
    }

    /**
     * processAndSaveResults 메서드의 기본 동작을 테스트
     * 마크다운 파일을 찾고, 변환하여 HTML로 저장하는 과정을 검증
     */
    @Test
    void testProcessAndSaveResults() throws IOException {
        // 테스트에 필요한 모의 객체 설정
        Path mockSourceDir = mock(Path.class);
        Path mockMarkdownFilePath = mock(Path.class);

        // 일반 마크다운 파일 객체 생성
        MarkdownFile mockMarkdownFile = new MarkdownFile(
                new MarkdownProperties(true, "file1", LocalDate.of(2020, 1, 1)),
                "",
                "file1.html"
        );

        // 메인 페이지용 마크다운 파일 객체 생성
        MarkdownFile mockMainPage = new MarkdownFile(
                new MarkdownProperties(true, "index", LocalDate.now()),
                "",
                "index.html"
        );

        // 마크다운 파일 목록과 게시 대상 항목 설정
        List<Path> markdownFiles = List.of(mockMarkdownFilePath);
        List<MarkdownFile> publishedItems = new ArrayList<>(List.of(mockMarkdownFile));

        // 모의 객체 동작 정의
        when(mockFinder.findAll(mockSourceDir)).thenReturn(markdownFiles);
        when(mockMapper.collectPublishedMarkdownFiles(markdownFiles)).thenReturn(publishedItems);
        when(mockGenerator.generateToHtml(Mockito.eq(mockMarkdownFile))).thenReturn("<html>File1</html>");
        when(mockGenerator.generateMainPage(Mockito.eq(mockMainPage), Mockito.any())).thenReturn("<html>Index</html>");

        try (MockedStatic<Files> mockedFiles = Mockito.mockStatic(Files.class)) {
            mockedFiles.when(() -> Files.writeString(any(Path.class), anyString())).thenReturn(mock(Path.class));

            // 테스트 대상 메서드 실행
            resultManager.processAndSaveResults(mockSourceDir);

            // 저장될 HTML 내용을 캡처하기 위한 ArgumentCaptor 설정
            ArgumentCaptor<String> contentCaptor = ArgumentCaptor.forClass(String.class);

            // 메서드 호출 검증
            verify(mockResultDir).resolve("file1.html");
            verify(mockResultDir).resolve("index.html");

            verify(mockGenerator).generateToHtml(Mockito.eq(mockMarkdownFile));
            verify(mockGenerator).generateMainPage(Mockito.eq(mockMainPage), Mockito.any());

            // Files 클래스의 정적 메서드 호출 검증 (3번 호출됨)
            // 2025-06-25 json 쓰기 추가
            verify(Files.class, times(3));
            Files.writeString(Mockito.any(), contentCaptor.capture());

            // 캡처된 HTML 내용 검증
            List<String> capturedContent = contentCaptor.getAllValues();
            assertEquals("""
                    <html>
                        <head></head>
                        <body>
                            File1
                        </body>
                    </html>""", capturedContent.get(1));
            assertEquals("""
                    <html>
                        <head></head>
                        <body>
                            Index
                        </body>
                    </html>""", capturedContent.get(2));
        }
    }

    /**
     * 게시글 저장 중 예외가 발생하는 경우 processAndSaveResults 메서드의 동작을 테스트
     * processPostHtml 에서 IOException이 발생하면 RuntimeException 으로 래핑되어 예외가 발생해야 함
     */
    @Test
    void testProcessAndSaveResultsWhenFileWriteFails() {
        // 테스트에 필요한 모의 객체 설정
        Path mockSourceDir = mock(Path.class);
        Path mockMarkdownFilePath = mock(Path.class);
        Path mockResultPath = mock(Path.class);

        // 오류가 발생할 마크다운 파일 객체 생성
        MarkdownFile mockMarkdownFile = new MarkdownFile(
                new MarkdownProperties(true, "errorFile", LocalDate.of(2025, 1, 1)),
                "",
                "errorFile.html"
        );

        // 마크다운 파일 목록과 게시 대상 항목 설정
        List<Path> markdownFiles = List.of(mockMarkdownFilePath);
        List<MarkdownFile> publishedItems = new ArrayList<>(List.of(mockMarkdownFile));

        // 모의 객체 동작 정의
        when(mockFinder.findAll(mockSourceDir)).thenReturn(markdownFiles);
        when(mockMapper.collectPublishedMarkdownFiles(markdownFiles)).thenReturn(publishedItems);
        when(mockGenerator.generateToHtml(eq(mockMarkdownFile))).thenReturn("<html>ErrorFile</html>");
        when(mockResultDir.resolve(anyString())).thenReturn(mock(Path.class));
        when(mockResultDir.resolve(eq(mockMarkdownFile.path()))).thenReturn(mockResultPath);


        // Files.writeString 메서드 호출 시 IOException 발생하도록 설정
        try (MockedStatic<Files> mockedFiles = Mockito.mockStatic(Files.class)) {
            mockedFiles.when(() -> Files.writeString(eq(mockResultPath), anyString())).thenThrow(new IOException("Write failed"));

            // 예외 발생 검증
            RuntimeException exception = assertThrows(
                    RuntimeException.class,
                    () -> resultManager.processAndSaveResults(mockSourceDir)
            );

            // 예외 메시지 검증
            assertTrue(exception.getMessage().contains("HTML 파일을 저장할 수 없습니다: "));

            // 메서드 호출 검증
            verify(mockFinder).findAll(mockSourceDir);
            verify(mockMapper).collectPublishedMarkdownFiles(markdownFiles);
            verify(mockGenerator).generateToHtml(eq(mockMarkdownFile));
        }
    }
    /**
     * 게시글 저장 중 예외가 발생하는 경우 processAndSaveResults 메서드의 동작을 테스트
     * processPostListJson 에서 IOException이 발생하면 RuntimeException 으로 래핑되어 예외가 발생해야 함
     */
    @Test
    void testProcessAndSaveResultsWhenJsonWriteFails() {
        // 테스트에 필요한 모의 객체 설정
        Path mockSourceDir = mock(Path.class);
        Path mockMarkdownFilePath = mock(Path.class);
        Path mockResultPath = mock(Path.class);

        // 오류가 발생할 마크다운 파일 객체 생성
        MarkdownFile mockMarkdownFile = new MarkdownFile(
                new MarkdownProperties(true, "errorFile", LocalDate.of(2025, 1, 1)),
                "",
                "errorFile.html"
        );

        // 마크다운 파일 목록과 게시 대상 항목 설정
        List<Path> markdownFiles = List.of(mockMarkdownFilePath);
        List<MarkdownFile> publishedItems = new ArrayList<>(List.of(mockMarkdownFile));

        // 모의 객체 동작 정의
        when(mockFinder.findAll(mockSourceDir)).thenReturn(markdownFiles);
        when(mockMapper.collectPublishedMarkdownFiles(markdownFiles)).thenReturn(publishedItems);
        when(mockGenerator.generateToHtml(eq(mockMarkdownFile))).thenReturn("<html>ErrorFile</html>");
        when(mockResultDir.resolve(anyString())).thenReturn(mockResultPath);
        when(mockResultDir.resolve(eq(mockMarkdownFile.path()))).thenReturn(mock(Path.class));


        // Files.writeString 메서드 호출 시 IOException 발생하도록 설정
        try (MockedStatic<Files> mockedFiles = Mockito.mockStatic(Files.class)) {
            mockedFiles.when(() -> Files.writeString(eq(mockResultPath), anyString())).thenThrow(new IOException("Write failed"));

            // 예외 발생 검증
            RuntimeException exception = assertThrows(
                    RuntimeException.class,
                    () -> resultManager.processAndSaveResults(mockSourceDir)
            );

            // 예외 메시지 검증
            assertTrue(exception.getMessage().contains("PostPage 파일을 저장할 수 없습니다: "));

            // 메서드 호출 검증
            verify(mockFinder).findAll(mockSourceDir);
            verify(mockMapper).collectPublishedMarkdownFiles(markdownFiles);
        }
    }

    /**
     * 결과 디렉토리 생성 기능 테스트
     * 생성자에서 디렉토리가 없을 때 생성하는 기능 검증
     */
    @Test
    void testConstructorCreatesResultDirectoryIfNotExists() {
        // 디렉토리가 존재하지 않는 상황 설정
        try (MockedStatic<Files> mockedFiles = Mockito.mockStatic(Files.class)) {
            mockedFiles.when(() -> Files.exists(any(Path.class))).thenReturn(false);

            // 생성자 호출
            new ResultManager(mockFinder, mockMapper, mockGenerator, mockResultDir);

            // Files.createDirectories 메서드 호출 검증
            mockedFiles.verify(() -> Files.createDirectories(mockResultDir), times(1));
        }
    }

    /**
     * 결과 디렉토리 생성 실패 시 예외 처리 테스트
     * 디렉토리 생성 중 IOException이 발생하면 RuntimeException 으로 래핑되어 예외가 발생해야 함
     */
    @Test
    void testConstructorThrowsExceptionWhenDirectoryCreationFails() {
        try (MockedStatic<Files> mockedFiles = Mockito.mockStatic(Files.class)) {
            mockedFiles.when(() -> Files.exists(any(Path.class))).thenReturn(false);
            mockedFiles.when(() -> Files.createDirectories(any(Path.class)))
                    .thenThrow(new IOException("Directory creation failed"));

            // 예외 발생 검증
            RuntimeException exception = assertThrows(
                    RuntimeException.class,
                    () -> new ResultManager(mockFinder, mockMapper, mockGenerator, mockResultDir)
            );

            // 예외 메시지 검증
            assertEquals("결과 디렉토리를 생성할 수 없습니다: " + mockResultDir, exception.getMessage());
        }
    }

    /**
     * 결과 디렉토리 생성 기능 테스트
     * 디렉토리가 존재하는 경우와 존재하지 않는 경우 모두 테스트
     */
    @Test
    void testResultDirectoryCreation() {
        // 기존 mock 객체 재설정
        MarkdownFinder testFinder = mock(MarkdownFinder.class);
        MarkdownFileMapper testMapper = mock(MarkdownFileMapper.class);
        MarkdownGenerator testGenerator = mock(MarkdownGenerator.class);
        Path testResultDir = mock(Path.class);

        // 케이스 1: 디렉토리가 이미 존재하는 경우
        try (MockedStatic<Files> mockedFiles = Mockito.mockStatic(Files.class)) {
            // 디렉토리가 이미 존재하는 상황 설정
            mockedFiles.when(() -> Files.exists(any(Path.class))).thenReturn(true);

            // 생성자 호출
            new ResultManager(testFinder, testMapper, testGenerator, testResultDir);

            // Files.exists는 호출되었지만 Files.createDirectories는 호출되지 않아야 함
            mockedFiles.verify(() -> Files.exists(testResultDir), times(1));
            mockedFiles.verify(() -> Files.createDirectories(testResultDir), never());
        }

        // 케이스 2: 디렉토리가 존재하지 않는 경우
        try (MockedStatic<Files> mockedFiles = Mockito.mockStatic(Files.class)) {
            // 디렉토리가 존재하지 않는 상황 설정
            mockedFiles.when(() -> Files.exists(any(Path.class))).thenReturn(false);

            // 생성자 호출
            new ResultManager(testFinder, testMapper, testGenerator, testResultDir);

            // Files.exists와 Files.createDirectories 모두 호출되어야 함
            mockedFiles.verify(() -> Files.exists(testResultDir), times(1));
            mockedFiles.verify(() -> Files.createDirectories(testResultDir), times(1));
        }
    }

    /**
     * 메인 페이지 생성 로직 테스트
     * file.path()가 "index.html"일 때 generator.generateMainPage()가 호출되는지 검증
     * 다른 경로일 때는 generator.generateToHtml()이 호출되는지 검증
     */
    @Test
    void testMainPageGenerationLogic() {
        // 모의 객체 설정
        Path mockSourceDir = mock(Path.class);
        Path mockIndexPath = mock(Path.class);
        Path mockArticlePath = mock(Path.class);

        // 메인 페이지 마크다운 파일 객체 생성
        MarkdownFile mainPageFile = new MarkdownFile(
                new MarkdownProperties(true, "index", LocalDate.now()),
                "",
                "index.html"
        );

        // 일반 게시글 마크다운 파일 객체 생성
        MarkdownFile articleFile = new MarkdownFile(
                new MarkdownProperties(true, "일반 게시글", LocalDate.now()),
                "",
                "article.html"
        );

        // 발행된 항목 리스트 설정
        List<MarkdownFile> publishedItems = new ArrayList<>();
        publishedItems.add(articleFile);

        // 모의 객체 동작 정의
        when(mockFinder.findAll(mockSourceDir)).thenReturn(List.of());
        when(mockMapper.collectPublishedMarkdownFiles(any())).thenReturn(publishedItems);
        when(mockGenerator.generateMainPage(eq(mainPageFile), any())).thenReturn("<html>메인 페이지</html>");
        when(mockGenerator.generateToHtml(articleFile)).thenReturn("<html>일반 게시글</html>");
        when(mockGenerator.generateMainPage(any(), any())).thenReturn("<html>Index</html>");
        when(mockResultDir.resolve("index.html")).thenReturn(mockIndexPath);
        when(mockResultDir.resolve("article.html")).thenReturn(mockArticlePath);

        // Files 정적 메서드 모킹
        try (MockedStatic<Files> mockedFiles = Mockito.mockStatic(Files.class)) {
            mockedFiles.when(() -> Files.writeString(any(Path.class), anyString())).thenReturn(null);

            // 테스트 대상 메서드 실행
            resultManager.processAndSaveResults(mockSourceDir);

            // 메인 페이지에 대해 generateMainPage가 호출되었는지 검증
            verify(mockGenerator, times(1)).generateMainPage(eq(mainPageFile), any());

            // 일반 게시글에 대해 generateToHtml이 호출되었는지 검증
            verify(mockGenerator, times(1)).generateToHtml(articleFile);

            // 두 파일이 모두 저장되었는지 검증
            mockedFiles.verify(() -> Files.writeString(eq(mockIndexPath), anyString()), times(1));
            mockedFiles.verify(() -> Files.writeString(eq(mockArticlePath), anyString()), times(1));
        }
    }

    /**
     * HTML 변환 및 포맷팅 테스트
     * Jsoup 파서를 통한 HTML 포맷팅이 올바르게 적용되는지 검증
     */
    @Test
    void testHtmlFormattingWithJsoup() {
        // 모의 객체 설정
        Path mockSourceDir = mock(Path.class);
        Path virtualFilePath = Path.of("/virtual/path/test.html");

        // 테스트할 HTML 내용
        String rawHtml = "<html><body><div><p>테스트</p></div></body></html>";
        Document expectedFormattedDoc = Jsoup.parse(rawHtml);
        expectedFormattedDoc.outputSettings().prettyPrint(true);
        expectedFormattedDoc.outputSettings().indentAmount(4);

        String expectedFormattedHtml = expectedFormattedDoc.outerHtml();

        // 마크다운 파일 객체 생성
        MarkdownFile testFile = new MarkdownFile(
                new MarkdownProperties(true, "테스트 파일", LocalDate.now()),
                "",
                "test.html"
        );

        // 모의 객체 동작 정의
        when(mockFinder.findAll(mockSourceDir)).thenReturn(List.of());
        when(mockMapper.collectPublishedMarkdownFiles(any())).thenReturn(new ArrayList<>(List.of(testFile)));
        when(mockGenerator.generateToHtml(testFile)).thenReturn(rawHtml);
        when(mockGenerator.generateMainPage(any(), any())).thenReturn(rawHtml);
        when(mockResultDir.resolve("test.html")).thenReturn(virtualFilePath);

        // 파일 저장 시 전달되는 HTML 캡처를 위한 ArgumentCaptor 설정
        ArgumentCaptor<String> htmlCaptor = ArgumentCaptor.forClass(String.class);

        // Files 정적 메서드 모킹
        try (MockedStatic<Files> mockedFiles = Mockito.mockStatic(Files.class)) {
            mockedFiles.when(() -> Files.writeString(eq(virtualFilePath), htmlCaptor.capture())).thenReturn(null);

            // 테스트 대상 메서드 실행
            resultManager.processAndSaveResults(mockSourceDir);

            // 캡처된 HTML이 예상대로 포맷팅되었는지 검증
            String capturedHtml = htmlCaptor.getValue();
            assertEquals(expectedFormattedHtml, capturedHtml);

            // Jsoup 파싱 및 포맷팅 설정이 적용되었는지 확인
            Document doc = Jsoup.parse(capturedHtml);
            assertTrue(doc.outputSettings().prettyPrint());
        }
    }

    /**
     * 디렉토리 생성 중 예외 발생 처리 테스트
     * 생성자에서 디렉토리를 생성할 수 없을 때 RuntimeException이 발생하는지 검증
     * 예외 메시지가 "결과 디렉토리를 생성할 수 없습니다: {경로}" 형식인지 확인
     */
    @Test
    void testDirectoryCreationExceptionHandling() {
        // 테스트를 위한 모의 객체 설정
        MarkdownFinder testFinder = mock(MarkdownFinder.class);
        MarkdownFileMapper testMapper = mock(MarkdownFileMapper.class);
        MarkdownGenerator testGenerator = mock(MarkdownGenerator.class);
        Path testResultDir = mock(Path.class);

        // 디렉토리 경로 문자열 표현 설정
        when(testResultDir.toString()).thenReturn("/test/result/dir");

        // Files 정적 메서드 모킹
        try (MockedStatic<Files> mockedFiles = Mockito.mockStatic(Files.class)) {
            // 디렉토리가 존재하지 않는 상황 설정
            mockedFiles.when(() -> Files.exists(testResultDir)).thenReturn(false);

            // 디렉토리 생성 시 IOException 발생 설정
            IOException testException = new IOException("테스트 예외");
            mockedFiles.when(() -> Files.createDirectories(testResultDir)).thenThrow(testException);

            // 예외 발생 및 메시지 검증
            RuntimeException exception = assertThrows(
                    RuntimeException.class,
                    () -> new ResultManager(testFinder, testMapper, testGenerator, testResultDir)
            );

            assertEquals("결과 디렉토리를 생성할 수 없습니다: /test/result/dir", exception.getMessage());
            assertEquals(testException, exception.getCause());
        }
    }

    @Test
    void testProcessPostListJson() throws IOException {
        List<MarkdownFile> markdownFiles = Stream.of("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10").map(x -> new MarkdownFile(
                                new MarkdownProperties(true, "title-" + x, LocalDate.now()),
                        "",
                        "/title-" + x
                ))
                .collect(Collectors.toCollection(ArrayList::new));

        // 메인 페이지용 마크다운 파일 객체 생성
        MarkdownFile mockMainPage = new MarkdownFile(
                new MarkdownProperties(true, "index", LocalDate.now()),
                "",
                "index.html"
        );

        // 테스트에 필요한 모의 객체 설정
        Path mockSourceDir = mock(Path.class);

        List<Path> markdownFilePaths = markdownFiles.stream()
                .map(x -> Path.of(x.path()))
                .collect(Collectors.toCollection(ArrayList::new));

        // 모의 객체 동작 정의
        when(mockFinder.findAll(mockSourceDir)).thenReturn(markdownFilePaths);
        when(mockMapper.collectPublishedMarkdownFiles(markdownFilePaths)).thenReturn(markdownFiles);
        when(mockGenerator.generateToHtml(any())).thenReturn("<html>File1</html>");
        when(mockGenerator.generateMainPage(Mockito.eq(mockMainPage), Mockito.any())).thenReturn("<html>Index</html>");

        try (MockedStatic<Files> mockedFiles = Mockito.mockStatic(Files.class)) {
            mockedFiles.when(() -> Files.writeString(any(Path.class), anyString())).thenReturn(mock(Path.class));

            // 테스트 대상 메서드 실행
            resultManager.processAndSaveResults(mockSourceDir);

            // 저장될 HTML 내용을 캡처하기 위한 ArgumentCaptor 설정
            ArgumentCaptor<String> contentCaptor = ArgumentCaptor.forClass(String.class);

            // 메서드 호출 검증
            verify(mockResultDir).resolve("post-list-1.json");
            verify(mockResultDir).resolve("post-list-2.json");

            // Files 클래스의 정적 메서드 호출 검증 (3번 호출됨)
            // 2025-06-25 json 쓰기 추가
            verify(Files.class, times(14));
            Files.writeString(Mockito.any(), contentCaptor.capture());

            // 캡처된 HTML 내용 검증
            List<String> capturedContent = contentCaptor.getAllValues();
            System.out.println(capturedContent.get(0));
            System.out.println(capturedContent.get(1));
            assertTrue(capturedContent.get(0).contains("\"current\":1"));
            assertTrue(capturedContent.get(1).contains("\"current\":2"));
        }
    }

}