package com.jimple.model.category;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * PathSegment 클래스에 대한 단위 테스트
 */
class PathSegmentTest {

    @Test
    void parsePath_null이나_빈문자열인_경우_빈리스트_반환() {
        // given & when & then
        assertTrue(PathSegment.parsePath(null).isEmpty());
        assertTrue(PathSegment.parsePath("").isEmpty());
        assertTrue(PathSegment.parsePath("   ").isEmpty());
    }

    @Test
    void parsePath_단일_세그먼트_정상_파싱() {
        // given
        String path = "개발";

        // when
        List<PathSegment> segments = PathSegment.parsePath(path);

        // then
        assertEquals(1, segments.size());
        
        PathSegment segment = segments.get(0);
        assertEquals("개발", segment.segment());
        assertEquals(0, segment.level());
        assertEquals("개발", segment.fullPath());
    }

    @Test
    void parsePath_여러_세그먼트_정상_파싱() {
        // given
        String path = "개발/Java/스프링";

        // when
        List<PathSegment> segments = PathSegment.parsePath(path);

        // then
        assertEquals(3, segments.size());

        // 첫 번째 세그먼트
        PathSegment first = segments.get(0);
        assertEquals("개발", first.segment());
        assertEquals(0, first.level());
        assertEquals("개발", first.fullPath());

        // 두 번째 세그먼트
        PathSegment second = segments.get(1);
        assertEquals("Java", second.segment());
        assertEquals(1, second.level());
        assertEquals("개발/Java", second.fullPath());

        // 세 번째 세그먼트
        PathSegment third = segments.get(2);
        assertEquals("스프링", third.segment());
        assertEquals(2, third.level());
        assertEquals("개발/Java/스프링", third.fullPath());
    }

    @Test
    void parsePath_앞뒤_슬래시가_있는_경우_정상_처리() {
        // given
        String path = "/개발/Java/";

        // when
        List<PathSegment> segments = PathSegment.parsePath(path);

        // then
        assertEquals(2, segments.size());
        assertEquals("개발", segments.get(0).segment());
        assertEquals("Java", segments.get(1).segment());
    }

    @Test
    void parsePath_공백이_포함된_세그먼트_정상_처리() {
        // given
        String path = " 개발 / Java 프로그래밍 / 스프링 ";

        // when
        List<PathSegment> segments = PathSegment.parsePath(path);

        // then
        assertEquals(3, segments.size());
        assertEquals("개발", segments.get(0).segment());
        assertEquals("Java 프로그래밍", segments.get(1).segment());
        assertEquals("스프링", segments.get(2).segment());
    }

    @Test
    void getParentPath_루트_레벨인_경우_빈문자열_반환() {
        // given
        PathSegment rootSegment = new PathSegment("개발", 0, "개발");

        // when
        String parentPath = rootSegment.getParentPath();

        // then
        assertEquals("", parentPath);
    }

    @Test
    void getParentPath_하위_레벨인_경우_부모경로_반환() {
        // given
        PathSegment childSegment = new PathSegment("스프링", 2, "개발/Java/스프링");

        // when
        String parentPath = childSegment.getParentPath();

        // then
        assertEquals("개발/Java", parentPath);
    }

    @Test
    void getParentPath_슬래시가_없는_경우_빈문자열_반환() {
        // given
        PathSegment segment = new PathSegment("개발", 0, "개발");

        // when
        String parentPath = segment.getParentPath();

        // then
        assertEquals("", parentPath);
    }
}