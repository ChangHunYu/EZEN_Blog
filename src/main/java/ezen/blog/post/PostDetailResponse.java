package ezen.blog.post;

import ezen.blog.comment.Comment;
import ezen.blog.user.User;
import lombok.Builder;

import java.util.List;

@Builder
public record PostDetailResponse(
        long postId,
        String title,
        String content,
        String userNickname,
        List<Comment> comments,
        List<PostImage> images
) {
}
