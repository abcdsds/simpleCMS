package book.modules.admin;

import java.util.List;
import java.util.stream.IntStream;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import book.modules.account.Account;
import book.modules.account.AccountRepository;
import book.modules.account.form.AccountListForm;
import book.modules.admin.form.StatisticsForm;
import book.modules.board.Board;
import book.modules.board.BoardRepository;
import book.modules.board.manager.BoardManagerRepository;
import book.modules.comment.Comment;
import book.modules.comment.CommentRepository;
import book.modules.menu.Menu;
import book.modules.menu.MenuRepository;
import book.modules.post.Post;
import book.modules.post.PostRepository;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional
@Service
public class AdminService {

	private final AccountRepository accountRepository;
	private final PostRepository postRepository;
	private final BoardManagerRepository boardManagerRepository;
	private final BoardRepository boardRepository;
	private final CommentRepository commentRepository;
	private final MenuRepository menuRespository;

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


	public List<Account> getAllAccounts() {
		
		return accountRepository.findAll();
	}
	
	public List<Board> getTop10Boards() {
		// TODO Auto-generated method stub
		return boardRepository.findTop10ByOrderByIdDesc();
	}

	public List<Comment> getTop10Comments() {
		// TODO Auto-generated method stub
		return commentRepository.findTop10ByOrderByIdDesc();
	}
	
	public List<Post> getTop10Posts() {
		// TODO Auto-generated method stub
		return postRepository.findTop10ByOrderByIdDesc();
	}
	
	public List<Account> getTop10Accounts() {
		// TODO Auto-generated method stub
		return accountRepository.findTop10ByOrderByIdDesc();
	}

	public Page<AccountListForm> getAccountsWithBoardId(Long id, Pageable pageable) {
		// TODO Auto-generated method stub
		
		return accountRepository.findAccountByBoardManagerId(id, pageable);
	}

	public Page<AccountListForm> getAccounts(Pageable pageable) {
		// TODO Auto-generated method stub
		return accountRepository.findAllAccount(pageable);
	}

	public List<Menu> getAllMenus() {
		// TODO Auto-generated method stub
		return menuRespository.findAll();
	}

	public List<Board> getAllBoards() {
		// TODO Auto-generated method stub
		return boardRepository.findAll();
	}

	public Menu getMenu(Long id) throws NotFoundException {
		// TODO Auto-generated method stub
		return menuRespository.findById(id).orElseThrow(() -> new NotFoundException("메뉴가 존재하지 않습니다."));
	}

	public List<Menu> getAllMenusWithOutId(Long id) {
		// TODO Auto-generated method stub
		return menuRespository.findAllByWithOutId(id);
	}	

}
