package book.modules.account;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import book.modules.account.form.AccountForm;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccountService implements UserDetailsService {

	private final AccountRepository accountRepository;
	private final PasswordEncoder passwordEncoder;
	
	public Account accountCreate(AccountForm form) {

		Account account = Account.builder().accountGender(form.getAccountGender()).accountType(AccountType.none)
				.ban(false).birthYear(form.getBirthYear()).email(form.getEmail()).loginId(form.getLoginId())
				.nickname(form.getNickname()).password(passwordEncoder.encode(form.getPassword())).role("ROLE_USER")
				.build();

		return accountRepository.save(account);
	}

	public void login(Account account) {
		// TODO Auto-generated method stub
		List<SimpleGrantedAuthority> list = new ArrayList<SimpleGrantedAuthority>();
		list.add(new SimpleGrantedAuthority(account.getRole()));

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

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// TODO Auto-generated method stub
		Account findByLoginId = accountRepository.findByLoginId(username);
		
		if (findByLoginId == null) {
			throw new UsernameNotFoundException(username + " 아이디가 존재하지 않습니다.");
		}
		
		return new UserAccount(findByLoginId);
	}
	
	
	
	

}
