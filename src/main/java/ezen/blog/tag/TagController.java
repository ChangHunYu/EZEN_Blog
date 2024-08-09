package ezen.blog.tag;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tags")
public class TagController {

    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @PostMapping
    public ResponseEntity<TagResponseDto> create(@RequestBody TagRequestDto request) {
        TagResponseDto responseDto = tagService.create(request);

        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<TagResponseDto>> findAll() {
        List<TagResponseDto> tagResponseDtos = tagService.findAll();

        return new ResponseEntity<>(tagResponseDtos, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TagResponseDto> findById(@PathVariable Long id) {
        TagResponseDto tagResponseDto = tagService.findById(id);

        return new ResponseEntity<>(tagResponseDto, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TagResponseDto> update(@PathVariable Long id, @RequestBody TagRequestDto request) {
        TagResponseDto tagResponseDto = tagService.update(id, request);

        return new ResponseEntity<>(tagResponseDto, HttpStatus.OK);
    }

    // ResponseEntity에서 반환할 것이 없다고 판단되어, 빈 리스트들 만들었으나 error가 발생하여 ResponseEntity에서 리스트를 제거함
    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable Long id) {
        tagService.delete(id);

        return new ResponseEntity(HttpStatus.OK);
    }
}
