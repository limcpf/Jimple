package com.jimple.parser.template;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SimpleTemplateEngineTest {

    /**
     * Tests for the `processTemplate` method in the `SimpleTemplateEngine` class.
     * This method processes a given template by replacing variables and processing conditional sections
     * using data supplied in a map.
     */

    @Test
    void testProcessTemplate_withVariables() {
        SimpleTemplateEngine engine = new SimpleTemplateEngine();
        String template = "Hello, {{name}}! Welcome to {{place}}.";
        Map<String, Object> data = new HashMap<>();
        data.put("name", "John");
        data.put("place", "New York");

        String result = engine.processTemplate(template, data);

        assertEquals("Hello, John! Welcome to New York.", result);
    }

    @Test
    void testProcessTemplate_withMissingVariables() {
        SimpleTemplateEngine engine = new SimpleTemplateEngine();
        String template = "Hello, {{name}}! Welcome to {{place}}.";
        Map<String, Object> data = new HashMap<>();

        String result = engine.processTemplate(template, data);

        assertEquals("Hello, ! Welcome to .", result);
    }

    @Test
    void testProcessTemplate_withConditionalSectionTrue() {
        SimpleTemplateEngine engine = new SimpleTemplateEngine();
        String template = "{{#loggedIn}}Welcome back, {{name}}!{{/loggedIn}}";
        Map<String, Object> data = new HashMap<>();
        data.put("loggedIn", true);
        data.put("name", "John");

        String result = engine.processTemplate(template, data);

        assertEquals("Welcome back, John!", result);
    }

    @Test
    void testProcessTemplate_withConditionalSectionFalse() {
        SimpleTemplateEngine engine = new SimpleTemplateEngine();
        String template = "{{#loggedIn}}Welcome back, {{name}}!{{/loggedIn}}";
        Map<String, Object> data = new HashMap<>();
        data.put("loggedIn", false);

        String result = engine.processTemplate(template, data);

        assertEquals("", result);
    }

    @Test
    void testProcessTemplate_withEmptyStringCondition() {
        SimpleTemplateEngine engine = new SimpleTemplateEngine();
        String template = "{{#valid}}This section should be shown.{{/valid}}";
        Map<String, Object> data = new HashMap<>();
        data.put("valid", "");

        String result = engine.processTemplate(template, data);

        assertEquals("", result);
    }

    @Test
    void testProcessTemplate_withNumericConditionZero() {
        SimpleTemplateEngine engine = new SimpleTemplateEngine();
        String template = "{{#value}}This section should be shown.{{/value}}";
        Map<String, Object> data = new HashMap<>();
        data.put("value", 0);

        String result = engine.processTemplate(template, data);

        assertEquals("", result);
    }

    @Test
    void testProcessTemplate_withNumericConditionNonZero() {
        SimpleTemplateEngine engine = new SimpleTemplateEngine();
        String template = "{{#value}}This section should be shown.{{/value}}";
        Map<String, Object> data = new HashMap<>();
        data.put("value", 42);

        String result = engine.processTemplate(template, data);

        assertEquals("This section should be shown.", result);
    }

    @Test
    void testProcessTemplate_withUndefinedCondition() {
        SimpleTemplateEngine engine = new SimpleTemplateEngine();
        String template = "{{#undefined}}This section should not be shown.{{/undefined}}";
        Map<String, Object> data = new HashMap<>();

        String result = engine.processTemplate(template, data);

        assertEquals("", result);
    }

    @Test
    void testProcessTemplate_withNestedConditions() {
        SimpleTemplateEngine engine = new SimpleTemplateEngine();
        String template = "{{#parent}}{{#child}}This section should be shown.{{/child}}{{/parent}}";
        Map<String, Object> data = new HashMap<>();
        data.put("parent", true);
        data.put("child", true);

        String result = engine.processTemplate(template, data);

        assertEquals("This section should be shown.", result);
    }

    @Test
    void testProcessTemplate_withComplexTemplate() {
        SimpleTemplateEngine engine = new SimpleTemplateEngine();
        String template = """
                Hello {{name}},
                {{#premium}}Thank you for being a premium user!{{/premium}}
                {{#free}}Consider upgrading to premium for more features.{{/free}}""";
        Map<String, Object> data = new HashMap<>();
        data.put("name", "John");
        data.put("premium", true);
        data.put("free", false);

        String result = engine.processTemplate(template, data);

        assertEquals("Hello John,\nThank you for being a premium user!\n", result);
    }

    @Test
    void testLoadTemplate_withValidPath() throws IOException {
        Files.createDirectories(Path.of("src/test/resources/templates"));
        Files.writeString(Path.of("src/test/resources/templates/example_template.txt"), "This is a test template.");

        SimpleTemplateEngine engine = new SimpleTemplateEngine();
        String resourcePath = "templates/example_template.txt";

        String result = engine.loadTemplate(resourcePath);

        assertEquals("This is a test template.", result);
    }

    @Test
    void testLoadTemplate_withInvalidPath() {
        SimpleTemplateEngine engine = new SimpleTemplateEngine();
        String resourcePath = "nonexistent/path.txt";

        IOException exception = org.junit.jupiter.api.Assertions.assertThrows(
                IOException.class,
                () -> engine.loadTemplate(resourcePath)
        );

        assertEquals(
                "템플릿 파일을 찾을 수 없습니다: " + resourcePath,
                exception.getMessage()
        );
    }

    @Test
    void testLoadTemplate_withEmptyFile() throws IOException {
        Files.createDirectories(Path.of("src/test/resources/templates"));
        Files.writeString(Path.of("src/test/resources/templates/empty_template.txt"), "");

        SimpleTemplateEngine engine = new SimpleTemplateEngine();

        String resourcePath = "templates/empty_template.txt";
        String result = engine.loadTemplate(resourcePath);

        assertEquals("", result);
    }
}