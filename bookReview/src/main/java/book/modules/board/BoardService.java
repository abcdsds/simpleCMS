package book.modules.board;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import book.modules.account.Account;
import book.modules.account.AccountRepository;
import book.modules.board.form.BoardForm;
import book.modules.board.form.BoardMessageForm;
import book.modules.board.form.BoardMessageType;
import book.modules.board.manager.BoardManager;
import book.modules.board.manager.BoardManagerRepository;
import book.modules.post.Post;
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
	private final ObjectMapper objectMapper;
	
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

	public Board addBoard(Account account, BoardForm boardForm) throws NotFoundException {
		// TODO Auto-generated method stub
		Board board = Board.builder().role(new SimpleGrantedAuthority(boardForm.getRole())).name(boardForm.getName()).path(boardForm.getPath()).build();
		
		Board save = boardRepository.save(board);
		
		BoardManager bm = BoardManager.builder()
								.board(board).managedBy(account).managedAt(LocalDateTime.now()).build();
		
		boardManagerRepository.save(bm);

		board.getManagers().add(bm);
		
		Optional<Account> findById = accountRepository.findById(account.getId());
		
		Account getAccount = findById.orElseThrow(() -> new NotFoundException("아이디가 존재하지 않습니다."));
		
		getAccount.getManagers().add(bm);
	
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

	public String addBoardManager(Long accountId, Long boardId) throws JsonProcessingException {
		// TODO Auto-generated method stub
		
		BoardMessageForm form = new BoardMessageForm();
		
		Board getBoard = boardRepository.findNotOptionalBoardById(boardId);
		
		if (getBoard == null) {
			
			form.setMessage("게시판이 존재하지 않습니다.");
			form.setMessageType(BoardMessageType.FAIL);
			return objectMapper.writeValueAsString(form);
		}

		Account getAccount = accountRepository.findNotOptionalById(accountId);
		
		if (getAccount == null) {
			
			form.setMessage("아이디가 존재하지 않습니다.");
			form.setMessageType(BoardMessageType.FAIL);
			return objectMapper.writeValueAsString(form);
			
		}
		
		BoardManager getBoardManager = boardManagerRepository.findByBoardAndManagedBy(getBoard, getAccount);
		
		if (getBoardManager != null) {
			
			form.setMessage("이미 등록된 관리자입니다.");
			form.setMessageType(BoardMessageType.FAIL);
			return objectMapper.writeValueAsString(form);
		}
		
		BoardManager bm = BoardManager.builder().board(getBoard).managedBy(getAccount).managedAt(LocalDateTime.now()).build();
		boardManagerRepository.save(bm);
		
		getBoard.getManagers().add(bm);
		getAccount.getManagers().add(bm);
		
		
		form.setMessage("성공적으로 추가했습니다.");
		form.setMessageType(BoardMessageType.SUCCESS);
		return objectMapper.writeValueAsString(form);
	}

	public String deleteBoardManager(Long accountId, Long boardId) throws JsonProcessingException {
		// TODO Auto-generated method stub
BoardMessageForm form = new BoardMessageForm();
		
		Board getBoard = boardRepository.findNotOptionalBoardById(boardId);
		
		if (getBoard == null) {
			
			form.setMessage("게시판이 존재하지 않습니다.");
			form.setMessageType(BoardMessageType.FAIL);
			return objectMapper.writeValueAsString(form);
		}

		Account getAccount = accountRepository.findNotOptionalById(accountId);
		
		if (getAccount == null) {
			
			form.setMessage("아이디가 존재하지 않습니다.");
			form.setMessageType(BoardMessageType.FAIL);
			return objectMapper.writeValueAsString(form);
			
		}
		
		BoardManager getBoardManager = boardManagerRepository.findByBoardAndManagedBy(getBoard, getAccount);
		
		if (getBoardManager == null) {
			
			form.setMessage("관리자로 등록되지 않은 회원입니다.");
			form.setMessageType(BoardMessageType.FAIL);
			return objectMapper.writeValueAsString(form);
		}
		
		boardManagerRepository.delete(getBoardManager);
		
		getBoard.getManagers().remove(getBoardManager);
		getAccount.getManagers().remove(getBoardManager);
		
		
		form.setMessage("성공적으로 해제했습니다.");
		form.setMessageType(BoardMessageType.SUCCESS);
		return objectMapper.writeValueAsString(form);
	}

	public String deleteBoard(Long boardId) throws JsonProcessingException {
		// TODO Auto-generated method stub
		BoardMessageForm form = new BoardMessageForm();
		
		Board board = boardRepository.findBoardAndPostById(boardId);
		
		if (board == null) {
			form.setMessage("게시판이 존재하지 않습니다.");
			form.setMessageType(BoardMessageType.FAIL);
			return objectMapper.writeValueAsString(form);
		}
			
		
		Set<Post> postList = board.getPostList();
		
		List<Long> postIdList = postList.stream().map(Post::getId).collect(Collectors.toList());
		
		postRepository.updateAllByIdInQuery(postIdList, null, true);
		//postList.forEach(p -> p.updateDeleteStatusTrueAndDeleteBoard());

		Set<BoardManager> managers = board.getManagers();
		
		//List<Long> boardManagerIdList = managers.stream().map(BoardManager::getId).collect(Collectors.toList());
		
		boardManagerRepository.deleteInBatch(managers);
		//boardManagerRepository.deleteAllByBoardList(managers);		
		boardRepository.delete(board);
		
		form.setMessage("성공적으로 삭제되었습니다.");
		form.setMessageType(BoardMessageType.SUCCESS);
		
		return objectMapper.writeValueAsString(form);
	}

}
