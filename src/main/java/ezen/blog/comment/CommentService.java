package ezen.blog.comment;

import ezen.blog.post.Post;
import ezen.blog.post.PostRepository;
import ezen.blog.user.User;
import ezen.blog.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.apache.ibatis.javassist.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

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
                .build();
    }
}
