package book.modules.account;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.data.repository.support.PageableExecutionUtils;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import book.modules.account.form.AccountForm;
import book.modules.account.form.AccountListForm;
import book.modules.account.form.QAccountListForm;
import book.modules.admin.form.StatisticsForm;
import book.modules.board.QBoard;
import book.modules.board.manager.QBoardManager;
import book.modules.post.form.PostPrevNextForm;

public class AccountRepositoryExtensionImpl extends QuerydslRepositorySupport implements AccountRepositoryExtension {

	private final JPAQueryFactory queryFactory;
	
	public AccountRepositoryExtensionImpl(JPAQueryFactory queryFactory) {
		super(Account.class);
		this.queryFactory = queryFactory;
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<StatisticsForm> findAllAccountMonthlyCount() {
		// TODO Auto-generated method stub
		
		
		QAccount account = QAccount.account;
		
		List<StatisticsForm> fetch = queryFactory.select(
						Projections.fields(StatisticsForm.class,
							account.createdAt.month().as("month") , account.count().as("count")
						))
						.from(account)
						.where(account.createdAt.year().eq(LocalDateTime.now().getYear()))
						.groupBy(account.createdAt.month())
						.orderBy(account.createdAt.month().asc())
						.fetch();
		
		return fetch;
	}

	@Override
	public Page<AccountListForm> findAllAccount(Pageable pageable) {
		// TODO Auto-generated method stub
		
		QAccount account = QAccount.account;
		
		List<AccountListForm> content = queryFactory.select(
						Projections.fields(AccountListForm.class, account.id , account.nickname , account.loginId , account.email , account.role , account.createdAt.as("managedAt"))
					)
					.from(account)
					.fetch();
		
		JPAQuery<Long> count = queryFactory.select(account.count()).from(account);
		
		
		return PageableExecutionUtils.getPage(content, pageable, count::fetchCount);
	}

	@Override
	public Page<AccountListForm> findAccountByBoardManagerId(Long id, Pageable pageable) {
		// TODO Auto-generated method stub
		QAccount account = QAccount.account;
		QBoardManager boardmanager = QBoardManager.boardManager;
		QBoard board = QBoard.board;
		
		 List<AccountListForm> content = queryFactory.select(
						Projections.fields(AccountListForm.class, account.id , account.nickname , account.loginId , account.email , account.role , boardmanager.managedAt)
					)
					.from(account)
					.leftJoin(account.managers ,boardmanager)
					.leftJoin(boardmanager.board , board)
					.where(board.id.eq(id))
					.fetch();
		
		
		JPAQuery<Long> count = queryFactory.select(account.count())
					.from(account)
					.leftJoin(account.managers ,boardmanager)
					.leftJoin(boardmanager.board , board)
					.where(board.id.eq(id));
		
		return PageableExecutionUtils.getPage(content, pageable, count::fetchCount);
	}

	@Override
	public Page<AccountListForm> findAllAccount(String keyword, Pageable pageable) {
		// TODO Auto-generated method stub
		
		QAccount account = QAccount.account;
		
		List<AccountListForm> content = queryFactory.select(new QAccountListForm(account.id , account.nickname , account.loginId , account.email , account.role , account.createdAt, account.createdAt.as("managedAt")))
					.from(account)
					.where(account.nickname.containsIgnoreCase(keyword)
							.or(account.email.containsIgnoreCase(keyword))
							.or(account.loginId.containsIgnoreCase(keyword))
					).orderBy(account.createdAt.desc()).fetch();
		
		JPAQuery<Long> count = queryFactory.select(account.count())
					.from(account)
					.where(account.nickname.containsIgnoreCase(keyword)
							.or(account.email.containsIgnoreCase(keyword))
							.or(account.loginId.containsIgnoreCase(keyword))
					);
		
		return PageableExecutionUtils.getPage(content, pageable, count::fetchCount);
	}

}
