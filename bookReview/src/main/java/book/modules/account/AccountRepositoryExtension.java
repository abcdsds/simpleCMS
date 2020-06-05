package book.modules.account;

import java.util.List;

import book.modules.admin.form.StatisticsForm;

public interface AccountRepositoryExtension {

	List<StatisticsForm> findAllAccountMonthlyCount();
}
