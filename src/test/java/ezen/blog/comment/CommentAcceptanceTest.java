package ezen.blog.comment;

import ezen.blog.post.Post;
import ezen.blog.post.PostRepository;
import ezen.blog.user.User;
import ezen.blog.user.UserRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT)
class CommentAcceptanceTest {
    @LocalServerPort
    int port;

    @Autowired
    UserRepository userRepository;
    @Autowired
    PostRepository postRepository;

    private static User user1;
    private static User user2;
    private static Post post1;
    @BeforeEach
    void setUp() {
        user1 = userRepository.save(
                User.builder()
                        .nickname("홍길동")
                        .username("홍길동")
                        .email("test@test.com")
                        .password("password")
                        .build());
        user2 = userRepository.save(
                User.builder()
                        .nickname("Chrome")
                        .username("크롬")
                        .email("chrome@google.com")
                        .password("password")
                        .build());
        post1 = postRepository.save(
                Post.builder()
                        .title("test")
                        .content("Hello World")
                        .user(user1)
                        .build());

        RestAssured.port = port;
    }

    @Test
    @DisplayName("Comment 저장 테스트")
    void create() {
        //given
        CommentRequestDTO requestDTO = CommentRequestDTO.builder()
                .content("크롬이 쓴 댓글")
                .userNickname(user2.getNickname())
                .postId(post1.getId())
                .build();

        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .body(requestDTO)
                .when()
                .post("/comments")
                .then().log().all()
                .extract();
        CommentResponseDTO responseDTO = response.jsonPath().getObject("", CommentResponseDTO.class);

        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        Assertions.assertThat(responseDTO.userNickname()).isEqualTo(requestDTO.userNickname());
        Assertions.assertThat(responseDTO.postId()).isEqualTo(requestDTO.postId());
        Assertions.assertThat(responseDTO.content()).isEqualTo(requestDTO.content());
    }



}