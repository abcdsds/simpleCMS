package book.modules.post;

import java.util.Optional;

import book.modules.post.form.PostPrevNextForm;

public interface PostRepositoryExtension {

	Optional<Post> findPostAndCommentAndPrevNextPostByIdAndDeleted(Long id, boolean deleted);
	
	PostPrevNextForm findPrevNextPostById(Long id, boolean b);
}
