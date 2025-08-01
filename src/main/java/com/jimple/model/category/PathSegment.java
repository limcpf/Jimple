package com.jimple.model.category;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 경로 세그먼트를 나타내는 레코드 클래스
 * @param segment 세그먼트 이름
 * @param level 세그먼트 레벨 (0부터 시작)
 * @param fullPath 현재까지의 전체 경로
 */
public record PathSegment(String segment, int level, String fullPath) {
    
    /**
     * 경로 문자열을 PathSegment 리스트로 파싱
     * @param path 경로 문자열 ("/"로 구분)
     * @return PathSegment 리스트
     */
    public static List<PathSegment> parsePath(String path) {
        if (path == null || path.trim().isEmpty()) {
            return List.of();
        }
        
        String[] segments = path.split("/");
        
        return Arrays.stream(segments)
                .map(String::trim)
                .filter(trim -> !trim.isEmpty())
                .collect(ArrayList::new, (list, segment) -> {
                    int level = list.size();
                    String fullPath = level == 0 ? segment : 
                        list.getLast().fullPath() + "/" + segment;
                    list.add(new PathSegment(segment, level, fullPath));
                }, ArrayList::addAll);
    }
    
    /**
     * 부모 경로 반환
     * @return 부모 경로 (루트인 경우 빈 문자열)
     */
    public String getParentPath() {
        if (level == 0) {
            return "";
        }
        
        int lastSlashIndex = fullPath.lastIndexOf("/");
        return lastSlashIndex == -1 ? "" : fullPath.substring(0, lastSlashIndex);
    }
}