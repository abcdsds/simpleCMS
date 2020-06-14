package book.modules.post;


import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import book.modules.account.Account;
import book.modules.board.Board;


@Transactional(readOnly = true)
public interface PostRepository extends JpaRepository<Post, Long> , PostRepositoryExtension{

	@EntityGraph("Post.withCommentAndParentAndGroupAndChildList")
	Optional<Post> findPostAndCommentByIdAndDeleted(Long id , boolean deleted);
	
	Post findByIdAndCreatedBy(Long id, Account account);

	Post findByIdAndCreatedByAndDeleted(Long id, Account account, boolean deleted);

	@EntityGraph(attributePaths = {"createdBy" , "comments" , "board"})
	Optional<Post> findByIdAndDeleted(Long id, boolean b);

	Post findTopByOrderByIdDesc();
	
	List<Post> findTop10ByOrderByIdDesc();

	@Transactional
	@Modifying
	@Query("update Post p set p.deleted = :deleted , p.board = :board where p.id in :ids")
	void updateAllByIdInQuery(@Param("ids") List<Long> postList , @Param("board") Long board, @Param("deleted") Boolean deleted);

	@EntityGraph(attributePaths = {"createdBy" , "board"})
	Optional<Post> findPostAndBoardAndAccountById(Long id);

	List<Post> findAllByCreatedByAndDeleted(Account account, boolean b);

	

}
