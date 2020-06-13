package book.modules.notification;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import book.modules.account.Account;
import book.modules.account.CurrentAccount;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/notification")
@Controller
public class NotificationController {

	private final NotificationService notificationService;
	
	@GetMapping("/post/view/{viewId}")
	public String notificationMove(@PathVariable Long viewId , Long id) {
		
		notificationService.updateCheck(id);
		
		return "redirect:/post/view/" + viewId;
	}
	
	@GetMapping("/unread")
	public String notificationUnreadList(@CurrentAccount Account account , Model model) {
		
		List<Notification> list = notificationService.getList(account,false);
		
		model.addAttribute("notifications", list);
		model.addAttribute("readcheck", "unread");
		
		return "notification/notification";
	}
	
	@GetMapping("/read")
	public String notificationReadList(@CurrentAccount Account account , Model model) {
		
		List<Notification> list = notificationService.getList(account,true);
		
		model.addAttribute("notifications", list);
		model.addAttribute("readcheck", "read");
		
		return "notification/notification";
	}
	
	@GetMapping("/allread")
	public String notificationAllRead(@CurrentAccount Account account , Model model) {
		
		notificationService.allRead(account);
		
		return "redirect:/notification/unread";
	}
}
