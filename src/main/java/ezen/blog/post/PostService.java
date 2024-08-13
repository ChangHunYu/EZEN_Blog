package ezen.blog.post;

import ezen.blog.infrastructure.minio.MinioService;
import ezen.blog.user.User;
import ezen.blog.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class PostService {

    private final PostRepository postRepository;

    private final MinioService minioService;

    private final UserRepository userRepository;

    @Value("${minio.buckets.postImage}")
    private String POST_IMAGE_BUCKET = "post-images";

    public PostService(PostRepository postRepository, MinioService minioService, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.minioService = minioService;
        this.userRepository = userRepository;
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

    //게시글 저장
    public Post save(CreatePostRequest request) {
        //nickname으로 유저 찾아서 유저가 있으면 같이 저장, 없으면 exception
        User user = userRepository.findByNickname(request.userNickname());
        if (user == null) {
            throw new EntityNotFoundException("User Not Found");
        }

        Post post = Post.builder()
                .title(request.title())
                .content(request.content())
                .user(user)
                .comments(new ArrayList<>())
                .build();

        Post savedPost = postRepository.save(post);

        return Post.builder()
                .id(savedPost.getId())
                .title(savedPost.getTitle())
                .content(savedPost.getContent())
                .user(savedPost.getUser())
                .comments(savedPost.getComments())
                .postTags(savedPost.getPostTags())
                .images(savedPost.getImages())
                .build();
    }

    //게시글 상세조회
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
                post.getUser().getNickname(),
                post.getComments(),
                post.getImages()
        );
    }


    //게시글 목록조회
    public List<PostListResponse> findAll() {
        return postRepository.findAll()
                .stream()
                .map(p-> new PostListResponse(
                        p.getId(),
                        p.getTitle(),
                        p.getUser()
                )).toList();
    }

    //게시글 수정
    @Transactional
    public PostDetailResponse update(Long id, PostDetailResponse request) {

        Post post = postRepository.findById(id).orElse(null);
        if (post == null) {
            throw new EntityNotFoundException("Comment Not Found");
        }

        if (!post.getUser().getNickname().equals(request.userNickname())) {
            throw new IllegalArgumentException("잘못된 접근");
        }

        post.updateContent(request.title(), request.content());

        Post savedPost = postRepository.save(post);
        return PostDetailResponse.builder()
                .postId(savedPost.getId())
                .userNickname(savedPost.getUser().getNickname())
                .content(savedPost.getContent())
                .userNickname(savedPost.getUser().getNickname())
                .comments(savedPost.getComments())
                .images(savedPost.getImages())
                .build();
    }

    //게시글 삭제
    @Transactional
    public void delete(Long id) {
        Post post = postRepository.findByIdAndIsDeletedFalse(id);
        if (post == null) {
            throw new EntityNotFoundException("Post Not Found");
        }

        post.softDelete();
        postRepository.save(post);
    }
}