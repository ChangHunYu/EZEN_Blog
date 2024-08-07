package ezen.blog.comment;

import lombok.Builder;

@Builder
public record CommentRequestDTO(
        String content
) {
}
