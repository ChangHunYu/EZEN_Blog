package ezen.blog.comment;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record CommentResponseDTO(
        Long id,
        String userNickname,
        Long postId,
        String content,
        LocalDateTime createdAt
) {
}
