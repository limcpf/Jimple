package com.jimple.finder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

public class SimpleMarkdownFinder implements MarkdownFinder{
    @Override
    public List<Path> findAll(Path rootDir) {
        if(rootDir == null) throw new IllegalArgumentException("rootDir must not be null");
        if(!Files.isDirectory(rootDir)) throw new IllegalArgumentException("rootDir must be a directory");
        if(!Files.isReadable(rootDir)) throw new IllegalArgumentException("rootDir must be readable");

        try (Stream<Path> paths = Files.walk(rootDir)) {
            return paths
                    .filter(Files::isRegularFile)
                    .filter(x -> x.endsWith(".md"))
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException("Error walking directory", e);
        }
    }
}
