package ezen.blog.tag;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public List<TagResponseDto> findAll() {
        List<Tag> tags = tagRepository.findAll();
        return tags.stream()
                .map(t -> TagResponseDto.builder()
                        .id(t.getId())
                        .name(t.getName())
                        .build())
                .toList();
}

    public TagResponseDto findById(Long id) {
        Tag tag = tagRepository.findById(id)
                .orElse(null);
        if (tag == null) {
            throw new EntityNotFoundException("Tag Not Found");
        }
        return TagResponseDto.builder()
                .id(tag.getId())
                .name(tag.getName())
                .build();
    }
}
