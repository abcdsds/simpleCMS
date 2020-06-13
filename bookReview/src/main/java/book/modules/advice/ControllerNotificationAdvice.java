package book.modules.advice;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import book.modules.account.Account;
import book.modules.account.CurrentAccount;
import book.modules.menu.MenuService;
import book.modules.notification.NotificationService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@ControllerAdvice
public class ControllerNotificationAdvice {

	private final NotificationService notificationService;
	
	@ModelAttribute
	public void addAttributes(@CurrentAccount Account account, Model model) {
		
		if (account != null) {
			model.addAttribute("notificationList" , notificationService.getNotificationList(account));
		}
	}
}
