package book.modules.post;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import book.modules.account.QAccount;
import book.modules.comment.QComment;
import book.modules.post.form.PostPrevNextForm;

@Repository
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

}
