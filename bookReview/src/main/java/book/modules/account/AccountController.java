package book.modules.account;

import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import book.modules.account.form.AccountForm;
import book.modules.account.validator.AccountFormValidator;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/account")
@RequiredArgsConstructor
public class AccountController {

	private final AccountService accountService;
	private final AccountFormValidator accountFormValidator;
	
	@InitBinder("accountForm")
	public void initBinder(WebDataBinder webDataBinder) {
		webDataBinder.addValidators(accountFormValidator);
	}
	
	@GetMapping("/register")
	public String registerForm(Model model) {
		
		model.addAttribute(new AccountForm());
		return "account/register";
	}
	
	@PostMapping("/register")
	public String registerSubmit(@Valid AccountForm accountForm, Errors errors) {
		
		if (errors.hasErrors()) {
			return "account/register";
		}
		
		Account newAccount = accountService.newAccount(accountForm);
		accountService.login(newAccount);
		
		return "redirect:/";
	}
	
}
