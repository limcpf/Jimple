package com.jimple.parser.yml;

import com.jimple.model.MarkdownProperties;
import com.jimple.model.Properties;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

public class MarkdownYmlParserTest {
    private final MarkdownYmlParser parser = new MarkdownYmlParser();

    @Nested
    @DisplayName("getProperties")
    class getPropertiesTest {
        @Test
        void getProperties_ShouldReturnProperties() {
            String markdownContents = """
                    ---
                    publish: "true"
                    date: 2025-05-21
                    title: java.nio.file.Files.walk
                    ---
                    # Hello World
                    > Hahaha
                    """;

            MarkdownProperties markdownProperties = parser.getProperties(markdownContents);

            Assertions.assertTrue(markdownProperties.publish());
            Assertions.assertTrue("java.nio.file.Files.walk".equals(markdownProperties.title()));
            Assertions.assertTrue(LocalDate.of(2025,05,21).equals(markdownProperties.date()));
        }
    }


}
