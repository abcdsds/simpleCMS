package book.modules.post;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import book.modules.post.form.PostListForm;
import book.modules.post.form.PostPrevNextForm;

public interface PostRepositoryExtension {

	Optional<Post> findPostAndCommentAndPrevNextPostByIdAndDeleted(Long id, boolean deleted);
	
	PostPrevNextForm findPrevNextPostById(Long id, boolean deleted);
	
	Page<PostListForm> findAllPostByBoardAndDeleted(String boardName , boolean deleted, Pageable pageable);
	
	Page<PostListForm> findAllPostByBoardAndDeletedWithKeyword(String keyword,String boardName , boolean deleted, Pageable pageable);
}
