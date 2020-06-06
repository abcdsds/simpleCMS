package book.modules.account;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import book.modules.account.form.AccountListForm;
import book.modules.admin.form.StatisticsForm;

public interface AccountRepositoryExtension {

	List<StatisticsForm> findAllAccountMonthlyCount();
	Page<AccountListForm> findAllAccount(Pageable pageable);
	Page<AccountListForm> findAccountByBoardManagerId(Long id , Pageable pageable);
}
