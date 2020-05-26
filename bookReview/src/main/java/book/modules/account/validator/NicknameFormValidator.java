package book.modules.account.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import book.modules.account.AccountRepository;
import book.modules.account.form.NicknameForm;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class NicknameFormValidator implements Validator{
	
	private final AccountRepository accountRepositroy;
	
	@Override
	public boolean supports(Class<?> clazz) {
		// TODO Auto-generated method stub
		return clazz.isAssignableFrom(NicknameForm.class);
	}

	@Override
	public void validate(Object target, Errors errors) {
		// TODO Auto-generated method stub
		NicknameForm form = (NicknameForm)target;
		
		if ( accountRepositroy.existsByNickname(form.getNickname()) ) {
			errors.rejectValue("nickname", "nickname-reduplication " , "닉네임이 중복입니다.");
		}
		
		
	}

}
