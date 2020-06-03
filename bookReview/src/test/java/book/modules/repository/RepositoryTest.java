package book.modules.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import book.modules.board.Board;
import book.modules.board.BoardRepository;

@SpringBootTest
public class RepositoryTest {

	@Autowired
	BoardRepository boardRepository;

	@Test
	void boardTest() {

		Board findByRole = boardRepository.findByRole(new SimpleGrantedAuthority("ROLE_USER"));

		assertThat(findByRole).isNotNull();

	}
}
