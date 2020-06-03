package book.modules.account;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sun.media.sound.InvalidDataException;

import book.modules.account.form.AccountForm;
import book.modules.account.form.NicknameForm;
import book.modules.account.form.PasswordForm;
import book.modules.account.form.ProfileForm;
import book.modules.account.validator.AccountFormValidator;
import book.modules.account.validator.PasswordFormValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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
		redirectAttributes.addFlashAttribute("message", "비밀번호가 성공적으로 변경되었습니다.");
		
		return "redirect:/account/password";
	}
	
	
	@GetMapping("/account/nickname")
	public String nicknameForm(@CurrentAccount Account account, Model model) {
		
		NicknameForm nicknameForm = modelMapper.map(account, NicknameForm.class);
		model.addAttribute(account);
		model.addAttribute(nicknameForm);
		
		return "account/nickname-form";
	}
	
	@PostMapping("/account/nickname")
	public String nicknameSubmit(@CurrentAccount Account account, Model model,
								 @Valid NicknameForm form , Errors errors,
								 RedirectAttributes redirectAttributes) {
		
		if (errors.hasErrors()) {
			model.addAttribute(account);
			return "account/nickname-form";
		}
		
		accountService.updateNickname(account, form);
		redirectAttributes.addFlashAttribute("message", "닉네임이 성공적으로 변경되었습니다.");
		
		return "redirect:/account/nickname";
	}
	
	@GetMapping("/account/leave")
	public String accountDeleteForm(@CurrentAccount Account account, Model model) {
		
		model.addAttribute(account);
		return "account/delete-form";
	}
	
	
	@PostMapping("/account/leave")
	public String accountDeleteSubmit(@CurrentAccount Account account, Model model,
									  HttpServletRequest request, HttpServletResponse response) {
		
		accountService.deleteAccount(account);
		accountService.logout(request, response);
		return "redirect:/";
	}
	
	
	@GetMapping("/account/profile-image")
	public String profileImageForm(@CurrentAccount Account account, Model model) {
		
		model.addAttribute(account);
		
		return "account/profile-image-form";
	}
	
	@PostMapping("/account/profile-image")
	public String profileImageSubmit(@CurrentAccount Account account, Model model, String profileImage,
										RedirectAttributes redirectAttributes) {
		
		
		log.info("null check {}" , profileImage == null);
		
		String message = "";
		
		if(profileImage.startsWith("data:image") && profileImage != null ) {
			accountService.updateProfileImage(account, profileImage);
			
			message = "프로필 이미지가 수정되었습니다.";
		}else {
			message = "프로필 이미지 수정이 실패했습니다.";
		}
		
		redirectAttributes.addFlashAttribute("message", message);
		
		return "redirect:/account/profile-image";
	}
	
	@GetMapping("/find-password")
	public String findPasswordForm(Model model) {
		return "account/find-password";
	}
		
	@PostMapping("/find-password")
	public String findPasswordSubmit(Model model, String email, RedirectAttributes redirect) {
		
		Account account = accountService.checkId(email);
		
		if (account == null) {
			model.addAttribute("message" , "일치하는 이메일이 존재하지 않습니다.");
			return "account/find-password";
		}
		accountService.sendPasswordMail(account, email);
		redirect.addFlashAttribute("message" , "메일이 발송되었습니다.");
		
		return "redirect:/find-password";
	}
	
	@GetMapping("/check-email-token")
	public String checkEmailToken(Model model ,@RequestParam(name = "token") String token , @RequestParam(name="email") String email) throws InvalidDataException {
		
		accountService.validToken(email, token);
		model.addAttribute("token" , token);
		model.addAttribute("email" , email);
		model.addAttribute(new PasswordForm());
		return "mail/changePassword";
	}
	
	@PostMapping("/change-password")
	public String changePasswordWithToken(Model model,@RequestParam(name = "token") String token , @RequestParam(name="email") String email, @Valid PasswordForm form , Errors errors,
			 							  RedirectAttributes redirectAttributes) throws InvalidDataException {
		if (errors.hasErrors()) {
			model.addAttribute("message" , "수정에 실패했습니다.");
			return "mail/changePassword";
		}
		
		accountService.changePasswordWithToken(token,email,form);
		
		return "redirect:/login";
	}
	
}
