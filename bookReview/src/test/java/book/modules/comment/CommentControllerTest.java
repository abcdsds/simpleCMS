package book.modules.comment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.Optional;

import javax.servlet.http.Cookie;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import book.modules.WithAccount;
import book.modules.account.Account;
import book.modules.account.AccountRepository;
import book.modules.board.Board;
import book.modules.board.BoardRepository;
import book.modules.board.BoardService;
import book.modules.board.manager.BoardManager;
import book.modules.board.manager.BoardManagerRepository;
import book.modules.post.Post;
import book.modules.post.PostRepository;
import book.modules.post.PostService;
import book.modules.post.vote.VoteType;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Rollback(value = true)
@Transactional
@AutoConfigureMockMvc
@SpringBootTest
class CommentControllerTest {

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
	
	private Board testBoard;
	
	private Post testPost;
		
	private Account testAccount;
	
	private Comment testComment;
	
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
	
	@WithAccount("ididid")
	@DisplayName("댓글 반대 - 실패 - 이미 반대한 상태")
	@Test
	void commentVoteDownFailure() throws Exception {
		
		commentService.vote(testComment.getId(), testPost.getId(), testAccount, VoteType.DOWN);
		
		MvcResult andReturn = mockMvc.perform(post("/comment/vote/down")
						.param("postId", testPost.getId().toString())
						.param("commentId", testComment.getId().toString())
						.with(csrf())
				)
				.andExpect(status().isOk())
				.andReturn();

		assertThat(andReturn.getResponse().getContentAsString().contains("이미 반대했습니다.")).isTrue();
		
	}
	
	
	@WithAccount("ididid")
	@DisplayName("댓글 반대 - 성공")
	@Test
	void commentVoteDownSuccess() throws Exception {
		
		MvcResult andReturn = mockMvc.perform(post("/comment/vote/down")
						.param("postId", testPost.getId().toString())
						.param("commentId", testComment.getId().toString())
						.with(csrf())
				)
				.andExpect(status().isOk())
				.andReturn();

		assertThat(andReturn.getResponse().getContentAsString().contains("반대 되었습니다.")).isTrue();
		
	}
	
	
	@WithAccount("ididid")
	@DisplayName("댓글 추천 - 실패 - 이미 추천한 상태")
	@Test
	void commentVoteUpFailure() throws Exception {
		
		commentService.vote(testComment.getId(), testPost.getId(), testAccount, VoteType.UP);
		
		MvcResult andReturn = mockMvc.perform(post("/comment/vote/up")
						.param("postId", testPost.getId().toString())
						.param("commentId", testComment.getId().toString())
						.with(csrf())
				)
				.andExpect(status().isOk())
				.andReturn();

		assertThat(andReturn.getResponse().getContentAsString().contains("이미 추천했습니다.")).isTrue();
		
	}
	
	
	@WithAccount("ididid")
	@DisplayName("댓글 추천 - 성공")
	@Test
	void commentVoteUpSuccess() throws Exception {
		
		MvcResult andReturn = mockMvc.perform(post("/comment/vote/up")
						.param("postId", testPost.getId().toString())
						.param("commentId", testComment.getId().toString())
						.with(csrf())
				)
				.andExpect(status().isOk())
				.andReturn();

		assertThat(andReturn.getResponse().getContentAsString().contains("추천 되었습니다.")).isTrue();
		
	}
	
	
	@WithAccount("ididid")
	@DisplayName("댓글 삭제 - 실패 - 존재하지 않는 댓글번호")
	@Test
	void commentSubDeleteSubmitFailure() throws Exception {
		
		MvcResult andReturn = mockMvc.perform(post("/comment/delete/")
						.param("postId", testPost.getId().toString())
						.param("commentId", testComment.getId().toString()+"1")
						.with(csrf())
				)
				.andExpect(status().isBadRequest())
				.andReturn();

		assertThat(andReturn.getResponse().getContentAsString().contains("댓글이 존재하지 않습니다.")).isTrue();
		
	}
	
	
	@WithAccount("ididid")
	@DisplayName("댓글 삭제 - 성공")
	@Test
	void commentSubDeleteSubmitSuccess() throws Exception {
		
		mockMvc.perform(post("/comment/delete/")
						.param("postId", testPost.getId().toString())
						.param("commentId", testComment.getId().toString())
						.with(csrf())
				)
				.andExpect(status().isOk());
		
		Optional<Comment> findById = commentRepository.findById(testComment.getId());
		assertTrue(findById.isPresent());
		assertThat(findById.get().isDeleted()).isEqualTo(true);
		
	}
	
	
	@WithAccount("ididid")
	@DisplayName("댓글 수정 - 실패 - 댓글내용 50자 초과")
	@Test
	void commentSubUpdateSubmitFailure() throws Exception {
		
		mockMvc.perform(post("/comment/update/"+testPost.getId()+"/"+testComment.getId())
						.param("postId", testPost.getId().toString())
						.param("parentCommentId", testComment.getId().toString())
						.param("depth", "0")
						.param("content", "댓글내용sub댓글내용sub댓글내용sub댓글내용sub댓글내용sub댓글내용sub댓글내용sub댓글내용sub댓글내용sub댓글내용sub댓글내용sub")
						.with(csrf())
				)
				.andExpect(status().is3xxRedirection());
		
		Optional<Comment> findById = commentRepository.findById(testComment.getId());
		assertTrue(findById.isPresent());
		assertThat(findById.get().getContent()).isEqualTo("댓글8172");
		
	}
	
	
	@WithAccount("ididid")
	@DisplayName("댓글 수정 - 성공")
	@Test
	void commentSubUpdateSubmitSuccess() throws Exception {
		
		mockMvc.perform(post("/comment/update/"+testPost.getId()+"/"+testComment.getId())
						.param("postId", testPost.getId().toString())
						.param("parentCommentId", testComment.getId().toString())
						.param("depth", "0")
						.param("content", "댓글내용sub")
						.with(csrf())
				)
				.andExpect(status().is3xxRedirection());
		
		Optional<Comment> findById = commentRepository.findById(testComment.getId());
		assertTrue(findById.isPresent());
		assertThat(findById.get().getContent()).isEqualTo("댓글내용sub");
		
	}
	
