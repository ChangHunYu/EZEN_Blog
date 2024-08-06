package ezen.blog.user;

import ezen.blog.comment.Comment;
import ezen.blog.post.Post;
import ezen.blog.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class User extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false, unique = true)
    private String nickname;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<UserProfileImage> profileImages = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Post> posts = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Comment> comments = new ArrayList<>();

    public String getCurrentProfileImageName() {
        return profileImages.stream()
                .filter(UserProfileImage::isActive)
                .findFirst()
                .map(UserProfileImage::getImageName)
                .orElse(null);
    }

    public void addProfileImage(String imageUrl) {
        UserProfileImage newImage = UserProfileImage.builder()
                .user(this)
                .imageName(imageUrl)
                .active(true)
                .build();

        profileImages.forEach(img -> img.deactivate());
        profileImages.add(newImage);
    }
}