package book.modules.account;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import book.modules.account.form.AccountForm;
import book.modules.account.form.PasswordForm;
import book.modules.account.form.ProfileForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AccountService implements UserDetailsService {

	private final AccountRepository accountRepository;
	private final PasswordEncoder passwordEncoder;
	private final ModelMapper modelMapper;
	
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

	public void updateProfile(Account account, ProfileForm profileForm) {
		// TODO Auto-generated method stub
		log.info("Gender {} ", profileForm.getAccountGender());
		log.info("Year {} ", profileForm.getBirthYear());
		
		modelMapper.map(profileForm, account);
		
		log.info("Account Gender {} ", account.getAccountGender());
		log.info("Account Year {} ", account.getBirthYear());
		
		accountRepository.save(account);
		
	}

	public void updatepassword(Account account, PasswordForm form) {
		// TODO Auto-generated method stub
		account.changePassword(passwordEncoder.encode(form.getNewPassword()));
		accountRepository.save(account);
		
	}
	
	
	
	

}
