package book.modules.board.manager;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import book.modules.account.Account;
import book.modules.board.Board;

@Transactional(readOnly = true)
public interface BoardManagerRepository extends JpaRepository<BoardManager, Long> , BoardManagerRepositoryExtension{

	BoardManager findByBoardAndManagedBy(Board getBoard, Account getAccount);

}
