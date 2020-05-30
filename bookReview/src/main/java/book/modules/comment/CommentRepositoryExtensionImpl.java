package book.modules.comment;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import book.modules.post.Post;


public class CommentRepositoryExtensionImpl extends QuerydslRepositorySupport implements CommentRepositoryExtension{

	public CommentRepositoryExtensionImpl(Class<?> domainClass) {
		super(Comment.class);
		// TODO Auto-generated constructor stub
	}

}
