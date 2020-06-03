package book.modules.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import book.modules.board.Board;
import book.modules.board.BoardRepository;
import book.modules.post.PostRepository;
import book.modules.post.form.PostListForm;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
public class RepositoryTest {

	@Autowired
	BoardRepository boardRepository;

	@Autowired
	PostRepository postRepository;
	
	@Test
	void boardTest() {

		Board findByRole = boardRepository.findByRole(new SimpleGrantedAuthority("ROLE_USER"));

		assertThat(findByRole).isNotNull();
		
		Pageable pageable = PageRequest.of(0, 10);
		
		Page<PostListForm> findAllPostByBoardAndDeleted = postRepository.findAllPostByBoardAndDeleted("default", false, pageable);
		
		assertNotNull(findAllPostByBoardAndDeleted);
		
		log.info("{}" , findAllPostByBoardAndDeleted.getSize());
		log.info("{}" , findAllPostByBoardAndDeleted.getSort());
		log.info("{}" , findAllPostByBoardAndDeleted.getNumber());
		log.info("{}" , findAllPostByBoardAndDeleted.getTotalElements());
		
		findAllPostByBoardAndDeleted.forEach(v -> {
			log.info("ID = " + v.getId());
			log.info("TITLE = " + v.getTitle());
		});

	}
}
