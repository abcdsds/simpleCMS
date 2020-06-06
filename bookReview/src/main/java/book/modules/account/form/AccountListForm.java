package book.modules.account.form;

import java.time.LocalDateTime;

import com.querydsl.core.annotations.QueryProjection;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AccountListForm {

	private Long id;

	private String nickname;
	
	private String loginId;
	
	private String email;
	
	private String role;
	
	private LocalDateTime managedAt;
	
	@QueryProjection
	public AccountListForm(Long id, String nickname, String loginId, String email, String role, LocalDateTime managedAt) {
		super();
		this.id = id;
		this.nickname = nickname;
		this.loginId = loginId;
		this.email = email;
		this.role = role;
		this.managedAt = managedAt;
	}

}
