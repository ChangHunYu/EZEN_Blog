package ezen.blog.post;

import ezen.blog.comment.Comment;
import ezen.blog.posttag.PostTag;
import ezen.blog.tag.Tag;
import ezen.blog.common.BaseTimeEntity;
import ezen.blog.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;
import java.util.stream.Collectors;

@Entity
@Table(name = "posts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Post extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Builder.Default
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostTag> postTags = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostImage> images = new ArrayList<>();

    public void updateContent(String newTitle, String newContent) {
        this.title = newTitle;
        this.content = newContent;
    }

    public void addComment(Comment comment) {
        this.comments.add(comment);
        comment.updatePost(this);
    }

    public void addTag(Tag tag) {
        PostTag postTag = PostTag.builder()
                .post(this)
                .tag(tag)
                .build();
        this.postTags.add(postTag);
        tag.getPostTags().add(postTag);
    }

    public void removeTag(Tag tag) {
        PostTag postTagToRemove = this.postTags.stream()
            .filter(pt -> pt.getTag().equals(tag))
            .findFirst()
            .orElse(null);
        if (postTagToRemove != null) {
            this.postTags.remove(postTagToRemove);
            tag.getPostTags().remove(postTagToRemove);
            postTagToRemove.updatePost(null);
            postTagToRemove.updateTag(null);
        }
    }

    public void addImage(String imageUrl) {
        PostImage postImage = PostImage.builder()
                .post(this)
                .imageName(imageUrl)
                .orderIndex(this.images.size())
                .build();
        this.images.add(postImage);
    }

    public void removeImage(Long imageId) {
        this.images.removeIf(image -> image.getId().equals(imageId));
        reorderImages();
    }

    public void reorderImages() {
        for (int i = 0; i < this.images.size(); i++) {
            this.images.get(i).updateOrderIndex(i);
        }
    }

    public List<String> getOrderedImageNames() {
        return this.images.stream()
                .sorted(Comparator.comparing(PostImage::getOrderIndex))
                .map(PostImage::getImageName)
                .collect(Collectors.toList());
    }
}