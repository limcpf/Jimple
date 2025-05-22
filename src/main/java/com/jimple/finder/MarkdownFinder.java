package com.jimple.finder;

import java.nio.file.Path;
import java.util.List;

public interface MarkdownFinder {
    List<Path> findAll(Path rootDir);
}
