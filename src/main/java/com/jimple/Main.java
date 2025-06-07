package com.jimple;

import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.jimple.collector.MarkdownCollector;
import com.jimple.finder.SimpleMarkdownFinder;
import com.jimple.generator.MarkdownGenerator;
import com.jimple.generator.SimpleMarkdownGenerator;
import com.jimple.generator.converter.SimpleMd2HtmlConverter;
import com.jimple.manager.ResultManager;
import com.jimple.model.Properties;
import com.jimple.model.config.BlogProperties;
import com.jimple.parser.extractor.SimpleMarkdownExtractor;
import com.jimple.parser.template.SimpleTemplateEngine;
import com.jimple.parser.yml.ConfigYmlParser;
import com.jimple.parser.yml.SimpleMarkdownYmlParser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.stream.Stream;

public class Main {
    private static final String DEFAULT_RESULT_DIR = "jimple-result";

    public static void main(String[] args) {
        // 명령줄 인수 처리
        if (args.length == 0) {
            printHelp();
            return;
        }

        // 인수에 따른 명령 처리
        String command = args[0];
        switch (command) {
            case "help" -> printHelp();
            case "version" -> System.out.println("Jimple v1.0.0");
            case "run" -> runCommand(args);
            default -> {
                System.out.println("알 수 없는 명령: " + command);
                printHelp();
            }
        }
    }

    private static void runCommand(String[] args) {
        if (args.length < 2) {
            System.out.println("소스 디렉터리를 지정해주세요.");
            printHelp();
            return;
        }

        Path sourceDir = Paths.get(args[1]);
        if (!Files.isDirectory(sourceDir)) {
            System.out.println("유효한 소스 디렉터리를 지정해주세요: " + sourceDir);
            return;
        }

        Path resultDir = (args.length > 2 && !args[2].startsWith("--"))
                ? Paths.get(args[2])
                : Paths.get(DEFAULT_RESULT_DIR);

        Path configPath = null;
        for (int i = 2; i < args.length; i++) {
            if (args[i].equals("--config") && i + 1 < args.length) {
                configPath = Paths.get(args[i + 1]);
                break;
            }
        }

        if (configPath == null) {
            configPath = Path.of("");
        }

        // 설정 파일 로드
        Properties blogProperties;
        ConfigYmlParser configParser = new ConfigYmlParser(new YAMLMapper());

        try {
            if (Files.exists(configPath)) {
                String configContent = Files.readString(configPath);
                blogProperties = configParser.getProperties(configContent);
                System.out.println("설정 파일을 로드했습니다: " + configPath);
            } else {
                blogProperties = configParser.getProperties("");
                System.out.println("설정 파일이 없어 기본 설정을 사용합니다.");
            }
        } catch (IOException e) {
            System.err.println("설정 파일 로드 중 오류 발생: " + e.getMessage());
            blogProperties = configParser.getProperties("");
        }

        cleanupResultDirectory(resultDir);
        copyAssets(resultDir);

        ResultManager manager = createResultManager(resultDir, blogProperties);
        manager.processAndSaveResults(sourceDir);
    }

    private static void printHelp() {
        System.out.println("사용법: jimple <command> [options]");
        System.out.println("Commands:");
        System.out.println("  help      도움말 표시");
        System.out.println("  version   버전 정보 표시");
        System.out.println("  run       마크다운 파일을 처리하여 HTML로 변환");
        System.out.println("            사용법: run <소스디렉터리> [결과디렉터리]");
    }

    private static ResultManager createResultManager(Path resultDir, Properties config) {
        MarkdownCollector collector = new MarkdownCollector(
                new SimpleMarkdownFinder(),
                new SimpleMarkdownYmlParser(),
                new SimpleMarkdownExtractor()
        );
        
        MarkdownGenerator generator = new SimpleMarkdownGenerator(
                new SimpleMd2HtmlConverter(),
                (BlogProperties) config,
                new SimpleTemplateEngine()
        );

        return new ResultManager(collector, generator, resultDir);
    }

    private static void cleanupResultDirectory(Path resultDir) {
        if (Files.exists(resultDir)) {
            try (Stream<Path> pathStream = Files.walk(resultDir)) {
                pathStream
                        .sorted((a, b) -> -a.compareTo(b))
                        .forEach(path -> {
                            try {
                                Files.delete(path);
                            } catch (IOException e) {
                                System.err.println("파일 삭제 중 오류 발생: " + path);
                            }
                        });
            } catch (IOException e) {
                System.err.println("결과 디렉터리 정리 중 오류 발생: " + resultDir);
            }
        }
    }


    private static void copyAssets(Path resultDir) {
        try {
            Files.createDirectories(resultDir.resolve("assets"));
            Files.createDirectories(resultDir.resolve("assets/fonts"));

            Files.copy(
                    Objects.requireNonNull(Main.class.getResourceAsStream("/jimple.css")),
                    resultDir.resolve("assets/jimple.css")
            );

            Files.copy(
                    Objects.requireNonNull(Main.class.getResourceAsStream("/fonts/IBMPlexSansKR-Regular-subset.woff2")),
                    resultDir.resolve("assets/fonts/IBMPlexSansKR-Regular.woff2")
            );

            Files.copy(
                    Objects.requireNonNull(Main.class.getResourceAsStream("/fonts/IBMPlexSansKR-Bold-subset.woff2")),
                    resultDir.resolve("assets/fonts/IBMPlexSansKR-Bold.woff2")
            );

        } catch (IOException | NullPointerException e) {
            throw new RuntimeException("에셋 파일 복사 중 오류 발생", e);
        }
    }
}
