package ezen.blog.tag;

import lombok.Builder;

@Builder
public record TagResponseDto(
        Long id,
        String name
) {
}
