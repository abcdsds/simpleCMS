package book.modules.post;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import book.modules.account.Account;


@Transactional(readOnly = true)
public interface PostRepository extends JpaRepository<Post, Long> , PostRepositoryExtension{

	@EntityGraph("Post.withCommentAndParentAndGroupAndChildList")
	Optional<Post> findPostAndCommentByIdAndDeleted(Long id , boolean deleted);
	
	Post findByIdAndCreatedBy(Long id, Account account);

	Post findByIdAndCreatedByAndDeleted(Long id, Account account, boolean deleted);

	Optional<Post> findByIdAndDeleted(Long id, boolean b);

	Post findTopByOrderByIdDesc();
	
	List<Post> findTop10ByOrderByIdDesc();
	

}
