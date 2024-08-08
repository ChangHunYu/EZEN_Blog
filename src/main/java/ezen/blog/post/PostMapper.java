package ezen.blog.post;

import java.util.List;

public interface PostMapper {
    List<PostListResponse> findAll();
}
