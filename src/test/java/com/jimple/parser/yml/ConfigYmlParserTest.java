package com.jimple.parser.yml;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.jimple.model.Properties;
import com.jimple.model.config.BlogConfigDefaults;
import com.jimple.model.config.BlogProperties;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ConfigYmlParserTest {

    /**
     * Tests for the {@link ConfigYmlParser#getProperties(String)} method.
     * This method is responsible for parsing YAML content into a {@link Properties} object.
     * If the content is null, empty, or invalid, it returns a new instance of {@link BlogProperties}.
     */

    @Test
    void testEmptyContentReturnsDefaultProperties() {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        ConfigYmlParser parser = new ConfigYmlParser(mapper);
        BlogProperties properties = (BlogProperties) parser.getProperties("");

        // 기본값 검증
        assertEquals(BlogConfigDefaults.DEFAULT_TITLE, properties.title());
        assertEquals(BlogConfigDefaults.DEFAULT_DESCRIPTION, properties.description());
    }

    @Test
    void testValidContentParsesCorrectly() {
        String yamlContent = """
              title: "Test Blog"
              description: "Test Description"
              layout:
                menuPosition: "left"
                colors:
                  primary: "#ff0000"
              profile:
                name: "Test User"
                social:
                  - platform: "github"
                    url: "https://github.com/test"
              customization:
                showDate: false
                footerText: "Custom Footer"
              """;

        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        ConfigYmlParser parser = new ConfigYmlParser(mapper);
        BlogProperties properties = (BlogProperties) parser.getProperties(yamlContent);

        // 파싱된 값 검증
        assertEquals("Test Blog", properties.title());
        assertEquals("Test Description", properties.description());
        assertEquals("left", properties.layout().menuPosition());
        assertEquals("#ff0000", properties.layout().colors().primary());
        assertEquals("Test User", properties.profile().name());
        assertEquals(1, properties.profile().social().size());
        assertEquals("github", properties.profile().social().getFirst().platform());
        assertEquals("https://github.com/test", properties.profile().social().getFirst().url());
        assertFalse(properties.customization().showDate());
        assertEquals("Custom Footer", properties.customization().footerText());
    }

    @Test
    void testInvalidYamlReturnsDefaultProperties() {
        String invalidYaml = """
                blog:
                  title: "Invalid YAML
                  description: Missing quote
                """;

        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        ConfigYmlParser parser = new ConfigYmlParser(mapper);
        BlogProperties properties = (BlogProperties) parser.getProperties(invalidYaml);

        // 오류 시 기본값 사용 검증
        assertEquals(BlogConfigDefaults.DEFAULT_TITLE, properties.title());
    }


    @Test
    void testGetPropertiesWithValidYAMLContent() throws Exception {
        // Arrange
        String validYaml = "key: value";
        ObjectMapper mockObjectMapper = mock(ObjectMapper.class);
        BlogProperties expectedProperties = new BlogProperties();
        when(mockObjectMapper.readValue(validYaml, BlogProperties.class)).thenReturn(expectedProperties);

        ConfigYmlParser parser = new ConfigYmlParser(mockObjectMapper);

        // Act
        Properties result = parser.getProperties(validYaml);

        // Assert
        assertNotNull(result);
        assertEquals(expectedProperties, result);
        verify(mockObjectMapper).readValue(validYaml, BlogProperties.class);
    }

    @Test
    void testGetPropertiesWithNullContent() throws JsonProcessingException {
        // Arrange
        ObjectMapper mockObjectMapper = mock(ObjectMapper.class);
        ConfigYmlParser parser = new ConfigYmlParser(mockObjectMapper);

        // Act
        Properties result = parser.getProperties(null);

        // Assert
        assertNotNull(result);
        assertInstanceOf(BlogProperties.class, result);
        verify(mockObjectMapper, never()).readValue(anyString(), eq(BlogProperties.class));
    }

    @Test
    void testGetPropertiesWithEmptyContent() throws JsonProcessingException {
        // Arrange
        ObjectMapper mockObjectMapper = mock(ObjectMapper.class);
        ConfigYmlParser parser = new ConfigYmlParser(mockObjectMapper);

        // Act
        Properties result = parser.getProperties("");

        // Assert
        assertNotNull(result);
        assertInstanceOf(BlogProperties.class, result);
        verify(mockObjectMapper, never()).readValue(anyString(), eq(BlogProperties.class));
    }

    @Test
    void testGetPropertiesWithInvalidYAMLContent() throws Exception {
        // Arrange
        String invalidYaml = "invalid: : yaml";
        ObjectMapper mockObjectMapper = mock(ObjectMapper.class);
        when(mockObjectMapper.readValue(invalidYaml, BlogProperties.class))
                .thenThrow(new JsonProcessingException("Invalid YAML.") {});
        ConfigYmlParser parser = new ConfigYmlParser(mockObjectMapper);

        // Act
        Properties result = parser.getProperties(invalidYaml);

        // Assert
        assertNotNull(result);
        assertInstanceOf(BlogProperties.class, result);
        verify(mockObjectMapper).readValue(invalidYaml, BlogProperties.class);
    }

    @Test
    void testGetPropertiesWhenMapperReturnsNull() throws Exception {
        // Arrange
        String validYaml = "key: value";
        ObjectMapper mockObjectMapper = mock(ObjectMapper.class);
        when(mockObjectMapper.readValue(validYaml, BlogProperties.class)).thenReturn(null);
        ConfigYmlParser parser = new ConfigYmlParser(mockObjectMapper);

        // Act
        Properties result = parser.getProperties(validYaml);

        // Assert
        assertNotNull(result);
        assertInstanceOf(BlogProperties.class, result);
        verify(mockObjectMapper).readValue(validYaml, BlogProperties.class);
    }
}