package ezen.blog.post;

import ezen.blog.user.User;

public record PostListResponse(
        long postId,
        String title,
        User user
) {
}
