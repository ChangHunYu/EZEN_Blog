package ezen.blog.comment;

import ezen.blog.post.Post;
import ezen.blog.post.PostRepository;
import ezen.blog.user.User;
import ezen.blog.user.UserRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
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
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@ActiveProfiles("test")
@Sql("/truncate.sql")
@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT)
class CommentAcceptanceTest {
    @LocalServerPort
    int port;

    @Autowired
    UserRepository userRepository;
    @Autowired
    PostRepository postRepository;
    @Autowired
    CommentService commentService;
    @PersistenceContext
    EntityManager em;
    @Autowired
    CommentRepository commentRepository;

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

        //when
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .body(requestDTO)
                .when()
                .post("/comments")
                .then().log().all()
                .extract();
        CommentResponseDTO responseDTO = response.jsonPath().getObject("", CommentResponseDTO.class);

        //then
        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        Assertions.assertThat(responseDTO.userNickname()).isEqualTo(requestDTO.userNickname());
        Assertions.assertThat(responseDTO.postId()).isEqualTo(requestDTO.postId());
        Assertions.assertThat(responseDTO.content()).isEqualTo(requestDTO.content());
        Assertions.assertThat(responseDTO.createdAt()).isBefore(LocalDateTime.now());
    }

    @Test
    @DisplayName("Comment 목록 조회 테스트")
    void findAll() {
        //given
        CommentRequestDTO requestDTO1 = CommentRequestDTO.builder()
                .content("크롬이 쓴 댓글")
                .userNickname(user2.getNickname())
                .postId(post1.getId())
                .build();
        CommentRequestDTO requestDTO2 = CommentRequestDTO.builder()
                .content("홍길동이 쓴 댓글")
                .userNickname(user1.getNickname())
                .postId(post1.getId())
                .build();
        commentService.create(requestDTO1);
        commentService.create(requestDTO2);
        em.clear();



        //when
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .when()
                .get("/comments")
                .then().log().all()
                .extract();
        List<CommentResponseDTO> responseDTOs = response.jsonPath().getList("", CommentResponseDTO.class);

        //then
        Assertions.assertThat(responseDTOs).hasSize(2);
        Assertions.assertThat(responseDTOs.get(0).userNickname()).isEqualTo(requestDTO1.userNickname());
        Assertions.assertThat(responseDTOs.get(0).postId()).isEqualTo(requestDTO1.postId());
        Assertions.assertThat(responseDTOs.get(0).content()).isEqualTo(requestDTO1.content());
        Assertions.assertThat(responseDTOs.get(0).createdAt()).isBefore(LocalDateTime.now());
    }

    @Test
    @DisplayName("Comment id로 조회 테스트")
    void findById() {
        //given
        CommentRequestDTO requestDTO1 = CommentRequestDTO.builder()
                .content("크롬이 쓴 댓글")
                .userNickname(user2.getNickname())
                .postId(post1.getId())
                .build();
        CommentResponseDTO createDTO = commentService.create(requestDTO1);
        em.clear();



        //when
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .when()
                .get("/comments/"+createDTO.id())
                .then().log().all()
                .extract();
        CommentResponseDTO responseDTO = response.jsonPath().getObject("", CommentResponseDTO.class);

        //then
        Assertions.assertThat(responseDTO.userNickname()).isEqualTo(requestDTO1.userNickname());
        Assertions.assertThat(responseDTO.postId()).isEqualTo(requestDTO1.postId());
        Assertions.assertThat(responseDTO.content()).isEqualTo(requestDTO1.content());
        Assertions.assertThat(responseDTO.createdAt()).isBefore(LocalDateTime.now());
    }

    @Test
    @DisplayName("Comment update 테스트")
    void update() {
        //given
        CommentRequestDTO requestDTO1 = CommentRequestDTO.builder()
                .content("크롬이 쓴 댓글")
                .userNickname(user2.getNickname())
                .postId(post1.getId())
                .build();
        CommentResponseDTO createDTO = commentService.create(requestDTO1);
        em.clear();



        //when
        CommentRequestDTO updateRequest1 = CommentRequestDTO.builder()
                .content("크롬이 쓴 댓글 수정")
                .userNickname(user2.getNickname())
                .postId(post1.getId())
                .build();

        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .body(updateRequest1)
                .when()
                .put("/comments/"+createDTO.id())
                .then().log().all()
                .extract();
        CommentResponseDTO responseDTO = response.jsonPath().getObject("", CommentResponseDTO.class);

        //then
        Assertions.assertThat(responseDTO.userNickname()).isEqualTo(updateRequest1.userNickname());
        Assertions.assertThat(responseDTO.postId()).isEqualTo(updateRequest1.postId());
        Assertions.assertThat(responseDTO.content()).isEqualTo(updateRequest1.content());
    }


    @Test
    @DisplayName("Comment delete 테스트")
    void delete() {
        //given
        CommentRequestDTO requestDTO1 = CommentRequestDTO.builder()
                .content("크롬이 쓴 댓글")
                .userNickname(user2.getNickname())
                .postId(post1.getId())
                .build();
        CommentResponseDTO createDTO = commentService.create(requestDTO1);
        em.clear();

        //when
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .when()
                .delete("/comments/"+createDTO.id())
                .then().log().all()
                .extract();

        //then
        Comment comment = commentRepository.findById(createDTO.id()).orElse(null);
        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        Assertions.assertThat(comment.isDeleted()).isTrue();
    }
}