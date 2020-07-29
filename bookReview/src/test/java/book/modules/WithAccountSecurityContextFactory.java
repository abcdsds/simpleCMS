package book.modules;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import book.modules.account.AccountGender;
import book.modules.account.AccountService;
import book.modules.account.form.AccountForm;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class WithAccountSecurityContextFactory implements WithSecurityContextFactory<WithAccount> {

	private final AccountService accountService;
	private final PasswordEncoder passwordEncoder;
	
	@Override
	public SecurityContext createSecurityContext(WithAccount withAccount) {
		// TODO Auto-generated method stub

		String loginId = withAccount.value();
		String role = "ROLE_USER";
		
		if (loginId.contains("admin")) {
			role = "ROLE_ADMIN";
		}
		
		AccountForm form = new AccountForm();
		form.setLoginId(loginId);
		form.setBirthYear(1990);
		form.setPassword(passwordEncoder.encode("12312300"));
		form.setEmail(loginId+"@naver.com");
		form.setNickname(loginId+"nickname");
		form.setPassword("12312344");
		form.setAccountGender(AccountGender.male);
		
		
		accountService.accountCreate(form, role);
		
		
		UserDetails principal = accountService.loadUserByUsername(loginId);
		Authentication authentication = new UsernamePasswordAuthenticationToken(principal, principal.getPassword(),
				principal.getAuthorities());
		SecurityContext context = SecurityContextHolder.createEmptyContext();
		context.setAuthentication(authentication);

		return context;
	}

}
