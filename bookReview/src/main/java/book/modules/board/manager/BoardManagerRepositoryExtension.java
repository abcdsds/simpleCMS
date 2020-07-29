package book.modules.board.manager;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import book.modules.account.form.AccountListForm;

public interface BoardManagerRepositoryExtension {

	Page<AccountListForm> findAccountByBoardId(Long id , Pageable pageable);
}
