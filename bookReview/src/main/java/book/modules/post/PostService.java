package book.modules.post;

import java.nio.file.AccessDeniedException;
import java.util.Arrays;
import java.util.Optional;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import book.modules.account.Account;
import book.modules.account.AccountRepository;
import book.modules.board.Board;
import book.modules.board.BoardRepository;
import book.modules.board.BoardService;
import book.modules.comment.form.DeleteType;
import book.modules.post.form.PostDeleteForm;
import book.modules.post.form.PostForm;
import book.modules.post.form.PostPrevNextForm;
import book.modules.post.vote.PostVote;
import book.modules.post.vote.PostVoteForm;
import book.modules.post.vote.PostVoteRepository;
import book.modules.post.vote.VoteType;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class PostService {

	private final PostRepository postRepository;
	private final PostVoteRepository postVoteRepository; 
	private final AccountRepository accountRepository;
	private final BoardRepository boardRepository;
	private final ModelMapper modelMapper;
	private final ObjectMapper objectMapper;
	private final BoardService boardService;

	public void add(Account account, PostForm form) throws AccessDeniedException {
		// TODO Auto-generated method stub
		
		Board board = boardService.getBoard(account, form.getBoardName());
		
		
		Optional<Account> findById = accountRepository.findById(account.getId());
		
		if (!findById.isPresent()) {
			
			throw new UsernameNotFoundException("사용자가 존재하지 않습니다.");
		}
		
		Account getAccount = findById.orElseThrow(null);

		Post post = Post.builder()
						 .best(false)
						 .title(form.getTitle())
						 .content(form.getContent())
						 .down(0)
						 .up(0)
						 .lock(false)
						 .views(0)
						 .deleted(false)
						 .board(board)
						 .build();
		
		Post save = postRepository.save(post);
		
		getAccount.getPosts().add(save);
		board.getPostList().add(save);
		
	}

	public Post getPost(Long id,HttpServletResponse response,HttpServletRequest request) throws NotFoundException {
		// TODO Auto-generated method stub
				
		
		Post post = postRepository.findPostAndCommentByIdAndDeleted(id, false).orElseThrow(() -> new NotFoundException("포스트를 찾을수 없습니다."));
		
		//Post post = postRepository.findPostAndCommentAndPrevNextPostByIdAndDeleted(id, false).orElseThrow(() -> new NotFoundException("포스트를 찾을수 없습니다."));
		
		//Post post = postRepository.findByIdAndDeleted(id, false).orElseThrow(() -> new NotFoundException("포스트를 찾을수 없습니다."));
		
		Cookie[] cookies = request.getCookies();
		
		for (Cookie cookie : cookies) {
			if (cookie.getName().equals("postViews")) {
				if (cookie.getValue().contains(id.toString())) {
					return post;
				}
			}
		}
		
		Optional<Cookie> findFirst = Arrays.stream(cookies).filter(c -> c.getName().equals("postViews")).findFirst();
		boolean anyMatchValue = Arrays.stream(cookies).anyMatch(c -> c.getName().equals("postViews") && c.getValue().indexOf("|"+id.toString()+'|') != -1);
		
		if (!findFirst.isPresent()) {
			
			Cookie myCookie = new Cookie("postViews" , "|" + id.toString() + "|");
			myCookie.setMaxAge(24 * 60 * 60 * 7); //쿠키 유효기간 7일
			post.increaseView();
			response.addCookie(myCookie);
		}
		
		if (findFirst.isPresent() && !anyMatchValue) {
			Cookie newCookie = findFirst.get();
			newCookie.setValue(findFirst.get().getValue() + id.toString() + "|");
			newCookie.setMaxAge(24 * 60 * 60 * 7); //쿠키 유효기간 7일
			post.increaseView();
			response.addCookie(newCookie);
		} 
		
		return post;
	}

	public Post getPostWithAccount(Long id, Account account,boolean deleted) throws AccessDeniedException {
		// TODO Auto-generated method stub
		Post findByIdAndAccount = postRepository.findByIdAndCreatedByAndDeleted(id,account,deleted);
		
		if (findByIdAndAccount == null) {
			throw new AccessDeniedException("잘못된 접근입니다.");
		}
		return findByIdAndAccount;
	}
	
	public void updatePost(PostForm form) throws NotFoundException {
		// TODO Auto-generated method stub
		Optional<Post> findById = postRepository.findById(form.getId());
		Post getPost = findById.orElseThrow(() -> new NotFoundException("글이 존재하지 않습니다."));
		

		Optional<Board> optionalBoard = boardRepository.findById(form.getBoardId());
		Board getBoard = optionalBoard.orElseThrow(() -> new NotFoundException("게시판이 존재하지 않습니다."));
		
		getPost.updateBoard(getBoard);
		modelMapper.map(form, getPost);		
		
	}
	
	public void updatePost(Post post, PostForm form) {
		// TODO Auto-generated method stub
		modelMapper.map(form, post);
	}

	public String UpdateDeleteStatus(Long id, Account account,boolean deleted) throws JsonProcessingException {
		// TODO Auto-generated method stub
		PostDeleteForm form = new PostDeleteForm();
		
		Post postWithAccount = postRepository.findByIdAndCreatedByAndDeleted(id,account,deleted);
		
		if (postWithAccount == null) {
			form.setMessage("글이 존재하지 않습니다.");
			form.setType(DeleteType.FALSE);
			return objectMapper.writeValueAsString(form);
		}
		
		postWithAccount.updateDeleteStatus(true);
		
		form.setMessage("삭제되었습니다.");
		form.setType(DeleteType.UPDATED);
		
		return objectMapper.writeValueAsString(form);
	}

	public PostVote createVote(Post post, VoteType voteType) {
		PostVote postVote = PostVote.builder().post(post).voteType(voteType).build();
		return postVote;
	}
	
	public String vote(Account account, Long postId, VoteType voteType) throws JsonProcessingException {
		// TODO Auto-generated method stub
		
		String message = voteType.equals(voteType.DOWN) ? "반대" : "추천";

		PostVoteForm form = new PostVoteForm();
		
		Optional<Post> findById = postRepository.findById(postId);
		
		if (!findById.isPresent()) {
			form.setMessage("글이 존재하지 않습니다.");
			return objectMapper.writeValueAsString(form);
		}
		
		Post post = findById.orElseThrow(null);
		
		
		PostVote findByPostAndCreatedBy = postVoteRepository.findByPostAndCreatedBy(post, account);
		if (findByPostAndCreatedBy != null) {
			form.setMessage("이미 "+message+"하셨습니다.");	
			form.setVoteDownCount(post.getDown());
			form.setVoteUpCount(post.getUp());
			return objectMapper.writeValueAsString(form);
		}
		
		findByPostAndCreatedBy = createVote(post,voteType);
		postVoteRepository.save(findByPostAndCreatedBy);
		
		if (voteType.equals(VoteType.UP)) {
			post.voteUp();
		} else {
			post.voteDown();
		}
		
		form.setMessage(message +" 되었습니다.");			
		form.setVoteDownCount(post.getDown());
		form.setVoteUpCount(post.getUp());
		
		
		return objectMapper.writeValueAsString(form);
	}

	public PostPrevNextForm getPrevNextId(Long id) {
		// TODO Auto-generated method stub
		return postRepository.findPrevNextPostById(id, false);
	}
	
	
}
