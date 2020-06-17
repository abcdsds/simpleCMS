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

import java.time.LocalDateTime;

import javax.servlet.http.Cookie;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

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
import book.modules.board.manager.BoardManager;
import book.modules.board.manager.BoardManagerRepository;
import book.modules.menu.Menu;
import book.modules.menu.MenuType;
import book.modules.post.Post;
import book.modules.post.PostRepository;
import book.modules.post.PostService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Rollback(value = true)
@MockMvcTest
class AdminPostControllerTest {

	@Autowired private MockMvc mockMvc;
	
	@Autowired private PostRepository postRepository;
	
	@Autowired private BoardRepository boardRepository;
	
	@Autowired private BoardManagerRepository boardManagerRepository;
	
	@Autowired private PasswordEncoder passwordEncoder;
	
	@Autowired private AccountService accountService;
	
	private Board testBoard;
	
	private Post testPost;
	
	private Board testSecondBoard;
	
	private Account testAccount;
	
	
	@BeforeEach
	void before() {
		
		String loginId = "choiyurim2";
		AccountForm form = new AccountForm();
		form.setLoginId(loginId);
		form.setBirthYear(1990);
		form.setPassword(passwordEncoder.encode("12312300"));
		form.setEmail(loginId+"@naver.com");
		form.setNickname(loginId+"nickname");
		form.setPassword("12341234");
		form.setAccountGender(AccountGender.male);
		
		testAccount = accountService.accountCreate(form);

		Board board = Board.builder().role(new SimpleGrantedAuthority("ROLE_USER")).name("기본게시판2").path("default2").build();
		
		testBoard  = boardRepository.save(board);
		
		Board secondBoard = Board.builder().role(new SimpleGrantedAuthority("ROLE_USER")).name("기본게시판3").path("default3").build();
		
		testSecondBoard  = boardRepository.save(secondBoard);
		
		
		BoardManager bm = BoardManager.builder()
								.board(board).managedBy(testAccount).managedAt(LocalDateTime.now()).build();
		
		boardManagerRepository.save(bm);

		testBoard.getManagers().add(bm);
		
		Post post = Post.builder()
				 .best(false)
				 .title("제목1")
				 .content("내용1")
				 .down(0)
				 .up(0)
				 .lock(false)
				 .views(0)
				 .deleted(false)
				 .board(testBoard)
				 .build();

		testPost = postRepository.save(post);
		
		testAccount.getPosts().add(testPost);
		testBoard.getPostList().add(testPost);

	}
	
	
	
	@WithAccount("admintest01")
	@DisplayName("어드민 페이지 게시글 관리 메인")
	@Test
	void adminPostList() throws Exception {
		
		mockMvc.perform(get("/admin/post"))
		.andExpect(status().isOk())
		.andExpect(model().attributeExists("postList"))
		.andExpect(model().attributeExists("keyword"))
		.andExpect(view().name("admin/post/post"));
	}
	
	@WithAccount("admintest01")
	@DisplayName("어드민 페이지 게시글 관리 - 수정 form")
	@Test
	void adminPostUpdateForm() throws Exception {
		
		mockMvc.perform(get("/admin/post/update/" + testPost.getId()))
		.andExpect(status().isOk())
		.andExpect(model().attributeExists("allBoardList"))
		.andExpect(model().attributeExists("postForm"))
		.andExpect(view().name("admin/post/update"));
	}
	
	@WithAccount("admintest01")
	@DisplayName("어드민 페이지 게시글 관리 - 수정 submit - 성공")
	@Test
	void adminPostUpdateSubmitSuccess() throws Exception {
		
		mockMvc.perform(
					post("/admin/post/update/")
					.param("id", testPost.getId().toString())
					.param("title", "제목ab0090763b")
					.param("content", "내용ab0090763b")
					.param("boardId", testSecondBoard.getId().toString())
					.param("boardName", testSecondBoard.getPath())
					.param("deleted", "true")	
					.with(csrf())
				)
		.andExpect(status().is3xxRedirection())
		.andExpect(flash().attributeExists("message"));
		
		assertThat(testPost.getTitle()).isEqualTo("제목ab0090763b");
		assertThat(testPost.getContent()).isEqualTo("내용ab0090763b");
		assertThat(testPost.isDeleted()).isEqualTo(true);
		assertThat(testPost.getBoard().getId()).isEqualTo(testSecondBoard.getId());
		
	}
	
	
	@WithAccount("admintest01")
	@DisplayName("어드민 페이지 게시글 관리 - 수정 submit - 실패 (content 없는 경우)")
	@Test
	void adminPostUpdateSubmitFailureNoContent() throws Exception {
		
		mockMvc.perform(
					post("/admin/post/update/")
					.param("id", testPost.getId().toString())
					.param("title", "제목ab0090763b")
					.param("boardId", testSecondBoard.getId().toString())
					.param("boardName", testSecondBoard.getPath())
					.param("deleted", "true")	
					.with(csrf())
				)
		.andExpect(status().is3xxRedirection())
		.andExpect(flash().attributeExists("message"));
		
		assertThat(testPost.getTitle()).isNotEqualTo("제목ab0090763b");
		assertThat(testPost.getContent()).isNotEqualTo("내용ab0090763b");
		assertThat(testPost.isDeleted()).isNotEqualTo(true);
		assertThat(testPost.getBoard().getId()).isNotEqualTo(testSecondBoard.getId());
		
	}
	
	@WithAccount("admintest01")
	@DisplayName("어드민 페이지 게시글 관리 - 수정 submit - 실패 (title 50자이상)")
	@Test
	void adminPostUpdateSubmitFailureWithTitle() throws Exception {
		
		mockMvc.perform(
					post("/admin/post/update/")
					.param("id", testPost.getId().toString())
					.param("title", "제목ab0090763bㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇ")
					.param("content", "내용ab0090763b")
					.param("boardId", testSecondBoard.getId().toString())
					.param("boardName", testSecondBoard.getPath())
					.param("deleted", "true")	
					.with(csrf())
				)
		.andExpect(status().is3xxRedirection())
		.andExpect(flash().attributeExists("message"));
		
		assertThat(testPost.getTitle()).isNotEqualTo("제목ab0090763b");
		assertThat(testPost.getContent()).isNotEqualTo("내용ab0090763b");
		assertThat(testPost.isDeleted()).isNotEqualTo(true);
		assertThat(testPost.getBoard().getId()).isNotEqualTo(testSecondBoard.getId());
		
	}

	
//	@WithAccount("admintest01")
//	@DisplayName("어드민 페이지 게시판 관리 - 추가 submit - 성공")
//	@Test
//	void adminBoardAddSubmitSuccess() throws Exception {
//		
//		mockMvc.perform(
//					post("/admin/post/add")
//					.param("name", "wioie")
//					.param("role", "ROLE_USER")
//					.param("type", "BOARD")
//					.param("boardId", board.getId().toString())
//					.param("boardName", board.getName())
//					.with(csrf())
//				)
//		.andExpect(status().is3xxRedirection());
//		
//		Menu lastMenu = menuRepository.findTop1ByOrderByIdDesc();
//		
//		assertThat(lastMenu.getName()).isEqualTo("wioie");
//		assertThat(lastMenu.getPath()).isEqualTo(board.getPath());
//		assertThat(lastMenu.getRole().toString()).isEqualTo("ROLE_USER");
//		assertThat(lastMenu.getType()).isEqualTo(MenuType.BOARD);
//	}
}
