package ezen.blog.post;

import ezen.blog.infrastructure.minio.MinioService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class PostService {

    private final PostRepository postRepository;

    private final MinioService minioService;

    @Value("${minio.buckets.postImage}")
    private String POST_IMAGE_BUCKET = "post-images";

    public PostService(PostRepository postRepository, MinioService minioService) {
        this.postRepository = postRepository;
        this.minioService = minioService;
    }

    public void addImageToPost(Long postId, MultipartFile file) throws Exception {
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new RuntimeException("Post not found"));

        String objectName = minioService.uploadFile(POST_IMAGE_BUCKET, file);
        String imageUrl = String.format("/minio/%s/%s", POST_IMAGE_BUCKET, objectName);

        post.addImage(imageUrl);
        postRepository.save(post);
    }

    public void removeImageFromPost(Long postId, Long imageId) throws Exception {
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new RuntimeException("Post not found"));

        post.removeImage(imageId);
        postRepository.save(post);
    }

    public List<String> getPostImages(Long postId) {
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new RuntimeException("Post not found"));

        return post.getOrderedImageNames();
    }

    public PostDetailResponse findById(Long id) {
        Post post = postRepository.findById(id)
                .orElse(null);

        if (post == null) {
            throw new NoSuchElementException("게시글을 찾을 수 없습니다.");
        }

        return new PostDetailResponse(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getUser(),
                post.getComments(),
                post.getImages()
        );
    }
}