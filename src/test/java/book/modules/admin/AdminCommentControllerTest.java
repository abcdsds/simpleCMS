package book.modules.admin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.time.LocalDateTime;

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
import book.modules.comment.Comment;
import book.modules.comment.CommentRepository;
import book.modules.comment.CommentService;
import book.modules.post.Post;
import book.modules.post.PostRepository;
import book.modules.post.PostService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Rollback(value = true)
@MockMvcTest
class AdminCommentControllerTest {

	@Autowired private MockMvc mockMvc;
	
	@Autowired private PostRepository postRepository;
	
	@Autowired private PostService postService;
	
	@Autowired private BoardService boardService;
	
	@Autowired private AccountRepository accountRepository;
	
	@Autowired private BoardRepository boardRepository;
	
	@Autowired private BoardManagerRepository boardManagerRepository;
	
	@Autowired private ObjectMapper objectMapper;
	
	@Autowired private CommentRepository commentRepository;
	
	@Autowired private CommentService commentService;
	
	@Autowired private PasswordEncoder passwordEncoder;
	
	@Autowired private AccountService accountService;
	
	private Board testBoard;
	
	private Post testPost;
		
	private Account testAccount;
	
	private Comment testComment;
	
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
		
		Comment comment = Comment.builder()
				.best(false)
				.content("댓글8172")
				.depth(0)
				.down(0)
				.up(0)
				.post(post)
				.build();

		testComment = commentRepository.save(comment);
		testComment.updateGroup(testComment);
	}
	
	@WithAccount("admintest01")
	@DisplayName("어드민 페이지 댓글 관리 메인")
	@Test
	void adminCommentList() throws Exception {
		
		mockMvc.perform(get("/admin/comment"))
		.andExpect(status().isOk())
		.andExpect(model().attributeExists("resultList"))
		.andExpect(model().attributeExists("keyword"))
		.andExpect(view().name("admin/comment/comment"));
	}
	
	@WithAccount("admintest01")
	@DisplayName("어드민 페이지 댓글 관리 - 수정 form")
	@Test
	void adminCommentUpdateForm() throws Exception {
		
		mockMvc.perform(get("/admin/comment/update/" + testComment.getId()))
		.andExpect(status().isOk())
		.andExpect(model().attributeExists("commentForm"))
		.andExpect(view().name("admin/comment/update"));
	}
	
	@WithAccount("admintest01")
	@DisplayName("어드민 페이지 댓글 관리 - 수정 submit - 성공")
	@Test
	void adminCommentUpdateSubmitSuccess() throws Exception {
		
		mockMvc.perform(
					post("/admin/comment/update/")
					.param("postId", testPost.getId().toString())
					.param("id", testComment.getId().toString())
					.param("depth", "0")
					.param("content", "댓글내용sub")
					.param("deleted", "true")
					.with(csrf())
				)
		.andExpect(status().is3xxRedirection());
		
		assertThat(testComment.getContent()).isEqualTo("댓글내용sub");
		assertThat(testComment.isDeleted()).isEqualTo(true);
	}
	
	@WithAccount("admintest01")
	@DisplayName("어드민 페이지 댓글 관리 - 수정 submit - 실패 (50자 초과)")
	@Test
	void adminCommentUpdateSubmitFailContentlargethan50() throws Exception {
		
		mockMvc.perform(
					post("/admin/comment/update/")
					.param("postId", testPost.getId().toString())
					.param("id", testComment.getId().toString())
					.param("depth", "0")
					.param("content", "댓글내용sub댓글내용댓글내용댓글내용댓글내용댓글내용댓글내용댓글내용댓글내용댓글내용댓글내용댓글내용댓글내용댓글내용댓글내용댓글내용댓글내용댓글내용댓글내용댓글내용")
					.param("deleted", "true")
					.with(csrf())
				)
		.andExpect(status().isOk())
		.andExpect(model().attributeExists("message"))
		.andExpect(view().name("admin/comment/update"));
		
		assertThat(testComment.getContent()).isEqualTo("댓글8172");
		assertThat(testComment.isDeleted()).isEqualTo(false);
	}
	
	@WithAccount("admintest01")
	@DisplayName("어드민 페이지 댓글 관리 - 수정 submit - 실패 ( 내용 없음 )")
	@Test
	void adminCommentUpdateSubmitFailNoContent() throws Exception {
		
		mockMvc.perform(
					post("/admin/comment/update/")
					.param("postId", testPost.getId().toString())
					.param("id", testComment.getId().toString())
					.param("depth", "0")
					.param("deleted", "true")
					.with(csrf())
				)
		.andExpect(status().isOk())
		.andExpect(model().attributeExists("message"))
		.andExpect(view().name("admin/comment/update"));
		
		assertThat(testComment.getContent()).isEqualTo("댓글8172");
		assertThat(testComment.isDeleted()).isEqualTo(false);
	}
	
	
}
