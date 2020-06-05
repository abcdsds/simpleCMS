package book.modules.account;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import book.modules.admin.form.StatisticsForm;
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

}
