package com.jimple;

import com.jimple.collector.MarkdownCollector;
import com.jimple.finder.SimpleMarkdownFinder;
import com.jimple.generator.MarkdownGenerator;
import com.jimple.generator.SimpleMarkdownGenerator;
import com.jimple.generator.converter.SimpleMd2HtmlConverter;
import com.jimple.manager.ResultManager;
import com.jimple.parser.extractor.SimpleMarkdownExtractor;
import com.jimple.parser.yml.MarkdownYmlParser;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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
            case "help":
                printHelp();
                break;
            case "version":
                System.out.println("Jimple v1.0.0");
                break;
            case "run":
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
                Path resultDir = args.length > 2 ?
                        Paths.get(args[2]) :
                        Paths.get(DEFAULT_RESULT_DIR);

                ResultManager manager = createResultManager(resultDir);
                manager.processAndSaveResults(sourceDir);
                break;
            default:
                System.out.println("알 수 없는 명령: " + command);
                printHelp();
                break;
        }
    }

    private static void printHelp() {
        System.out.println("사용법: jimple <command> [options]");
        System.out.println("Commands:");
        System.out.println("  help      도움말 표시");
        System.out.println("  version   버전 정보 표시");
        System.out.println("  run       마크다운 파일을 처리하여 HTML로 변환");
        System.out.println("            사용법: run <소스디렉터리> [결과디렉터리]");
    }

    private static ResultManager createResultManager(Path resultDir) {
        MarkdownCollector collector = new MarkdownCollector(
                new SimpleMarkdownFinder(),
                new MarkdownYmlParser(),
                new SimpleMarkdownExtractor()
        );
        MarkdownGenerator generator = new SimpleMarkdownGenerator(new SimpleMd2HtmlConverter());
        return new ResultManager(collector, generator, resultDir);
    }
}
