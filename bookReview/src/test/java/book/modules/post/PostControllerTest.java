package book.modules.post;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.time.LocalDateTime;
import java.util.Optional;

import javax.servlet.http.Cookie;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import book.MockMvcTest;
import book.modules.WithAccount;
import book.modules.account.Account;
import book.modules.account.AccountRepository;
import book.modules.board.Board;
import book.modules.board.BoardRepository;
import book.modules.board.BoardService;
import book.modules.board.manager.BoardManager;
import book.modules.board.manager.BoardManagerRepository;
import book.modules.post.vote.PostVote;
import book.modules.post.vote.VoteType;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Rollback(value = true)
@MockMvcTest
class PostControllerTest {

	@Autowired private MockMvc mockMvc;
	
	@Autowired private PostRepository postRepository;
	
	@Autowired private PostService postService;
	
	@Autowired private AccountRepository accountRepository;
	
	@Autowired private BoardRepository boardRepository;
	
	@Autowired private BoardManagerRepository boardManagerRepository;
	
	private Board testBoard;
	
	private Post testPost;
	
	private Cookie someCookie;
	
	private Account testAccount;
	
	
	@WithAccount("ididid")
	@BeforeEach
	void before() {
		testAccount = accountRepository.findByLoginId("ididid");

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

	}
	
	
	@WithAccount("ididid")
	@DisplayName("게시판 글 삭제 - 실패 - 없는 글 번호")
	@Test
	void postDeleteSubmitFailure() throws Exception {
		mockMvc.perform(
					post("/post/delete")
					.param("postId", testPost.getId().toString()+"1")
					.with(csrf())
				)
				.andExpect(status().isOk());
		
		
		assertFalse(testPost.isDeleted());
		
	}
	
	
	@WithAccount("ididid")
	@DisplayName("게시판 글 삭제 - 성공")
	@Test
	void postDeleteSubmitSuccess() throws Exception {
		mockMvc.perform(
					post("/post/delete")
					.param("postId", testPost.getId().toString())
					.with(csrf())
				)
				.andExpect(status().isOk());
		
		
		assertTrue(testPost.isDeleted());
		
	}
	
	
	@WithAccount("ididid")
	@DisplayName("게시판 수정 서브밋 - 실패 - 글자수 50자 초과")
	@Test
	void postUpdateSubmitFailure() throws Exception {
		mockMvc.perform(
					post("/post/update/"+testPost.getId())
					.param("title", "제목ab0090763b제목ab0090763b제목ab0090763b제목ab0090763b제목ab0090763b")
					.param("content", "내용ab0090763b")
					.param("boardName", testBoard.getPath())
					.with(csrf())
				)
				.andExpect(status().is3xxRedirection());
		
		assertThat(testPost.getTitle()).isEqualTo("제목1");
		assertThat(testPost.getContent()).isEqualTo("내용1");
		
	}
	
	
	@WithAccount("ididid")
	@DisplayName("게시판 수정 서브밋 - 성공")
	@Test
	void postUpdateSubmitSuccess() throws Exception {
		mockMvc.perform(
					post("/post/update/"+testPost.getId())
					.param("title", "제목ab0090763b")
					.param("content", "내용ab0090763b")
					.param("boardName", testBoard.getPath())
					.with(csrf())
				)
				.andExpect(status().is3xxRedirection());
		
		assertThat(testPost.getTitle()).isEqualTo("제목ab0090763b");
		assertThat(testPost.getContent()).isEqualTo("내용ab0090763b");
		
	}
	
	
	
	@WithAccount("ididid")
	@DisplayName("게시판 수정 폼")
	@Test
	void postUpdateForm() throws Exception {
		mockMvc.perform(
					get("/post/update/"+testPost.getId())
				)
				.andExpect(status().isOk())
				.andExpect(model().attributeExists("account"))
				.andExpect(model().attributeExists("postForm"))
				.andExpect(model().attributeExists("post_id"))
				.andExpect(view().name("post/update"));
		
	}
	
	
	
