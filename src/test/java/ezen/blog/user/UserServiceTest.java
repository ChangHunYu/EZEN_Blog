package ezen.blog.user;

import ezen.blog.infrastructure.minio.MinioService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MinioService minioService;

    @PersistenceContext
    private EntityManager em;


    private static User user;
    private static MultipartFile file;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .username("testuser")
                .email("test@example.com")
                .password("password")
                .build();
        user = userRepository.save(user);
        em.clear();

        file = new MockMultipartFile(
                "file",
                "test-image.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );
    }

    @Test
    void updateProfileImage() {
        // when
        try {
            userService.updateProfileImage(user.getId(), file);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // then
        User updatedUser = userRepository.findById(user.getId()).orElseThrow();
        String imageUrl = updatedUser.getCurrentProfileImageUrl();

        assertNotNull(imageUrl);
        assertTrue(imageUrl.startsWith("/minio/test-user-profile-images/"));
    }

    @Test
    void getCurrentProfileImageUrl() {
        // Given
        try {
            userService.updateProfileImage(user.getId(), file);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // When
        String imageUrl = userService.getCurrentProfileImageUrl(user.getId());

        // Then
        assertNotNull(imageUrl);
        assertTrue(imageUrl.startsWith("/minio/test-user-profile-images/"));
    }
}