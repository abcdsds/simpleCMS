package book.modules.account.validator;


import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import book.modules.account.form.PasswordForm;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class PasswordFormValidator implements Validator{

	@Override
	public boolean supports(Class<?> clazz) {
		// TODO Auto-generated method stub
		return clazz.isAssignableFrom(PasswordForm.class);
	}

	@Override
	public void validate(Object target, Errors errors) {
		// TODO Auto-generated method stub
		PasswordForm passwordForm = (PasswordForm) target;
		log.info("password {}" , passwordForm.getNewPassword());
		log.info("passwordConfirm {}" , passwordForm.getNewPasswordConfirm());
		
		if (!passwordForm.getNewPassword().equals(passwordForm.getNewPasswordConfirm())) {
			errors.rejectValue("newPassword", "wrong-password" ,  "패스워드가 일치하지 않음");
		}
		
	}
}
