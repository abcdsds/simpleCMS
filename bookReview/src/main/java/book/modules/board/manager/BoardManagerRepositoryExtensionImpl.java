package book.modules.board.manager;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.data.repository.support.PageableExecutionUtils;
import org.springframework.stereotype.Component;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import book.modules.account.QAccount;
import book.modules.account.form.AccountListForm;

@Component
public class BoardManagerRepositoryExtensionImpl extends QuerydslRepositorySupport implements BoardManagerRepositoryExtension{

	private final JPAQueryFactory queryFactory;
	
	public BoardManagerRepositoryExtensionImpl(JPAQueryFactory queryFactory) {
		super(BoardManager.class);
		this.queryFactory = queryFactory;
	}

	
	
	@Override
	public Page<AccountListForm> findAccountByBoardId(Long id, Pageable pageable) {
		// TODO Auto-generated method stub
		
		QAccount account = QAccount.account;
		
		QBoardManager boardmanager = QBoardManager.boardManager;
		
		List<AccountListForm> content = queryFactory.select(
							Projections.fields(AccountListForm.class,
								account.createdAt.month().as("month") , account.count().as("count")
							)
					)
					.from(boardmanager)
					.innerJoin(boardmanager.managedBy , account).fetchJoin()
					.where(boardmanager.id.eq(id))
					.fetch();
					
		JPAQuery<Long> count = queryFactory.select(boardmanager.count())
					.from(boardmanager)
					.innerJoin(boardmanager.managedBy).fetchJoin()
					.where(boardmanager.id.eq(id));
					
		
		return PageableExecutionUtils.getPage(content, pageable, count::fetchCount);
	}

}
