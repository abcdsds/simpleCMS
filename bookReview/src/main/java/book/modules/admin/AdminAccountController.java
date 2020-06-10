package book.modules.admin;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.resource.HttpResource;

import com.fasterxml.jackson.core.JsonProcessingException;

import book.modules.account.Account;
import book.modules.account.AccountService;
import book.modules.account.form.AccountAdminForm;
import book.modules.account.form.AccountListForm;
import book.modules.account.validator.AccountAdminFormValidator;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestMapping("/admin")
@RequiredArgsConstructor
@Controller
public class AdminAccountController {

	private final AccountService accountService;
	private final AdminService adminService;
	private final ModelMapper modelMapper;
	private final AccountAdminFormValidator accountAdminFormValidator; 
	
	@InitBinder("accountAdminForm")
	public void initBinderBoardForm (WebDataBinder webDataBinder) {
		webDataBinder.addValidators(accountAdminFormValidator);
	}
	
	@GetMapping("/account")
	public String adminAccountManagement(Model model, @PageableDefault(page = 0, size = 10, sort = "createdAt" , direction = Sort.Direction.DESC) Pageable pageable, @RequestParam (defaultValue = "") String keyword) {
		
		Page<AccountListForm> accountPaging = adminService.getAccountPaging(keyword , pageable);
		
		model.addAttribute("resultList", accountPaging);
		
		return "admin/account/account";
	}
	
	@GetMapping("/account/update/{id}")
	public String adminAccountManageUpdateForm(Model model, @PathVariable Long id) throws NotFoundException {
		
		Account account = accountService.getAccount(id);
				
		model.addAttribute(modelMapper.map(account, AccountAdminForm.class));
		
		return "admin/account/update";
	}
	
	@PostMapping("/account/update")
	public String adminAccountManageUpdateSubmit(Model model , @Valid AccountAdminForm accountAdminForm, Errors errors , RedirectAttributes redirect) throws NotFoundException {
		
		
		
		if (errors.hasErrors()) {
			log.info("========================실패");
			//Account account = accountService.getAccount(accountAdminForm.getId());
			//model.addAttribute(modelMapper.map(account, AccountAdminForm.class));
			model.addAttribute("meesage", "수정에 실패했습니다.");
			return "admin/account/update";
		}
		
		log.info("================= {} " + accountAdminForm.getEmail());
		log.info("================= {} " + accountAdminForm.getLoginId());
		
		adminService.updateAccount(accountAdminForm);
		
		redirect.addFlashAttribute("message", "성공적으로 변경되었습니다.");
		log.info("========================성공");
		return "redirect:/admin/account/update/" + accountAdminForm.getId();
	}
	
	@PostMapping("/account/delete")
	@ResponseBody
	public ResponseEntity<String> adminMenuManagerDeleteSutmit(Long accountId, HttpServletRequest request, HttpServletResponse response) throws JsonProcessingException {

		
		String message = accountService.deleteAccountWithId(accountId);
		
		accountService.logout(request, response);
		
		return new ResponseEntity<>(message , HttpStatus.OK);
	}
}
