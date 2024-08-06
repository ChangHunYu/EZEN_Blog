package ezen.blog.post;

import ezen.blog.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "post_images")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class PostImage extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @Column(nullable = false)
    private String imageName;

    @Column(nullable = false)
    private Integer orderIndex;

    public void updatePost(Post post) {
        this.post = post;
    }

    public void updateOrderIndex(Integer newOrderIndex) {
        this.orderIndex = newOrderIndex;
    }
}