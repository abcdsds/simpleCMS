package book.modules.board;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import book.modules.account.QAccount;
import book.modules.account.form.AccountListForm;


public class BoardRepositoryExtensionImpl extends QuerydslRepositorySupport implements BoardRepositoryExtension {

private final JPAQueryFactory queryFactory;
	
	public BoardRepositoryExtensionImpl(JPAQueryFactory queryFactory) {
		super(Board.class);
		this.queryFactory = queryFactory;
		// TODO Auto-generated constructor stub
	}

}
