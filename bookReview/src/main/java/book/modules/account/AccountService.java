package book.modules.account;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import book.modules.account.form.AccountForm;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccountService {

	private final AccountRepository accountRepository;

	public Account accountCreate(AccountForm form) {

		Account account = Account.builder().accountGender(form.getAccountGender()).accountType(AccountType.none)
				.ban(false).birthYear(form.getBirthYear()).email(form.getEmail()).loginId(form.getLoginId())
				.nickname(form.getNickname()).password(form.getPassword()).role(new SimpleGrantedAuthority("ROLE_USER"))
				.build();

		return accountRepository.save(account);
	}

	public void login(Account account) {
		// TODO Auto-generated method stub
		List<SimpleGrantedAuthority> list = new ArrayList<SimpleGrantedAuthority>();
		list.add(account.getRole());

		UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(new UserAccount(account),
				account.getPassword(), list);

		SecurityContextHolder.getContext().setAuthentication(token);
	}

	public Account newAccount(AccountForm form) {

		Account account = accountCreate(form);

		return account;
	}

	public Account findAccountEmail(String email) {

		Account findByEmail = accountRepository.findByEmail(email);
		
		if (findByEmail == null) {
			throw new UsernameNotFoundException(email);
		}
		return findByEmail;
	}
	
	
	
	

}
