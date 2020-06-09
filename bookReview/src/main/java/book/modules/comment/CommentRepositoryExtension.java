package book.modules.comment;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import book.modules.admin.form.StatisticsForm;
import book.modules.comment.form.CommentForm;
import book.modules.comment.form.CommentListForm;

@Transactional(readOnly = true)
public interface CommentRepositoryExtension {
	
	List<StatisticsForm> findAllCommentMonthlyCount();
	
	Page<CommentListForm> findAllComment(String keyword, Pageable pageable);
}
