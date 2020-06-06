package book.modules.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import book.modules.account.AccountRepository;
import book.modules.admin.form.StatisticsForm;
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
	
	@Autowired
	AccountRepository accountRepository;
	
	@Autowired
	ObjectMapper objectMapper;
	
	@Test
	void test() {
		int[] array2 = IntStream.range(1, 13).toArray();	
		int[] c = Arrays.copyOfRange(array2, 1, 3);
		
		System.out.println(c[0]);
		System.out.println(c[1]);
		System.out.println(c[2]);
		
	}
	
	@Test
	void accountTest() throws JsonProcessingException {
		
		int[] array2 = IntStream.range(1, 13).map(v -> 0).toArray();			
	
		
		List<StatisticsForm> list = accountRepository.findAllAccountMonthlyCount();
		list.forEach(a -> {
			array2[a.getMonth()-1] = a.getCount().intValue();
			log.info("month : {}" , a.getMonth());
			log.info("count : {}" , a.getCount());
		});
		
		for (int a : array2) {
			System.out.println(a);
		}
		
		String writeValueAsString = objectMapper.writeValueAsString(array2);
		log.info("result {}" , writeValueAsString);
		
		//findAllAccountMonthlyCount.stream().iterator()
	}
	
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
