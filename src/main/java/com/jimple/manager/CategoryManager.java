package com.jimple.manager;

import com.jimple.model.category.CategoryInfo;
import com.jimple.model.category.PathSegment;
import com.jimple.model.md.MarkdownFile;

import java.util.*;

/**
 * 카테고리 관리를 담당하는 클래스
 */
public class CategoryManager {
    
    /**
     * 마크다운 파일 목록을 카테고리별로 그룹핑하여 CategoryInfo 트리 구조로 반환
     * @param files 마크다운 파일 목록
     * @return 루트 CategoryInfo (전체 카테고리 트리를 포함)
     */
    public CategoryInfo buildCategoryTree(List<MarkdownFile> files) {
        Map<String, CategoryInfo> categoryMap = new HashMap<>();
        
        // 모든 파일을 순회하며 카테고리 구조 생성
        for (MarkdownFile file : files) {
            String path = file.properties().path();
            
            if (path == null || path.trim().isEmpty()) {
                // path가 없는 경우 루트 카테고리에 추가
                addToRootCategory(categoryMap, file);
            } else {
                // path가 있는 경우 해당 카테고리에 추가
                addToCategoryPath(categoryMap, file, path);
            }
        }
        
        // 트리 구조 구성
        return buildHierarchy(categoryMap);
    }
    
    /**
     * 특정 카테고리 경로의 CategoryInfo 반환
     * @param files 마크다운 파일 목록
     * @param categoryPath 카테고리 경로
     * @return 해당 카테고리의 CategoryInfo
     */
    public CategoryInfo getCategoryInfo(List<MarkdownFile> files, String categoryPath) {
        CategoryInfo rootCategory = buildCategoryTree(files);
        
        if (categoryPath == null || categoryPath.trim().isEmpty()) {
            return rootCategory;
        }
        
        return findCategoryByPath(rootCategory, categoryPath);
    }
    
    /**
     * 모든 카테고리 경로 목록 반환
     * @param files 마크다운 파일 목록
     * @return 카테고리 경로 목록
     */
    public List<String> getAllCategoryPaths(List<MarkdownFile> files) {
        Set<String> paths = new HashSet<>();
        
        for (MarkdownFile file : files) {
            String path = file.properties().path();
            if (path != null && !path.trim().isEmpty()) {
                List<PathSegment> segments = PathSegment.parsePath(path);
                for (PathSegment segment : segments) {
                    paths.add(segment.fullPath());
                }
            }
        }
        
        List<String> result = new ArrayList<>(paths);
        result.sort(String::compareTo);
        return result;
    }
    
    private void addToRootCategory(Map<String, CategoryInfo> categoryMap, MarkdownFile file) {
        String rootKey = "";
        CategoryInfo rootCategory = categoryMap.computeIfAbsent(rootKey, 
            k -> new CategoryInfo("", "", 0, new ArrayList<>(), new ArrayList<>()));
        
        rootCategory.posts().add(file);
    }
    
    private void addToCategoryPath(Map<String, CategoryInfo> categoryMap, MarkdownFile file, String path) {
        List<PathSegment> segments = PathSegment.parsePath(path);
        
        // 모든 경로 세그먼트에 대해 카테고리 생성
        for (PathSegment segment : segments) {
            categoryMap.computeIfAbsent(segment.fullPath(), 
                k -> new CategoryInfo(segment.segment(), segment.fullPath(), segment.level(), 
                    new ArrayList<>(), new ArrayList<>()));
        }
        
        // 마지막 카테고리에 파일 추가
        if (!segments.isEmpty()) {
            PathSegment lastSegment = segments.getLast();
            CategoryInfo category = categoryMap.get(lastSegment.fullPath());
            if (category != null) {
                category.posts().add(file);
            }
        }
    }
    
    private CategoryInfo buildHierarchy(Map<String, CategoryInfo> categoryMap) {
        // 루트 카테고리가 없다면 생성하여 맵에 추가
        CategoryInfo rootCategory = categoryMap.computeIfAbsent("", 
            k -> new CategoryInfo("", "", 0, new ArrayList<>(), new ArrayList<>()));
        
        // 루트가 아닌 모든 카테고리를 적절한 부모에 연결
        for (CategoryInfo category : categoryMap.values()) {
            if (!category.fullPath().isEmpty()) {
                String parentPath = getParentPath(category.fullPath());
                CategoryInfo parent = categoryMap.get(parentPath);
                
                if (parent != null) {
                    parent.subCategories().add(category);
                }
            }
        }
        
        // 각 카테고리의 하위 카테고리를 이름순으로 정렬
        sortSubCategories(rootCategory);
        
        return rootCategory;
    }
    
    private String getParentPath(String fullPath) {
        int lastSlashIndex = fullPath.lastIndexOf("/");
        return lastSlashIndex == -1 ? "" : fullPath.substring(0, lastSlashIndex);
    }
    
    private void sortSubCategories(CategoryInfo category) {
        category.subCategories().sort(Comparator.comparing(CategoryInfo::name));
        category.posts().sort(Comparator.comparing(file -> file.properties().title()));
        
        for (CategoryInfo subCategory : category.subCategories()) {
            sortSubCategories(subCategory);
        }
    }
    
    private CategoryInfo findCategoryByPath(CategoryInfo root, String targetPath) {
        if (targetPath.equals(root.fullPath())) {
            return root;
        }
        
        for (CategoryInfo subCategory : root.subCategories()) {
            CategoryInfo found = findCategoryByPath(subCategory, targetPath);
            if (found != null) {
                return found;
            }
        }
        
        return null;
    }
}