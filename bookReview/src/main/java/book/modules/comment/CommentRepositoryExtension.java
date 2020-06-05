package book.modules.comment;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import book.modules.admin.form.StatisticsForm;
import book.modules.post.Post;

@Transactional(readOnly = true)
public interface CommentRepositoryExtension {
	
	List<StatisticsForm> findAllCommentMonthlyCount();
}
