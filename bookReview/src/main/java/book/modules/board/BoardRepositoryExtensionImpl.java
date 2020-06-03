package book.modules.board;

import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import com.querydsl.jpa.impl.JPAQueryFactory;


public class BoardRepositoryExtensionImpl extends QuerydslRepositorySupport implements BoardRepositoryExtension {

private final JPAQueryFactory queryFactory;
	
	public BoardRepositoryExtensionImpl(JPAQueryFactory queryFactory) {
		super(Board.class);
		this.queryFactory = queryFactory;
		// TODO Auto-generated constructor stub
	}

}
