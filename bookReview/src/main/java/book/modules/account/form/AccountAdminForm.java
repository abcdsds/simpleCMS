package book.modules.account.form;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;

import book.modules.account.AccountGender;
import lombok.Data;

@Data
public class AccountAdminForm {

	private Long id;
	
	private String loginId;
	
	private String email;
	
	@NotEmpty
	@Length(min = 2 , max = 12)
	@Pattern(regexp = "^[ㄱ-ㅎ가-힣a-z0-9_-]{2,12}$")
	private String nickname;
		
	@NotNull
	private int birthYear;
	
	@NotNull
	private AccountGender accountGender;
	
	@NotNull
	private String role;
}
