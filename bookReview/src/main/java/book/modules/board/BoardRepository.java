package book.modules.board;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface BoardRepository extends JpaRepository<Board, Long> , BoardRepositoryExtension{

	@EntityGraph(attributePaths = {"postList"})
	Optional<Board> findByIdAndRole(Long boardId, SimpleGrantedAuthority simpleGrantedAuthority);

	@EntityGraph(attributePaths = {"postList"})
	Optional<Board> findByPathAndRole(String boardPath, SimpleGrantedAuthority simpleGrantedAuthority);

	Board findByRole(SimpleGrantedAuthority simpleGrantedAuthority);

	List<Board> findTop10ByOrderByIdDesc();

	Board findByPath(String path);
	
	Optional<Board> findOptionalBoardByPath(String path);
}
