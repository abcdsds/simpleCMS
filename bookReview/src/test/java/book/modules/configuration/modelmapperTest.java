package book.modules.configuration;

import java.util.HashSet;

import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import book.modules.account.Account;
import book.modules.board.Board;
import book.modules.board.form.BoardForm;

@SpringBootTest
public class modelmapperTest {

	@Autowired ModelMapper modelMapper;
	
	@Test
	void test () {
		
		BoardForm form = new BoardForm();
		
		form.setName("가위");
		
		Board board = Board.builder().name("바위")
									 .build();
		
		modelMapper.map(form, board);
		
		//System.out.println(board.getName());
	}
}
