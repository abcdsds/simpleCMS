package book.modules.account;

import java.util.Arrays;

import org.springframework.security.core.userdetails.User;

import lombok.Getter;

@Getter
public class UserAccount extends User{

	private Account account;
	
	public UserAccount(Account account) {
		super(account.getLoginId(), account.getPassword(), Arrays.asList(account.getRole()));
		// TODO Auto-generated constructor stub
		this.account = account;
	}
	
	
}
