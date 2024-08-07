package ezen.blog.post;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping("/posts") //게시글 작성

    @GetMapping("/posts/{id}") //게시글 상세조회
    public PostDetailResponse findById(@PathVariable Long id){
        return postService.findById(id);
    }
}
