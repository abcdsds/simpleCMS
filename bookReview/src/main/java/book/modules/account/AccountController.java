package book.modules.account;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import book.modules.account.form.AccountForm;
import book.modules.account.form.PasswordForm;
import book.modules.account.form.ProfileForm;
import book.modules.account.validator.AccountFormValidator;
import book.modules.account.validator.PasswordFormValidator;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class AccountController {

	private final AccountService accountService;
	private final ModelMapper modelMapper;
	private final AccountFormValidator accountFormValidator;
	private final PasswordFormValidator passwordFormValidator;
	
	@InitBinder("accountForm")
	public void initBinderAccountForm (WebDataBinder webDataBinder) {
		webDataBinder.addValidators(accountFormValidator);
	}
	
	@InitBinder("passwordForm")
	public void initBinderPasswordForm (WebDataBinder webDataBinder) {
		webDataBinder.addValidators(passwordFormValidator);
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
	
	@GetMapping("/account/profile")
	public String profile(@CurrentAccount Account account , Model model) {

		ProfileForm profileForm = modelMapper.map(account, ProfileForm.class);
		
		model.addAttribute(account);
		model.addAttribute(profileForm);
		
		return "account/profile-form";
	}
	
	@PostMapping("/account/profile")
	public String profileSubmit(@CurrentAccount Account account, Model model, 
								@Valid ProfileForm profileForm, Errors errors,
								RedirectAttributes redirectAttributes) {
		
		if (errors.hasErrors()) {
			model.addAttribute(account);
			return "account/profile-form";
		}
		
		accountService.updateProfile(account, profileForm);
		redirectAttributes.addFlashAttribute("message", "성공적으로 수정되었습니다.");
		return "redirect:/account/profile";
	}
	
		
	@GetMapping("/account/profile/image")
	public String profileImageForm(@CurrentAccount Account account, Model model) {
		return "account/profile-image-form";
	}
	
	@PostMapping("/account/profile/image")
	public String profileImageSubmit(@CurrentAccount Account account, Model model) {
		return "redirect:/account/profile/image";
	}
	
	@GetMapping("/account/password")
	public String passwordForm(@CurrentAccount Account account, Model model) {
		
		model.addAttribute(account);
		model.addAttribute(new PasswordForm());
		
		return "account/password-form";
	}
	
	@PostMapping("/account/password")
	public String passwordSubmit(@CurrentAccount Account account, Model model,
								 @Valid PasswordForm form , Errors errors,
								 RedirectAttributes redirectAttributes) {
		
		if (errors.hasErrors()) {
			model.addAttribute(account);
			return "account/password-form";
		}
		
		accountService.updatepassword(account,form);
		
		return "account/password-form";
	}
	
	
	@GetMapping("/account/nickname")
	public String nicknameForm(@CurrentAccount Account account, Model model) {
		return "account/nickname-form";
		
	}
	
	@GetMapping("/account/delete")
	public String accountDeleteForm(@CurrentAccount Account account, Model model) {
		
		return "account/delete-form";
	}
	
	@PostMapping("/account/delete")
	public String accountDeleteSubmit(@CurrentAccount Account account, Model model) {
		
		return "account/delete-form";
	}
	
	
}
