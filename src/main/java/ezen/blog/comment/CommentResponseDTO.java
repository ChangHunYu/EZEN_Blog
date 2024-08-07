package ezen.blog.comment;

import lombok.Builder;

@Builder
public record CommentResponseDTO(
        Long id,
        String content
) {
}
