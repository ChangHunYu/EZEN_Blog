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

    @Transactional
    public List<TagResponseDto> findAll() {
        List<Tag> tags = tagRepository.findAll();
        return tags.stream()
                .map(t -> TagResponseDto.builder()
                        .id(t.getId())
                        .name(t.getName())
                        .build())
                .toList();
    }

    @Transactional
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

    // 기존에 태그를 작성했던 유저가 아니라면 태그를 수정할 수 없게 막을 수 있는 조건문이 필요함 - 작성 예정
    @Transactional
    public TagResponseDto update(Long id, TagRequestDto request) {
        Tag tag = tagRepository.findById(id)
                .orElse(null);
        if (tag == null) {
            throw new EntityNotFoundException("Tag Not Found");
        }

        tag.updateName(request.name());

        Tag savedTag = tagRepository.save(tag);

        return TagResponseDto.builder()
                .id(savedTag.getId())
                .name(savedTag.getName())
                .build();
    }

    // findById 대신 tagRepository에 'findByIdAndIsDeletedFalse' 라는 Jpa Query를 추가하여 사용할지에 대한 검토 필요
    // softDelete vs hardDelete 에 대한 검토 필요
    // tag.delete(); -> 에러 발생 - 추후 조치 필요
    @Transactional
    public void delete(Long id) {
        Tag tag = tagRepository.findByIdAndIsDeletedFalse(id);
        if (tag == null) {
            throw new EntityNotFoundException("Tag Not Found");
        }

//        tag.delete();
        tagRepository.save(tag);
    }
}
