package ezen.blog.comment;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Comment findByIdAndIsDeletedFalse(Long id);
}
