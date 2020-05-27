package book.config;

import java.util.Optional;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import book.modules.account.UserAccount;
import book.modules.account.Account;
import book.modules.account.AccountService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class SecurityAuditorAware implements AuditorAware<Account>{

	private final AccountService accountService;
	
	@Override
	public Optional<Account> getCurrentAuditor() {
		// TODO Auto-generated method stub
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		
		if (authentication == null || authentication instanceof AnonymousAuthenticationToken || !authentication.isAuthenticated()) {
			return Optional.empty();
		}
				
		return Optional.ofNullable(((UserAccount)authentication.getPrincipal()).getAccount());
	}

}
