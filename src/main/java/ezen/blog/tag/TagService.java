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
    private final PostRepository postRepository;
    private final PostTag postTag;

    public TagService(TagRepository tagRepository, PostRepository postRepository, PostTag postTag) {
        this.tagRepository = tagRepository;
        this.postRepository = postRepository;
        this.postTag = postTag;
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
