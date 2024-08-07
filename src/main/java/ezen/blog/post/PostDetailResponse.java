package ezen.blog.post;

import ezen.blog.comment.Comment;
import ezen.blog.user.User;

import java.util.List;

public record PostDetailResponse(
        long postId,
        String title,
        String content,
        User user,
        List<Comment> comments,
        List<PostImage> images
) {
}
