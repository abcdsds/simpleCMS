package book.modules.notification;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import book.modules.account.Account;

public interface NotificationRepository extends JpaRepository<Notification, Long>{

	Optional<Notification> findByAccount(Account account);
	
	List<Notification> findTop5ByAccountAndCheckedOrderByIdDesc(Account account, Boolean checked);

	List<Notification> findAllByAccountAndChecked(Account account, boolean b);

//	@Transactional
//	@Modifying
//	@Query("update Post p set p.deleted = :deleted , p.board = :board where p.id in :ids")
	
	@Transactional
	@Modifying
	@Query("update Notification n set n.checked = :checked where n.id in :ids")
	void updateAllByIdInQuery(@Param("ids") List<Long> notificationList , @Param("checked") Boolean checked);
	
}
