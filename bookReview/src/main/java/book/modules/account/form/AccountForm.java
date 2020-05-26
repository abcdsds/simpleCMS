package book.modules.account.form;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;

import book.modules.account.AccountGender;
import lombok.Data;

@Data
public class AccountForm {

	@NotEmpty
	@Length(min = 6 , max = 12)
	@Pattern(regexp = "^[A-Za-z0-9_-]{6,12}$")
	private String loginId;
	
	@NotEmpty
	@Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}")
	private String email;
	
	@NotEmpty
	@Length(min = 2 , max = 12)
	@Pattern(regexp = "^[ㄱ-ㅎ가-힣a-z0-9_-]{2,12}$")
	private String nickname;
	
	@NotEmpty
	@Length(min = 6 , max = 15)
	private String password;
	
	@NotNull
	private int birthYear;
	
	@NotNull
	private AccountGender accountGender;
	
}
