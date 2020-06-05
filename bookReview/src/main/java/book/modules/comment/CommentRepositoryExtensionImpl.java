package book.modules.comment;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import book.modules.admin.form.StatisticsForm;
import book.modules.post.Post;


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

}
