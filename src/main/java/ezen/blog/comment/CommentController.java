package ezen.blog.comment;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comments")
public class CommentController {
    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    public ResponseEntity<CommentResponseDTO> create(@RequestBody CommentRequestDTO request) {
        CommentResponseDTO responseDTO = commentService.create(request);

        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<CommentResponseDTO>> findAll() {
        List<CommentResponseDTO> responseDTOs = commentService.findAll();

        return new ResponseEntity<>(responseDTOs, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommentResponseDTO> findById(@PathVariable Long id) {
        CommentResponseDTO responseDTO = commentService.findById(id);

        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommentResponseDTO> update(@PathVariable Long id, @RequestBody CommentRequestDTO request) {
        CommentResponseDTO responseDTO = commentService.update(id, request);

        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        commentService.delete(id);

        return new ResponseEntity<>("Deleted Success",HttpStatus.OK);
    }
}
