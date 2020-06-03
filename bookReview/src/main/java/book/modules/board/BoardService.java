package book.modules.board;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import book.modules.account.Account;
import book.modules.board.manager.BoardManager;
import book.modules.board.manager.BoardManagerRepository;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;

@Transactional
@RequiredArgsConstructor
@Service
public class BoardService {

	private final BoardRepository boardRepository;
	private final BoardManagerRepository boardManagerRepository;
	
	public void boardCreateTest(Account account) {
		//Board board = Board.builder().
		
		
		Board board = Board.builder().role(new SimpleGrantedAuthority("ROLE_USER")).name("기본게시판1").path("default").build();
				
		Board save = boardRepository.save(board);
		
		BoardManager bm = BoardManager.builder()
								.board(board).managedBy(account).managedAt(LocalDateTime.now()).build();
		
		boardManagerRepository.save(bm);

		board.getManagers().add(bm);
		
	}

	public Board getBoard(Account account, String boardPath) throws AccessDeniedException {
		// TODO Auto-generated method stub
		Optional<Board> findByIdAndRole = boardRepository.findByPathAndRole(boardPath,new SimpleGrantedAuthority(account.getRole()));
		
		Board orElseThrow = findByIdAndRole.orElseThrow(() -> new AccessDeniedException("접근할수 없는 게시판입니다."));
		
		return orElseThrow;
	}

	public void getPostList(Board board) {
		// TODO Auto-generated method stub
	}
}
