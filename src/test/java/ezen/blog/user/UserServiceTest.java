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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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
    void setUp() throws IOException {
        user = User.builder()
                .username("testuser")
                .nickname("testUserNickName")
                .email("test@example.com")
                .password("password")
                .build();
        user = userRepository.save(user);
        em.clear();

        Path path = Paths.get("src/test/resources/img/chrome.svg"); // 테스트 리소스 경로
        byte[] content = Files.readAllBytes(path); // 파일 내용을 바이트 배열로 읽기

        file = new MockMultipartFile(
                "file",
                "chrome.svg",
                "image/svg+xml", // MIME 타입
                content // 파일 내용
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
        String imageName = updatedUser.getCurrentProfileImageName();
        System.out.println(imageName);

        assertNotNull(imageName);
        assertTrue(imageName.contains("chrome.svg"));
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
        System.out.println(imageUrl);
        assertTrue(imageUrl.contains("/test-user-profile-images/"));
    }
}