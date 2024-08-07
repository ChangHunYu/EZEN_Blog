package ezen.blog.comment;

import ezen.blog.post.Post;
import ezen.blog.post.PostRepository;
import ezen.blog.user.User;
import ezen.blog.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    public CommentService(CommentRepository commentRepository, UserRepository userRepository, PostRepository postRepository) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
    }

    @Transactional
    public CommentResponseDTO create(CommentRequestDTO request) {
        User user = userRepository.findByNickname(request.userNickname());
        Post post = postRepository.findById(request.postId()).orElse(null);

        if (user == null) {
            throw new EntityNotFoundException("User Not Found");
        }

        if (post == null) {
            throw new IllegalArgumentException("Post Not Found");
        }

        Comment comment = Comment.builder()
                .user(user)
                .post(post)
                .content(request.content())
                .build();
        Comment savedComment = commentRepository.save(comment);
        return CommentResponseDTO.builder()
                .id(savedComment.getId())
                .postId(savedComment.getPost().getId())
                .userNickname(savedComment.getUser().getNickname())
                .content(savedComment.getContent())
                .createdAt(savedComment.getCreatedAt())
                .build();
    }

    public List<CommentResponseDTO> findAll() {
        List<Comment> comments = commentRepository.findAll();
        return comments.stream().map(
                c->CommentResponseDTO.builder()
                        .id(c.getId())
                        .postId(c.getPost().getId())
                        .userNickname(c.getUser().getNickname())
                        .content(c.getContent())
                        .createdAt(c.getCreatedAt())
                        .build()).toList();
    }

    public CommentResponseDTO findById(Long id) {
        Comment comment = commentRepository.findById(id).orElse(null);
        if (comment == null) {
            throw new EntityNotFoundException("Comment Not Found");
        }
        return CommentResponseDTO.builder()
                .id(comment.getId())
                .postId(comment.getPost().getId())
                .userNickname(comment.getUser().getNickname())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .build();
    }

    @Transactional
    public CommentResponseDTO update(Long id, CommentRequestDTO request) {
        Comment comment = commentRepository.findById(id).orElse(null);
        if (comment == null) {
            throw new EntityNotFoundException("Comment Not Found");
        }

        if (!comment.getUser().getNickname().equals(request.userNickname())) {
            throw new IllegalArgumentException("잘못된 접근");
        }

        comment.updateContent(request.content());

        Comment savedComment = commentRepository.save(comment);

        return CommentResponseDTO.builder()
                .id(savedComment.getId())
                .postId(savedComment.getPost().getId())
                .userNickname(savedComment.getUser().getNickname())
                .content(savedComment.getContent())
                .createdAt(savedComment.getCreatedAt())
                .build();
    }

    @Transactional
    public void delete(Long id) {
        Comment comment = commentRepository.findByIdAndIsDeletedFalse(id);
        if (comment == null) {
            throw new EntityNotFoundException("Comment Not Found");
        }

        comment.softDelete();
        commentRepository.save(comment);
    }
}
