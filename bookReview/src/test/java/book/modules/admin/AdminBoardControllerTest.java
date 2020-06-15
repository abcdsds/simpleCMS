package book.modules.admin;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;

import book.MockMvcTest;
import book.modules.WithAccount;
import book.modules.account.Account;
import book.modules.account.AccountGender;
import book.modules.account.AccountService;
import book.modules.account.form.AccountForm;
import book.modules.board.Board;
import book.modules.board.BoardRepository;
import book.modules.board.BoardService;
import book.modules.board.form.BoardForm;
import javassist.NotFoundException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Rollback(true)
@MockMvcTest
class AdminBoardControllerTest {

	@Autowired private MockMvc mockMvc;
	@Autowired private BoardRepository boardRepository;
	@Autowired private PasswordEncoder passwordEncoder;
	@Autowired private AccountService accountService;
	@Autowired private BoardService boardService;
	private Board board;
	private Account account;
	
	
	@BeforeEach
	void before() throws NotFoundException {
		
		String loginId = "choiyurim2";
		AccountForm form = new AccountForm();
		form.setLoginId(loginId);
		form.setBirthYear(1990);
		form.setPassword(passwordEncoder.encode("12312300"));
		form.setEmail(loginId+"@naver.com");
		form.setNickname(loginId+"nickname");
		form.setPassword("12312344");
		form.setAccountGender(AccountGender.male);
		
		account = accountService.accountCreate(form);
		
		BoardForm boardform = new BoardForm();
		boardform.setName("테스트게시판1");
		boardform.setPath("ppppppp");
		boardform.setRole("ROLE_USER");
		
		board = boardService.addBoard(account, boardform);
		
	}
	
	@WithAccount("admintest01")
	@DisplayName("어드민 페이지 게시판 관리 메인")
	@Test
	void adminBoardList() throws Exception {
		
		mockMvc.perform(get("/admin/board"))
		.andExpect(status().isOk())
		.andExpect(model().attributeExists("boardList"))
		.andExpect(view().name("admin/board/board"));
	}

	
	@WithAccount("admintest01")
	@DisplayName("어드민 페이지 게시판 관리 - 추가 form")
	@Test
	void adminBoardAddForm() throws Exception {
		
		mockMvc.perform(get("/admin/board/add"))
		.andExpect(status().isOk())
		.andExpect(model().attributeExists("boardForm"))
		.andExpect(view().name("admin/board/add"));
	}
	
//	@WithAccount("admintest01")
//	@DisplayName("어드민 페이지 게시판 관리 - 추가 submit")
//	@Test
//	void adminBoardAddSubmit() throws Exception {
//		
//		mockMvc.perform(
//					post("/admin/board/add")
//					.param("", "")
//					.param("", "")
//					.param("", "")
//				
//				)
//		.andExpect(status().isOk())
//		.andExpect(model().attributeExists("boardForm"))
//		.andExpect(view().name("admin/board/add"));
//	}
	
}
