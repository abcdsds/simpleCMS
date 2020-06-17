package book.modules.admin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
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
import org.springframework.test.web.servlet.MvcResult;

import book.MockMvcTest;
import book.modules.WithAccount;
import book.modules.account.Account;
import book.modules.account.AccountGender;
import book.modules.account.AccountRepository;
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
	@Autowired private AccountRepository accountRepository;
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
	
	@WithAccount("admintest01")
	@DisplayName("어드민 페이지 게시판 관리 - 추가 submit - 성공")
	@Test
	void adminBoardAddSubmitSuccess() throws Exception {
		
		mockMvc.perform(
					post("/admin/board/add")
					.param("name", "wioie")
					.param("path", "aaa")
					.param("role", "ROLE_USER")
					.with(csrf())
				)
		.andExpect(status().is3xxRedirection());
		
		Board lastBoard = boardRepository.findTop1ByOrderByIdDesc();
		
		assertThat(lastBoard.getName()).isEqualTo("wioie");
		assertThat(lastBoard.getPath()).isEqualTo("aaa");
		assertThat(lastBoard.getRole().toString()).isEqualTo("ROLE_USER");
	}
	
	@WithAccount("admintest01")
	@DisplayName("어드민 페이지 게시판 관리 - 추가 submit - 실패")
	@Test
	void adminBoardAddSubmitFaillure() throws Exception {
		
		mockMvc.perform(
					post("/admin/board/add")
					.param("name", "wioie")
					.param("path", "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa")
					.param("role", "ROLE_USER")
					.with(csrf())
				)
		.andExpect(status().isOk())
		.andExpect(model().attributeExists("account"))
		.andExpect(view().name("admin/board/add"));
		
		Board lastBoard = boardRepository.findTop1ByOrderByIdDesc();
		
		if (lastBoard != null) {
			assertThat(lastBoard.getName()).isNotEqualTo("wioie");
			assertThat(lastBoard.getPath()).isNotEqualTo("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
		} else {
			assertNull(lastBoard);
		}
	}
	
	@WithAccount("admintest01")
	@DisplayName("어드민 페이지 게시판 관리 - 수정 form")
	@Test
	void adminBoardUpdateForm() throws Exception {
		
		mockMvc.perform(get("/admin/board/update/"+board.getId()))
		.andExpect(status().isOk())
		.andExpect(model().attributeExists("boardForm"))
		.andExpect(model().attributeExists("board"))
		.andExpect(model().attributeExists("accountListWithBoard"))
		.andExpect(model().attributeExists("allAccountList"))
		.andExpect(view().name("admin/board/update"));
	}
	
	@WithAccount("admintest01")
	@DisplayName("어드민 페이지 게시판 관리 - 수정 submit - 성공")
	@Test
	void adminBoardUpdateSubmitSuccess() throws Exception {
		
		mockMvc.perform(
					post("/admin/board/update/")
					.param("id", board.getId().toString())
					.param("name", "wioie")
					.param("path", "aaa")
					.param("role", "ROLE_ADMIN")
					.with(csrf())
				)
		.andExpect(status().is3xxRedirection())
		.andExpect(flash().attribute("message", "수정에 성공했습니다."));
		
		
		assertThat(board.getName()).isEqualTo("wioie");
		assertThat(board.getPath()).isEqualTo("aaa");
		assertThat(board.getRole().toString()).isEqualTo("ROLE_ADMIN");
	}
	
	@WithAccount("admintest01")
	@DisplayName("어드민 페이지 게시판 관리 - 수정 submit - 실패")
	@Test
	void adminBoardUpdateSubmitFaillure() throws Exception {
		
		mockMvc.perform(
					post("/admin/board/update/")
					.param("id", board.getId().toString())
					.param("name", "wioie")
					.param("path", "aaaㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁ")
					.param("role", "ROLE_ADMIN")
					.with(csrf())
				)
		.andExpect(status().is3xxRedirection())
		.andExpect(flash().attribute("message", "수정에 실패했습니다."));
		
		
		assertThat(board.getName()).isNotEqualTo("wioie");
		assertThat(board.getPath()).isNotEqualTo("aaaㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁ");
		assertThat(board.getRole().toString()).isEqualTo("ROLE_USER");
	}
	
	
	@WithAccount("admintest01")
	@DisplayName("어드민 페이지 게시판 관리 - 매니저 추가 submit - 성공")
	@Test
	void adminBoardAddManagerSubmitSuccess() throws Exception {
		
		Account findByLoginId = accountRepository.findByLoginId("admintest01");
		
		MvcResult andReturn = mockMvc.perform(
					post("/admin/board/manager/add")
					.param("boardId", board.getId().toString())
					.param("accountId", findByLoginId.getId().toString())
					.with(csrf())
				)
		.andExpect(status().isOk())
		.andReturn();
		
		assertThat(andReturn.getResponse().getContentAsString().contains("성공적으로 추가했습니다.")).isTrue();
	}
	
	@WithAccount("admintest01")
	@DisplayName("어드민 페이지 게시판 관리 - 매니저 추가 submit - 실패 (이미 추가된 관리자)")
	@Test
	void adminBoardAddManagerSubmitFailureAlreadyAdded() throws Exception {
		
		MvcResult andReturn = mockMvc.perform(
					post("/admin/board/manager/add")
					.param("boardId", board.getId().toString())
					.param("accountId", account.getId().toString())
					.with(csrf())
				)
		.andExpect(status().isOk())
		.andReturn();
		
		assertThat(andReturn.getResponse().getContentAsString().contains("이미 등록된 관리자입니다.")).isTrue();
	}
	
	
	@WithAccount("admintest01")
	@DisplayName("어드민 페이지 게시판 관리 - 매니저 삭제 submit - 성공")
	@Test
	void adminBoardDeleteManagerSubmitSuccess() throws Exception {
		
		MvcResult andReturn = mockMvc.perform(
					post("/admin/board/manager/delete")
					.param("boardId", board.getId().toString())
					.param("accountId", account.getId().toString())
					.with(csrf())
				)
		.andExpect(status().isOk())
		.andReturn();
		
		assertThat(andReturn.getResponse().getContentAsString().contains("성공적으로 해제했습니다.")).isTrue();
	}
	
	@WithAccount("admintest01")
	@DisplayName("어드민 페이지 게시판 관리 - 매니저 추가 submit - 실패 (관리자 목록에 없는사람을 삭제하려고함)")
	@Test
	void adminBoardDeleteManagerSubmitFailureAlreadyAdded() throws Exception {
		
		Account findByLoginId = accountRepository.findByLoginId("admintest01");
		
		MvcResult andReturn = mockMvc.perform(
					post("/admin/board/manager/delete")
					.param("boardId", board.getId().toString())
					.param("accountId", findByLoginId.getId().toString())
					.with(csrf())
				)
		.andExpect(status().isOk())
		.andReturn();
		
		assertThat(andReturn.getResponse().getContentAsString().contains("관리자로 등록되지 않은 회원입니다.")).isTrue();
	}
	
	@WithAccount("admintest01")
	@DisplayName("어드민 페이지 게시판 관리 - 게시판 삭제 submit - 성공")
	@Test
	void adminBoardDeleteSubmitSuccess() throws Exception {
		
		MvcResult andReturn = mockMvc.perform(
					post("/admin/board/delete")
					.param("boardId", board.getId().toString())
					.with(csrf())
				)
		.andExpect(status().isOk())
		.andReturn();
		
		assertThat(andReturn.getResponse().getContentAsString().contains("성공적으로 삭제되었습니다.")).isTrue();
	}
	
	@WithAccount("admintest01")
	@DisplayName("어드민 페이지 게시판 관리 - 게시판 삭제 submit - 실패 (게시판이 없음)")
	@Test
	void adminBoardDeleteSubmitFailureAlreadyAdded() throws Exception {
		
		
		MvcResult andReturn = mockMvc.perform(
					post("/admin/board/delete")
					.param("boardId", board.getId().toString()+"0098")
					.with(csrf())
				)
		.andExpect(status().isOk())
		.andReturn();
		
		assertThat(andReturn.getResponse().getContentAsString().contains("게시판이 존재하지 않습니다.")).isTrue();
	}
	
	
}
