package book.modules.main;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import book.modules.account.Account;
import book.modules.account.CurrentAccount;

@Controller
public class mainController {

	@GetMapping("/")
	public String main(@CurrentAccount Account account, Model model) {
		
		if (account != null) {
			model.addAttribute(account);
		}
		
		return "index";
	}
}
