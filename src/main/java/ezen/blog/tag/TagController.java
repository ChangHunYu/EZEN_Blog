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
}
