package book.modules.post;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.data.repository.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import book.modules.account.QAccount;
import book.modules.admin.form.StatisticsForm;
import book.modules.board.QBoard;
import book.modules.comment.QComment;
import book.modules.post.form.PostListForm;
import book.modules.post.form.PostPrevNextForm;
import book.modules.post.form.QPostListForm;

public class PostRepositoryExtensionImpl extends QuerydslRepositorySupport implements PostRepositoryExtension {

	private final JPAQueryFactory queryFactory;
	
	public PostRepositoryExtensionImpl(JPAQueryFactory queryFactory) {
		super(Post.class);
		this.queryFactory = queryFactory;
		// TODO Auto-generated constructor stub
	}

	@Override
	public Optional<Post> findPostAndCommentAndPrevNextPostByIdAndDeleted(Long id, boolean deleted) {
		// TODO Auto-generated method stub
		
		QPost post = QPost.post;
		
		Post fetchOne = from(post).where(post.deleted.isFalse()
							.and(post.id.eq(id))
						).leftJoin(post.comments , QComment.comment).fetchJoin()
						 .leftJoin(post.createdBy, QAccount.account).fetchJoin()
						 .fetchOne();
		
		return Optional.ofNullable(fetchOne);
	}

	//query dsl subquery 에서 limit를 지원하지않음 그래서 폐기
	@Override
	public PostPrevNextForm findPrevNextPostById(Long id, boolean b) {
		// TODO Auto-generated method stub
		QPost post = new QPost("post");
		QPost postSub = new QPost("postSub");
		QPost postSecondSub = new QPost("postSecondSub");
		
		//query dsl subquery 에서 limit를 지원하지않음 그래서 폐기
		//		PostPrevNextForm fetchOne = (PostPrevNextForm) queryFactory.select(
		//						ExpressionUtils.as(
		//								JPAExpressions.select(postSub.id)
		//												.from(postSub)
		//												.where(postSub.id.gt(id))
		//							    , "nextPostId") ,
		//						ExpressionUtils.as(
		//								JPAExpressions.select(postSecondSub.id)
		//												.from(postSecondSub)
		//												.where(postSecondSub.id.lt(id))
		//							    , "prevPostId")
		//					).from(post).limit(1).fetchOne();
		
		PostPrevNextForm fetchOne = queryFactory.select(
								Projections.fields(PostPrevNextForm.class,postSub.id.as("prevPostId") , postSecondSub.id.as("nextPostId")))
					.from(post)
					.leftJoin(postSub).on(postSub.id.lt(post.id).and(postSub.deleted.eq(false)))
					.leftJoin(postSecondSub).on(postSecondSub.id.gt(post.id).and(postSecondSub.deleted.eq(false)))
					.where(post.id.eq(id)
						  )
					.orderBy(postSub.id.desc() , postSecondSub.id.asc())
					.limit(1)
					.distinct()
					.fetchOne();
		
		
		
		return fetchOne;
	}

	@Override
	public Page<PostListForm> findAllPostByBoardAndDeleted(String boardPath, boolean deleted, Pageable pageable) {
		// TODO Auto-generated method stub
		
		QPost post = QPost.post;
		QAccount account = QAccount.account;
		QBoard board = QBoard.board;
		
		List<PostListForm> content = queryFactory.select(new QPostListForm(
								post.id,
								post.title,
								post.createdAt,
								account.nickname,
								post.up,
								post.views								
							)
				
					).from(post)
					 .leftJoin(post.createdBy , account)
					 .leftJoin(post.board, board)
					 .where(
							 post.board.path.eq(boardPath),
							 post.deleted.eq(deleted)
							 
					 )
					 .offset(pageable.getOffset())
					 .limit(pageable.getPageSize())
					 .fetch();
		
		JPAQuery<Post> count = queryFactory.select(post)
					.from(post)
					.leftJoin(post.createdBy , account)
					 .leftJoin(post.board, board)
					 .where(
							 post.board.path.eq(boardPath),
							 post.deleted.eq(deleted)
							 
					 );

		return PageableExecutionUtils.getPage(content, pageable, count::fetchCount);
	}

