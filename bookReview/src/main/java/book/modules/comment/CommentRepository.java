package book.modules.comment;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import book.modules.post.Post;

@Transactional(readOnly = true)
public interface CommentRepository extends JpaRepository<Comment, Long> {

	List<Comment> findAllByPostAndDeleted(Post post, boolean b);

	@EntityGraph(attributePaths = {"parent","group","childList"})
	List<Comment> findAllByPostAndDeletedOrderByIdAsc(Post post, boolean b);

	@EntityGraph(attributePaths = {"parent","group","childList"})
	List<Comment> findAllByPostAndDeletedAndDepthOrderByIdAsc(Post post, boolean b, int i);

}
