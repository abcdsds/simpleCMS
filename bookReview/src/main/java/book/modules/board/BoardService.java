package book.modules.board;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import book.modules.account.Account;
import book.modules.account.AccountRepository;
import book.modules.account.form.AccountListForm;
import book.modules.board.form.BoardForm;
import book.modules.board.manager.BoardManager;
import book.modules.board.manager.BoardManagerRepository;
import book.modules.post.PostRepository;
import book.modules.post.form.PostListForm;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;

@Transactional
@RequiredArgsConstructor
@Service
public class BoardService {

	private final BoardRepository boardRepository;
	private final BoardManagerRepository boardManagerRepository;
	private final PostRepository postRepository;
	private final AccountRepository accountRepository;
	private final ModelMapper modelMapper;
	
	public Board boardCreateTest(Account account) {
		//Board board = Board.builder().
		
		
		Board board = Board.builder().role(new SimpleGrantedAuthority("ROLE_USER")).name("기본게시판1").path("default").build();
				
		Board save = boardRepository.save(board);
		
		BoardManager bm = BoardManager.builder()
								.board(board).managedBy(account).managedAt(LocalDateTime.now()).build();
		
		boardManagerRepository.save(bm);

		board.getManagers().add(bm);
	
		return board;
	}

	public Board getBoard(Account account, String boardPath) throws AccessDeniedException {
		// TODO Auto-generated method stub
		Optional<Board> findByIdAndRole = boardRepository.findByPathAndRole(boardPath,new SimpleGrantedAuthority(account.getRole()));
		Board orElseThrow = findByIdAndRole.orElseThrow(() -> new AccessDeniedException("접근할수 없는 게시판입니다."));
		
		return orElseThrow;
	}

	public Page<PostListForm> getPostList(Board board, String keyword , Pageable pageable) {
		// TODO Auto-generated method stub
		
		if (keyword == null) {
			return postRepository.findAllPostByBoardAndDeleted(board.getPath(), false, pageable);
		}
		
		return postRepository.findAllPostByBoardAndDeletedWithKeyword(keyword, board.getPath(), false, pageable);
	}

	public Board addBoard(Account account, BoardForm boardForm) {
		// TODO Auto-generated method stub
		Board board = Board.builder().role(new SimpleGrantedAuthority(boardForm.getRole())).name(boardForm.getName()).path(boardForm.getPath()).build();
		
		Board save = boardRepository.save(board);
		
		BoardManager bm = BoardManager.builder()
								.board(board).managedBy(account).managedAt(LocalDateTime.now()).build();
		
		boardManagerRepository.save(bm);

		board.getManagers().add(bm);
	
		return board;
	}

	public Board updateBoard(BoardForm boardForm) throws NotFoundException {
		// TODO Auto-generated method stub
		Optional<Board> findById = boardRepository.findById(boardForm.getId());
		Board board = findById.orElseThrow(() -> new NotFoundException("게시판이 존재하지 않습니다."));
		modelMapper.map(boardForm, board);
		return board;
		
	}

	public Board getBoardWithId(Account account, Long id) throws NotFoundException {
		// TODO Auto-generated method stub
		Optional<Board> findById = boardRepository.findById(id);
		Board orElseThrow = findById.orElseThrow(() -> new NotFoundException("게시판이 존재하지 않습니다."));
		return orElseThrow;
	}

}