	@WithAccount("ididid")
	@DisplayName("게시판 글 반대하기 - 실패 - 이미반대완료")
	@Test
	void postViewVoteDownFailure() throws Exception {
		
		postService.vote(testAccount, testPost.getId(), VoteType.DOWN);
		
		MvcResult andReturn = mockMvc.perform(
					post("/post/vote/down")
						.param("postId", testPost.getId().toString())
					
					.with(csrf())
				)
				.andExpect(status().isOk())
				.andReturn();
		
		 assertTrue(andReturn.getResponse().getContentAsString().contains("이미 반대하셨습니다."));
	}
	
	
	@WithAccount("ididid")
	@DisplayName("게시판 글 반대하기 - 성공")
	@Test
	void postViewVoteDownSuccess() throws Exception {

		 MvcResult andReturn = mockMvc.perform(
					post("/post/vote/down")
						.param("postId", testPost.getId().toString())
					
					.with(csrf())
				)
				.andExpect(status().isOk())
				.andReturn();
		
		 assertTrue(andReturn.getResponse().getContentAsString().contains("반대 되었습니다."));
	}
	
	
	@WithAccount("ididid")
	@DisplayName("게시판 글 추천하기 - 실패 - 이미추천완료")
	@Test
	void postViewVoteUpFailure() throws Exception {
		
		postService.vote(testAccount, testPost.getId(), VoteType.UP);
		
		MvcResult andReturn = mockMvc.perform(
					post("/post/vote/up")
						.param("postId", testPost.getId().toString())
					
					.with(csrf())
				)
				.andExpect(status().isOk())
				.andReturn();
		
		 assertTrue(andReturn.getResponse().getContentAsString().contains("이미 추천하셨습니다."));
	}
	
	
	@WithAccount("ididid")
	@DisplayName("게시판 글 추천하기 - 성공")
	@Test
	void postViewVoteUpSuccess() throws Exception {
		
		
		
		 MvcResult andReturn = mockMvc.perform(
					post("/post/vote/up")
						.param("postId", testPost.getId().toString())
					
					.with(csrf())
				)
				.andExpect(status().isOk())
				.andReturn();
		
		 assertTrue(andReturn.getResponse().getContentAsString().contains("추천 되었습니다."));
	}
	

	
	@WithAccount("ididid")
	@DisplayName("게시판 글 보기 - 중복 조회수 방지 체크")
	@Test
	void postViewCheckViewsCount() throws Exception {
		
		someCookie = new Cookie("postViews", "|"+testPost.getId()+"|");
		
		int beforeViews = testPost.getViews();
		
		mockMvc.perform(
					get("/post/view/"+testPost.getId())
					.cookie(someCookie)
				)
				.andExpect(status().isOk())
				.andExpect(model().attributeExists("account"))
				.andExpect(model().attributeExists("post"))
				.andExpect(model().attributeExists("postPrevNextForm"))
				.andExpect(model().attributeExists("commentTotalCount"))
				.andExpect(view().name("post/view"));
		
		assertEquals(testPost.getViews(), beforeViews);
		
	}
	
	@WithAccount("ididid")
	@DisplayName("게시판 글 보기 및 조회수 증가 체크")
	@Test
	void postView() throws Exception {
		
		someCookie = new Cookie("postViews", "");
		
		int beforeViews = testPost.getViews();
		
		mockMvc.perform(
					get("/post/view/"+testPost.getId())
					.cookie(someCookie)
				)
				.andExpect(status().isOk())
				.andExpect(model().attributeExists("account"))
				.andExpect(model().attributeExists("post"))
				.andExpect(model().attributeExists("postPrevNextForm"))
				.andExpect(model().attributeExists("commentTotalCount"))
				.andExpect(view().name("post/view"));
		
		assertNotEquals(testPost.getViews() , beforeViews);
		
	}
	
	@WithAccount("ididid")
	@DisplayName("게시판 글쓰기 서브밋 - 실패 - 제목 50자 초과")
	@Test
	void postAddSubmitFailure() throws Exception {
		mockMvc.perform(
					post("/post/add")
					.param("title", "제목제목제목제목제목제목제목제목제목제목제목제목제목제목제목제목제목제목제목제목제목제목제목제목제목제목제목제목제목제목")
					.param("content", "내용9811")
					.param("boardName", testBoard.getPath())
					.with(csrf())
				)
				.andExpect(status().isOk())
				.andExpect(model().attributeExists("account"))
				.andExpect(view().name("post/add"));
		
		Post findTopByOrderByIdDesc = postRepository.findTopByOrderByIdDesc();
		
		assertNotNull(findTopByOrderByIdDesc);
		assertThat(findTopByOrderByIdDesc.getContent()).isNotEqualTo("내용9811");
		
	}
	
	
	@WithAccount("ididid")
	@DisplayName("게시판 글쓰기 서브밋 - 성공")
	@Test
	void postAddSubmitSuccess() throws Exception {
		mockMvc.perform(
					post("/post/add")
					.param("title", "제목ab0090763b")
					.param("content", "내용ab0090763b")
					.param("boardName", testBoard.getPath())
					.with(csrf())
				)
				.andExpect(status().is3xxRedirection());
		
		Post findTopByOrderByIdDesc = postRepository.findTopByOrderByIdDesc();
		
		assertNotNull(findTopByOrderByIdDesc);
		assertThat(findTopByOrderByIdDesc.getTitle()).isEqualTo("제목ab0090763b");
		assertThat(findTopByOrderByIdDesc.getContent()).isEqualTo("내용ab0090763b");
		assertThat(findTopByOrderByIdDesc.getBoard()).isEqualTo(testBoard);
	}
	
	
	@WithAccount("ididid")
	@DisplayName("게시판 글쓰기 폼")
	@Test
	void postAddForm() throws Exception {
		mockMvc.perform(
					get("/post/"+testBoard.getPath()+"/add")
				)
				.andExpect(status().isOk())
				.andExpect(model().attributeExists("account"))
				.andExpect(model().attributeExists("postForm"))
				.andExpect(view().name("post/add"));
		
	}

	
}
