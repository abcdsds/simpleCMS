package book.modules.board.manager;


import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import book.modules.account.Account;
import book.modules.board.Board;

@Transactional(readOnly = true)
public interface BoardManagerRepository extends JpaRepository<BoardManager, Long> , BoardManagerRepositoryExtension{

	BoardManager findByBoardAndManagedBy(Board getBoard, Account getAccount);

	@Transactional
	@Query("delete from BoardManager bm where bm.id in :ids")
	void deleteAllByBoardList(@Param("ids") Set<BoardManager> managers);

}