	@WithAccount("ididid")
	@DisplayName("대댓글 등록 - 실패 - 댓글내용 50자초과")
	@Test
	void commentSubAddSubmitFailure() throws Exception {
		
		mockMvc.perform(post("/comment/add/"+testPost.getId()+"/"+testComment.getId())
						.param("postId", testPost.getId().toString())
						.param("parentCommentId", testComment.getId().toString())
						.param("depth", "1")
						.param("content", "댓글내용댓글내용댓글내용댓글내용댓글내용댓글내용댓글내용댓글내용댓글내용댓글내용댓글내용댓글내용댓글내용댓글내용")
						.with(csrf())
				)
				.andExpect(status().is3xxRedirection());
		
		Comment findTopByOrderByIdDesc = commentRepository.findTopByOrderByIdDesc();
		
		assertNotNull(findTopByOrderByIdDesc);
		assertThat(findTopByOrderByIdDesc.getPost()).isEqualTo(testPost);
		assertThat(findTopByOrderByIdDesc.getContent()).isNotEqualTo("댓글내용sub");
		assertThat(findTopByOrderByIdDesc.getDepth()).isNotEqualTo(1);
		
	}
	
	
	@WithAccount("ididid")
	@DisplayName("대댓글 등록 - 성공")
	@Test
	void commentSubAddSubmitSuccess() throws Exception {
		
		mockMvc.perform(post("/comment/add/"+testPost.getId()+"/"+testComment.getId())
						.param("postId", testPost.getId().toString())
						.param("parentCommentId", testComment.getId().toString())
						.param("depth", "1")
						.param("content", "댓글내용sub")
						.with(csrf())
				)
				.andExpect(status().is3xxRedirection());
		
		Comment findTopByOrderByIdDesc = commentRepository.findTopByOrderByIdDesc();
		
		assertNotNull(findTopByOrderByIdDesc);
		assertThat(findTopByOrderByIdDesc.getPost()).isEqualTo(testPost);
		assertThat(findTopByOrderByIdDesc.getContent()).isEqualTo("댓글내용sub");
		assertThat(findTopByOrderByIdDesc.getDepth()).isEqualTo(1);
		
	}
	
	@WithAccount("ididid")
	@DisplayName("댓글 등록 - 실패 - 댓글 내용 50자초과")
	@Test
	void commentAddSubmitFailure() throws Exception {
		
		mockMvc.perform(post("/comment/add/"+testPost.getId())
						.param("postId", testPost.getId().toString())
						.param("parentCommentId", "0")
						.param("depth", "0")
						.param("content", "댓글내용댓글내용댓글내용댓글내용댓글내용댓글내용댓글내용댓글내용댓글내용댓글내용댓글내용댓글내용댓글내용댓글내용")
						.with(csrf())
				)
				.andExpect(status().is3xxRedirection());
		
		Comment findTopByOrderByIdDesc = commentRepository.findTopByOrderByIdDesc();
		
		assertNotNull(findTopByOrderByIdDesc);
		assertThat(findTopByOrderByIdDesc.getContent()).isNotEqualTo("댓글내용댓글내용댓글내용댓글내용댓글내용댓글내용댓글내용댓글내용댓글내용댓글내용댓글내용댓글내용댓글내용댓글내용");
		
	}
	
	@WithAccount("ididid")
	@DisplayName("댓글 등록 - 성공")
	@Test
	void commentAddSubmitSuccess() throws Exception {
		
		mockMvc.perform(post("/comment/add/"+testPost.getId())
						.param("postId", testPost.getId().toString())
						.param("parentCommentId", "0")
						.param("depth", "0")
						.param("content", "댓글내용")
						.with(csrf())
				)
				.andExpect(status().is3xxRedirection());
		
		Comment findTopByOrderByIdDesc = commentRepository.findTopByOrderByIdDesc();
		
		assertNotNull(findTopByOrderByIdDesc);
		assertThat(findTopByOrderByIdDesc.getPost()).isEqualTo(testPost);
		assertThat(findTopByOrderByIdDesc.getContent()).isEqualTo("댓글내용");
		assertThat(findTopByOrderByIdDesc.getDepth()).isEqualTo(0);
		
	}

}
