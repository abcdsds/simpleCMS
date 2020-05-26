package book.modules.account.form;


import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import book.modules.account.AccountGender;
import lombok.Data;

@Data
public class ProfileForm {

	@NotNull
	@Min(value = 1950)
	@Max(value = 2100)
	private int birthYear;
	
	@NotNull
	private AccountGender accountGender = AccountGender.female;
}
