package book.modules.notification;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import book.exception.NotFoundIdException;
import book.modules.account.Account;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;


@Transactional
@Service
@RequiredArgsConstructor
public class NotificationService {

	private final NotificationRepository notificationRepository;

	public Notification getNotification(Account account) {

		if (account == null) {
			return null;
		}

		Notification notification = notificationRepository.findByAccount(account)
										.orElseThrow(() -> new NotFoundIdException("아이디가 존재하지 않습니다."));

		return notification;
	}
	
	public List<Notification> getNotificationList(Account account) {
		
		return notificationRepository.findTop5ByAccountAndCheckedOrderByIdDesc(account, false);
	}

	public void updateCheck(Long id) {
		// TODO Auto-generated method stub
		Notification notification = notificationRepository.findById(id).orElseThrow(() -> new NotFoundIdException("알림이 존재하지 않습니다."));
		notification.updateChecked(true);
	}

	public List<Notification> getList(Account account, boolean b) {
		// TODO Auto-generated method stub
		return notificationRepository.findAllByAccountAndChecked(account,b);
	}

	public void allRead(Account account) {
		// TODO Auto-generated method stub
		List<Notification> list = getList(account,false);
		List<Long> notificationIdList = list.stream().map(Notification::getId).collect(Collectors.toList());
		notificationRepository.updateAllByIdInQuery(notificationIdList, true);
	}
}
