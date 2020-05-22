package book.modules.account.form;

import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;

import book.modules.account.AccountGender;
import lombok.Data;

@Data
public class AccountForm {

	@Length(min = 6 , max = 12)
	@Pattern(regexp = "^[A-Za-z0-9_-]{6,12}$")
	private String loginId;
	
	@Pattern(regexp = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\\\.[A-Z]{2,6}$")
	private String email;
	
	@Length(min = 2 , max = 12)
	@Pattern(regexp = "^[ㄱ-ㅎ가-힣a-z0-9_-]{2,12}$")
	private String nickname;
	
	@Length(min = 6 , max = 15)
	private String password;
	
	private int birthYear;
	
	private AccountGender accountGender;
	
}
