package com.jimple.parser.yml;

import com.jimple.model.Properties;

public interface YmlParser {
    Properties getProperties(String contents);
}
