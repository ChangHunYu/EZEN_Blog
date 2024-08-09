package ezen.blog.tag;

import lombok.Builder;

@Builder
public record TagRequestDto(
        String name
) {
}
