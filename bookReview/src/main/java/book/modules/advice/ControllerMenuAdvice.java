package book.modules.advice;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import book.modules.account.Account;
import book.modules.account.CurrentAccount;
import book.modules.menu.MenuService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@ControllerAdvice(basePackages = {"book.modules.account","book.modules.post", "book.modules.board", "book.modules.main" , "book.modules.notification"})
public class ControllerMenuAdvice {

	private final MenuService menuService;
	
	@ModelAttribute
	public void addAttributes(@CurrentAccount Account account, Model model) {
		model.addAttribute("menus" , menuService.getMenuWithSubmenu(account));
	}
}
