package ezen.blog.post;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping("/posts") //게시글 작성
    public void create(@RequestBody CreatePostRequest request){
        postService.save(request);
    }

    @GetMapping("/posts/{id}") //게시글 상세조회
    public PostDetailResponse findById(@PathVariable Long id){
        return postService.findById(id);
    }

    @GetMapping("/posts") //게시글 목록조회
    public List<PostListResponse> findAll(){
        return postService.findAll();
    }

    @Transactional
    @PutMapping("/posts/{id}") //게시글 수정
    public ResponseEntity<PostDetailResponse> update(@PathVariable Long id, @RequestBody PostDetailResponse request) {

        PostDetailResponse responseDTO = postService.update(id, request);

        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @DeleteMapping("/posts/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        postService.delete(id);

        return new ResponseEntity<>("Deleted Success",HttpStatus.OK);
    }
}
