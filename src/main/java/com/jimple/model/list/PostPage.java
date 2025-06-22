package com.jimple.model.list;

import java.util.List;

public record PostPage(List<PostPageItem> posts, PostPageInfo page) {
}
