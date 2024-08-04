package ezen.blog.user;

import ezen.blog.infrastructure.minio.MinioService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UserService {

    private final UserRepository userRepository;

    private final MinioService minioService;

    @Value("${minio.buckets.userProfile}")
    private String PROFILE_IMAGE_BUCKET;

    public UserService(UserRepository userRepository, MinioService minioService) {
        this.userRepository = userRepository;
        this.minioService = minioService;
    }

    @Transactional
    public void updateProfileImage(Long userId, MultipartFile file) throws Exception {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        // 새 이미지 업로드
        String objectName = minioService.uploadFile(PROFILE_IMAGE_BUCKET, file);
        String imageUrl = String.format("/minio/%s/%s", PROFILE_IMAGE_BUCKET, objectName);

        // 사용자 정보 업데이트
        user.addProfileImage(imageUrl);
        userRepository.save(user);
    }

    public String getCurrentProfileImageUrl(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getCurrentProfileImageUrl();
    }
}