package book.modules.admin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import book.MockMvcTest;
import book.modules.WithAccount;
import book.modules.account.Account;
import book.modules.account.AccountGender;
import book.modules.account.AccountService;
import book.modules.account.form.AccountForm;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Rollback(value = true)
@MockMvcTest
class AdminAccountControllerTest {

	@Autowired private MockMvc mockMvc;
	@Autowired private PasswordEncoder passwordEncoder;
	@Autowired private AccountService accountService;
	
	private Account account;
	
	
	@BeforeEach
	void before() {
		
		String loginId = "choiyurim2";
		AccountForm form = new AccountForm();
		form.setLoginId(loginId);
		form.setBirthYear(1990);
		form.setPassword(passwordEncoder.encode("12312300"));
		form.setEmail(loginId+"@naver.com");
		form.setNickname(loginId+"nickname");
		form.setPassword("12312344");
		form.setAccountGender(AccountGender.male);
		
		account = accountService.accountCreate(form);
	}
	
	@WithAccount("admintest01")
	@DisplayName("회원관리 페이지 - 메인")
	@Test
	void adminAccountForm() throws Exception {
		
		mockMvc.perform(get("/admin/account"))
				.andExpect(status().isOk())
				.andExpect(model().attributeExists("resultList"))
				.andExpect(model().attributeExists("keyword"))
				.andExpect(view().name("admin/account/account"));
	}
	
	@WithAccount("admintest01")
	@DisplayName("회원관리 페이지 - 수정 ")
	@Test
	void adminAccountUpdateForm() throws Exception {
		
		mockMvc.perform(get("/admin/account/update/" + account.getId()))
				.andExpect(status().isOk())
				.andExpect(model().attributeExists("accountAdminForm"))
				.andExpect(view().name("admin/account/update"));
	}
	
	@WithAccount("admintest01")
	@DisplayName("회원관리 페이지 - 수정 적용 - 실패")
	@Test
	void adminAccountUpdateSubmitFailure() throws Exception {
		
		String nickname = account.getNickname();
		String role = account.getRole();
		String accountGender = account.getAccountGender().toString();
		int birthYear = account.getBirthYear();
		
		mockMvc.perform(
					post("/admin/account/update/")
					.param("id", account.getId().toString())
					.param("nickname", "네임변경됨")
					.param("role", "ROLE_ADMIN")
					.param("accountGender" , "FEMALE")
					.param("birthYear" , "2000")
					.with(csrf())
				)
				.andExpect(status().isOk())
				.andExpect(model().attributeExists("accountAdminForm"))
				.andExpect(view().name("admin/account/update"));
		
		assertThat(account.getAccountGender().toString()).isEqualTo(accountGender);
		assertThat(account.getRole()).isEqualTo(role);
		assertThat(account.getBirthYear()).isEqualTo(birthYear);
		assertThat(account.getNickname()).isEqualTo(nickname);
		
	}
	
	@WithAccount("admintest01")
	@DisplayName("회원관리 페이지 - 수정 적용 - 성공")
	@Test
	void adminAccountUpdateSubmitSuccess() throws Exception {
		
		String nickname = account.getNickname();
		String role = account.getRole();
		String accountGender = account.getAccountGender().toString();
		int birthYear = account.getBirthYear();
		
		mockMvc.perform(
					post("/admin/account/update/")
					.param("id", account.getId().toString())
					.param("nickname", "네임변경됨")
					.param("role", "ROLE_ADMIN")
					.param("accountGender" , "female")
					.param("birthYear" , "2000")
					.with(csrf())
				)
				.andExpect(status().is3xxRedirection());
		
		assertThat(account.getAccountGender().toString()).isNotEqualTo(accountGender);
		assertThat(account.getRole()).isNotEqualTo(role);
		assertThat(account.getBirthYear()).isNotEqualTo(birthYear);
		assertThat(account.getNickname()).isNotEqualTo(nickname);
		
	}
	
	@WithAccount("admintest01")
	@DisplayName("회원관리 페이지 - 패스워드 변경 ")
	@Test
	void adminAccountPasswordUpdateForm() throws Exception {
		
		mockMvc.perform(get("/admin/account/password/" + account.getId()))
				.andExpect(status().isOk())
				.andExpect(model().attributeExists("passwordForm"))
				.andExpect(view().name("admin/account/password"));
	}
	
	@WithAccount("admintest01")
	@DisplayName("회원관리 페이지 - 패스워드 변경 적용 - 실패")
	@Test
	void adminAccountPasswordUpdateSubmitFailure() throws Exception {
		
		String password = account.getPassword();
		
		mockMvc.perform(
					post("/admin/account/password/")
					.param("id", account.getId().toString())
					.param("newPassword", "123123009a111111")
					.param("newPasswordConfirm", "123123009")
					.with(csrf())
				)
				.andExpect(status().isOk());
		
		assertThat(account.getPassword().toString()).isEqualTo(password);
		
	}
	
	
	@WithAccount("admintest01")
	@DisplayName("회원관리 페이지 - 패스워드 변경 적용 - 성공")
	@Test
	void adminAccountPasswordUpdateSubmitSuccess() throws Exception {
		
		String password = account.getPassword();
		
		mockMvc.perform(
					post("/admin/account/password/")
					.param("id", account.getId().toString())
					.param("newPassword", "123123009")
					.param("newPasswordConfirm", "123123009")
					.with(csrf())
				)
				.andExpect(status().is3xxRedirection());
		
		assertThat(account.getPassword().toString()).isNotEqualTo(password);
	}
	
	@WithAccount("admintest01")
	@DisplayName("회원관리 페이지 - 회원 삭제처리 - 성공")
	@Test
	void adminAccountDeleteSubmitSuccess() throws Exception {
		
		String loginId = account.getLoginId();
		String email = account.getEmail();
		String nickname = account.getNickname();
		
		mockMvc.perform(
					post("/admin/account/delete")
					.param("accountId", account.getId().toString())
					.with(csrf())
				)
				.andExpect(status().isOk());
		
		assertThat(account.getNickname()).isNotEqualTo(nickname);
		assertThat(account.getEmail()).isNotEqualTo(email);
		assertThat(account.getLoginId()).isNotEqualTo(loginId);

		
	}
}
