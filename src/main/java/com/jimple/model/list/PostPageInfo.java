package com.jimple.model.list;

public record PostPageInfo(
    int current,
    int last,
    int count,
    boolean hasNext,
    boolean hasPrev
) {
}
