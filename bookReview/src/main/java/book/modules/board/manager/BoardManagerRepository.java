package book.modules.board.manager;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface BoardManagerRepository extends JpaRepository<BoardManager, Long> , BoardManagerRepositoryExtension{

}
