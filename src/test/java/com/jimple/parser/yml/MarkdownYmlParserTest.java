package com.jimple.parser.yml;

import com.jimple.model.MarkdownProperties;
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
            Assertions.assertEquals("java.nio.file.Files.walk", markdownProperties.title());
            Assertions.assertEquals(LocalDate.of(2025,5,21), markdownProperties.date());
        }

        @Test
        void getProperties_ShouldThrowException_WhenYamlFrontMatterIsMissing() {
            String markdownContents = """
                    # Hello World
                    > Hahaha
                    """;

            Assertions.assertThrows(IllegalArgumentException.class, () -> parser.getProperties(markdownContents));
        }

        @Test
        void getProperties_ShouldThrowException_WhenTitleIsMissing() {
            String markdownContents = """
                    ---
                    publish: "true"
                    date: 2025-05-21
                    ---
                    # Hello World
                    > Hahaha
                    """;

            Assertions.assertThrows(IllegalArgumentException.class, () -> parser.getProperties(markdownContents));
        }

        @Test
        void getProperties_ShouldDefaultToCurrentDate_WhenDateIsMissing() {
            String markdownContents = """
                    ---
                    publish: "true"
                    title: java.nio.file.Files.walk
                    ---
                    # Hello World
                    > Hahaha
                    """;

            MarkdownProperties markdownProperties = parser.getProperties(markdownContents);

            Assertions.assertTrue(markdownProperties.date().isEqual(LocalDate.now()));
        }

        @Test
        void getProperties_ShouldThrowException_WhenContentsIsNull() {
            Assertions.assertThrows(IllegalArgumentException.class, () -> parser.getProperties(null));
        }
    }


}
