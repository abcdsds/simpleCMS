package book.modules.account.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import book.modules.account.Account;
import book.modules.account.AccountRepository;
import book.modules.account.form.AccountAdminForm;
import book.modules.account.form.AccountForm;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AccountAdminFormValidator implements Validator{

	private final AccountRepository accountRepository;

	@Override
	public boolean supports(Class<?> clazz) {
		// TODO Auto-generated method stub
		return clazz.isAssignableFrom(AccountAdminForm.class);
	}

	@Override
	public void validate(Object target, Errors errors) {
		// TODO Auto-generated method stub
		AccountAdminForm form = (AccountAdminForm)target;
				
		
		Account findByNickname = accountRepository.findByNickname(form.getNickname());
		
		if (findByNickname != null ) {
			
			if (!findByNickname.getId().equals(form.getId())) {
				errors.rejectValue("nickname", "nickname-reduplication " , "닉네임이 중복입니다.");
			}
		}
		

	}

}
