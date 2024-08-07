package ezen.blog;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.time.LocalDateTime;

//@SpringBootTest
class BlogApplicationTests {

	@Test
	void contextLoads() {
		System.out.println(LocalDateTime.now());
	}

}