	@Override
	public Page<PostListForm> findAllPostByBoardAndDeletedWithKeyword(String keyword,String boardPath, boolean deleted,
			Pageable pageable) {
		// TODO Auto-generated method stub
		QPost post = QPost.post;
		QAccount account = QAccount.account;
		QBoard board = QBoard.board;
		
		List<PostListForm> content = queryFactory.select(new QPostListForm(
								post.id,
								post.title,
								post.createdAt,
								account.nickname,
								post.up,
								post.views								
							)
				
					).from(post)
					 .leftJoin(post.createdBy , account)
					 .leftJoin(post.board, board)
					 .where(
							 post.title.containsIgnoreCase(keyword)
							 .or(post.content.containsIgnoreCase(keyword)),
							 post.board.path.eq(boardPath),
							 post.deleted.eq(deleted)
							 
					 )
					 .offset(pageable.getOffset())
					 .limit(pageable.getPageSize())
					 .fetch();
		
		JPAQuery<Post> count = queryFactory.select(post)
					.from(post)
					.leftJoin(post.createdBy , account)
					 .leftJoin(post.board, board)
					 .where(
							 post.title.containsIgnoreCase(keyword)
							 .or(post.content.containsIgnoreCase(keyword)),
							 post.board.path.eq(boardPath),
							 post.deleted.eq(deleted)
							 
					 );

		return PageableExecutionUtils.getPage(content, pageable, count::fetchCount);
	}

	@Override
	public List<StatisticsForm> findAllPostMonthlyCount() {
		// TODO Auto-generated method stub
		QPost post = QPost.post;
		
		List<StatisticsForm> fetch = queryFactory.select(
				Projections.fields(StatisticsForm.class,
						post.createdAt.month().as("month") , post.count().as("count")
				))
				.from(post)
				.where(post.createdAt.year().eq(LocalDateTime.now().getYear()))
				.groupBy(post.createdAt.month())
				.orderBy(post.createdAt.month().asc())
				.fetch();
		
		return fetch;
	}

	@Override
	public Page<PostListForm> findAllPost(String keyword, Pageable pageable) {
		// TODO Auto-generated method stub
		QPost post = QPost.post;
		QAccount account = QAccount.account;
		QBoard board = QBoard.board;
		
		List<PostListForm> content = queryFactory.select(
								new QPostListForm(
										post.id,
										post.title,
										post.createdAt,
										account.nickname,
										post.up,
										post.views,
										board.name
								))
								.from(post)
								.leftJoin(post.createdBy , account)
								.leftJoin(post.board, board)
								.where(post.title.containsIgnoreCase(keyword).or(post.content.containsIgnoreCase(keyword)))
								.orderBy(post.createdAt.desc())
								.fetch();
		
		
		JPAQuery<Long> count = queryFactory.select(post.count())
					.from(post)
					.where(post.title.containsIgnoreCase(keyword)
						  .or(post.content.containsIgnoreCase(keyword))
					);
		
		return PageableExecutionUtils.getPage(content, pageable, count::fetchCount);
	}

	@Override
	public Optional<Post> findPostDataById(Long id, boolean deleted) {
		// TODO Auto-generated method stub
		
		QPost post = QPost.post;
		QComment comment = QComment.comment;
		QComment commentChild = new QComment("commentChild");
		QAccount account = QAccount.account;
		QAccount commentAccount = new QAccount("commentAccount");
		QAccount commentChildAccount = new QAccount("commentChildAccount");
		QBoard board = QBoard.board;
		
		Post content = queryFactory.select(post)
					.from(post)
					.leftJoin(post.board, board).fetchJoin()
					.leftJoin(post.createdBy, account).fetchJoin()
					.leftJoin(post.comments, comment).fetchJoin()
					.leftJoin(comment.createdBy, commentAccount).fetchJoin()
					.leftJoin(comment.childList, commentChild).fetchJoin()
					.leftJoin(commentChild.createdBy, commentChildAccount).fetchJoin()
					.where(post.id.eq(id).and(post.deleted.eq(deleted)))
					.fetchOne();
					
		return Optional.ofNullable(content);
	}

}
