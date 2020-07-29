package book.modules.account;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface AccountRepository extends JpaRepository<Account, Long> , AccountRepositoryExtension{

	Account findByEmail(String email);

	boolean existsByNickname(String nickname);

	boolean existsByLoginId(String loginId);

	boolean existsByEmail(String email);

	Account findByLoginId(String loginId);

	List<Account> findTop10ByOrderByIdDesc();

	Account findNotOptionalById(Long accountId);

	Account findByNickname(String nickname);
}
