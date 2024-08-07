package ezen.blog.comment;

import org.springframework.stereotype.Service;

@Service
public class CommentService {
    private final CommentRepository commentRepository;

    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public CommentResponseDTO create(CommentRequestDTO request) {
        Comment comment = Comment.builder()
                .content(request.content())
                .build();
        Comment savedComment = commentRepository.save(comment);
        return CommentResponseDTO.builder()
                .id(savedComment.getId())
                .content(savedComment.getContent())
                .build();
    }
}
