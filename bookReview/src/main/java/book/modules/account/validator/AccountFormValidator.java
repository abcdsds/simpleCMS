package book.modules.account.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import book.modules.account.AccountRepository;
import book.modules.account.form.AccountForm;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AccountFormValidator implements Validator{

	private final AccountRepository accountRepository;

	@Override
	public boolean supports(Class<?> clazz) {
		// TODO Auto-generated method stub
		return clazz.isAssignableFrom(AccountForm.class);
	}

	@Override
	public void validate(Object target, Errors errors) {
		// TODO Auto-generated method stub
		AccountForm form = (AccountForm)target;
		
		if (accountRepository.existsByNickname(form.getNickname()) ) {
			errors.rejectValue("nickname", "nickname-reduplication " , "닉네임이 중복입니다.");
		}
		
		if (accountRepository.existsByLoginId(form.getLoginId()) ) {
			errors.rejectValue("loginid", "loginid-reduplication " , "아이디가 중복입니다.");
		}
		
		if (accountRepository.existsByEmail(form.getEmail()) ) {
			errors.rejectValue("loginid", "loginid-reduplication " , "아이디가 중복입니다.");
		}
		
	}

}
