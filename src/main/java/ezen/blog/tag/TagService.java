package ezen.blog.tag;

import ezen.blog.post.Post;
import ezen.blog.post.PostRepository;
import ezen.blog.posttag.PostTag;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class TagService {

    private final TagRepository tagRepository;

    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @Transactional
    public TagResponseDto create(TagRequestDto request) {

        Tag tag = Tag.builder()
                .name(request.name())
                .build();
        Tag savedTag = tagRepository.save(tag);
        return TagResponseDto.builder()
                .id(savedTag.getId())
                .name(savedTag.getName())
                .build();
    }
}
