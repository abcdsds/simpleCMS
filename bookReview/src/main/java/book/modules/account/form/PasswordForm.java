package book.modules.account.form;

import javax.validation.constraints.NotEmpty;

import org.hibernate.validator.constraints.Length;

import lombok.Data;

@Data
public class PasswordForm {

	private Long id;
	@NotEmpty
	@Length(min = 6 , max = 15)
	private String newPassword;
	
	@NotEmpty
	@Length(min = 6 , max = 15)
	private String newPasswordConfirm;
}
