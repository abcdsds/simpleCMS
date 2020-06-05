package book.modules.admin;

import java.util.List;
import java.util.stream.IntStream;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import book.modules.account.Account;
import book.modules.account.AccountRepository;
import book.modules.admin.form.StatisticsForm;
import book.modules.board.Board;
import book.modules.board.BoardRepository;
import book.modules.comment.Comment;
import book.modules.comment.CommentRepository;
import book.modules.post.Post;
import book.modules.post.PostRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional
@Service
public class AdminService {

	private final AccountRepository accountRepository;
	private final PostRepository postRepository;
	private final BoardRepository boardRepository;
	private final CommentRepository commentRepository;

	private final ObjectMapper objectMapper;

	public String getAccountStatistics() throws JsonProcessingException {
		// TODO Auto-generated method stub
		return filter(accountRepository.findAllAccountMonthlyCount());
	}

	public String getCommentStatistics() throws JsonProcessingException {
		// TODO Auto-generated method stub
		return filter(commentRepository.findAllCommentMonthlyCount());
	}

	public String getPostStatistics() throws JsonProcessingException {
		// TODO Auto-generated method stub
		return filter(postRepository.findAllPostMonthlyCount());
	}

	public String filter(List<StatisticsForm> list) throws JsonProcessingException {

		int[] array = IntStream.range(1, 13).map(v -> 0).toArray();
		list.forEach(a -> array[a.getMonth() - 1] = a.getCount().intValue());

		return objectMapper.writeValueAsString(array);
	}

	public List<Board> getAllBoards() {
		// TODO Auto-generated method stub
		
		return boardRepository.findTop10ByOrderByIdDesc();
	}

	public List<Post> getAllPosts() {
		// TODO Auto-generated method stub
		return postRepository.findTop10ByOrderByIdDesc();
	}

	public List<Comment> getAllComments() {
		// TODO Auto-generated method stub
		return commentRepository.findTop10ByOrderByIdDesc();
	}

	public List<Account> getAllAccounts() {
		// TODO Auto-generated method stub
		return accountRepository.findTop10ByOrderByIdDesc();
	}
}
