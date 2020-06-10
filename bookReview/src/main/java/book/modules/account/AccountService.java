package book.modules.account;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.media.sound.InvalidDataException;

import book.config.AppProperties;
import book.mail.Email;
import book.mail.EmailService;
import book.modules.account.form.AccountForm;
import book.modules.account.form.AccountMessageForm;
import book.modules.account.form.NicknameForm;
import book.modules.account.form.PasswordForm;
import book.modules.account.form.ProfileForm;
import book.modules.simple.SimpleMessageType;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AccountService implements UserDetailsService {

	private final AccountRepository accountRepository;
	private final EmailService emailService;
	private final PasswordEncoder passwordEncoder;
	private final ModelMapper modelMapper;
	private final AppProperties appProperties;
	private final TemplateEngine templateEngine;
	private final ObjectMapper objectMapper;
	
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
	public UserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {
		// TODO Auto-generated method stub
		Account findByLoginId = accountRepository.findByLoginId(loginId);
		
		if (findByLoginId == null) {
			throw new UsernameNotFoundException(loginId + " 아이디가 존재하지 않습니다.");
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

	public void updateNickname(Account account, NicknameForm form) {
		// TODO Auto-generated method stub
		account.changeNickname(form.getNickname());
		accountRepository.save(account);
	}

	public void deleteAccount(Account account) {
		// TODO Auto-generated method stub
		account.updateDeleted();
		//accountRepository.delete(account);
	}

	public void logout(HttpServletRequest request, HttpServletResponse response) {
		// TODO Auto-generated method stub
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if(authentication != null) {
			new SecurityContextLogoutHandler().logout(request, response, authentication);
		}
	}

	public void updateProfileImage(Account account, String profileImage) {
		// TODO Auto-generated method stub
		account.changeProfileImage(profileImage);
		accountRepository.save(account);
	}

	public Account checkId(String email) {
		// TODO Auto-generated method stub
		return accountRepository.findByEmail(email);
	}

	public void sendPasswordMail(Account account, String email) {
		// TODO Auto-generated method stub
		
		account.generateEmailCheckToken();
		
		Context context = new Context();
		context.setVariable("link", "/check-email-token?token="+account.getToken()+"&email="+account.getEmail());
		context.setVariable("nickname", account.getNickname());
		context.setVariable("linkName", "이메일 인증하기");
		context.setVariable("message", "서비스를 사용하려면 링크를 클릭하세요.");
		context.setVariable("host", appProperties.getHost());
		
		String message = templateEngine.process("mail/page", context);
		
		Email emailMessage = Email.builder()
										.to(account.getEmail())
										.subject("비밀번호 변경 인증 메일")
										.message(message)
										.build();										
		
		emailService.sendEmail(emailMessage);
		
		accountRepository.save(account);
	}

	public Account validToken(String email, String token) throws InvalidDataException {
		// TODO Auto-generated method stub
		Account account = accountRepository.findByEmail(email);
		
		if (account == null) {
			throw new UsernameNotFoundException("아이디가 존재하지 않습니다.");
		}
		
		
		if (!account.getToken().equals(token)) {
			throw new InvalidDataException("토큰이 일치하지 않습니다.");
		}
		
		return account;
	}

	public void changePasswordWithToken(String token, String email, PasswordForm form) throws InvalidDataException {
		// TODO Auto-generated method stub
		Account account = validToken(email,token);
		account.changePassword(passwordEncoder.encode(form.getNewPassword()));
		account.generateEmailCheckToken();
	
	}

	public Account getAccount(Long id) throws NotFoundException {
		// TODO Auto-generated method stub
		return accountRepository.findById(id).orElseThrow(() -> new NotFoundException("아이디가 존재하지 않습니다."));
	}

	public String deleteAccountWithId(Long accountId) throws JsonProcessingException {
		// TODO Auto-generated method stub
		AccountMessageForm form = new AccountMessageForm();
		
		Account account = null;
		
		try {
			account = getAccount(accountId);
		} catch (NotFoundException e) {

			form.setMessage(e.toString());
			form.setType(SimpleMessageType.FAIL);
			return objectMapper.writeValueAsString(form);
		}
		
		account.updateDeleted();
		form.setMessage("성공적으로 삭제되었습니다.");
		form.setType(SimpleMessageType.SUCCESS);
		
		return objectMapper.writeValueAsString(form);
	}
	
	
	
	
	
	
	
	

}
