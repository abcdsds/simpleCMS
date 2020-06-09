package book.modules.comment;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.data.querydsl.QuerydslUtils;
import org.springframework.data.repository.support.PageableExecutionUtils;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import book.modules.account.QAccount;
import book.modules.admin.form.StatisticsForm;
import book.modules.board.QBoard;
import book.modules.comment.form.CommentListForm;
import book.modules.comment.form.QCommentListForm;
import book.modules.post.Post;
import book.modules.post.QPost;


public class CommentRepositoryExtensionImpl extends QuerydslRepositorySupport implements CommentRepositoryExtension{

	private final JPAQueryFactory queryFactory;
	
	public CommentRepositoryExtensionImpl(JPAQueryFactory queryFactory) {
		super(Comment.class);
		this.queryFactory = queryFactory;
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<StatisticsForm> findAllCommentMonthlyCount() {
		// TODO Auto-generated method stub
		QComment comment = QComment.comment;
		
		List<StatisticsForm> fetch = queryFactory.select(
				Projections.fields(StatisticsForm.class,
						comment.createdAt.month().as("month") , comment.count().as("count")
				))
				.from(comment)
				.where(comment.createdAt.year().eq(LocalDateTime.now().getYear()))
				.groupBy(comment.createdAt.month())
				.orderBy(comment.createdAt.month().asc())
				.fetch();
		
		return fetch;
	}

	@Override
	public Page<CommentListForm> findAllComment(String keyword, Pageable pageable) {
		// TODO Auto-generated method stub
		QAccount account = QAccount.account;
		QPost post = QPost.post;
		QComment comment = QComment.comment;
		
		List<CommentListForm> content = queryFactory.select(
						new QCommentListForm(
								comment.id, post.id.as("postId") , comment.content , comment.createdAt , account.nickname.as("createdBy"))
					)
					.from(comment)
					.leftJoin(comment.createdBy, account)
					.leftJoin(comment.post, post)
					.where(comment.content.containsIgnoreCase(keyword))
					.orderBy(comment.createdAt.desc())
					.fetch();
		
		JPAQuery<Long> count = queryFactory.select(comment.count())
					.from(comment)
					.leftJoin(comment.createdBy, account)
					.leftJoin(comment.post, post)
					.where(comment.content.containsIgnoreCase(keyword));
		
		
		return PageableExecutionUtils.getPage(content, pageable, count::fetchCount);
	}

}
