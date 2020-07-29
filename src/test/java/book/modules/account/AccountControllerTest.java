package book.modules.account;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.then;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import book.mail.Email;
import book.mail.EmailService;
import book.modules.WithAccount;
import book.modules.account.form.AccountForm;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Rollback(value = true)
@Transactional
@AutoConfigureMockMvc
@SpringBootTest
class AccountControllerTest {

	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private AccountRepository accountRepository;
	
	@Autowired
	private AccountService accountService;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@MockBean
	private EmailService emailService;

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
	
	@DisplayName("토큰 인증 성공 후 비밀번호 수정 - 실패 - 이메일이 틀림")
	@Test
	void accountChangePasswordSubmitFailureEmail() throws Exception {
				
		Account beforeAccount = accountRepository.findByLoginId(account.getLoginId());
				
		assertNotNull(beforeAccount);
		beforeAccount.generateEmailCheckToken();

		
		mockMvc.perform(post("/change-password")
						.param("email", "ㅁoaoaowi")
						.param("token", account.getToken())
						.param("newPassword", "1111111")
						.param("newPasswordConfirm", "1111111")
						.with(csrf())
						)
		.andExpect(status().isOk());
		
		assertFalse(passwordEncoder.matches("1111111", beforeAccount.getPassword()));
		
	}
	
	@DisplayName("토큰 인증 성공 후 비밀번호 수정 - 실패 - 비밀번호가 일치하지 않음")
	@Test
	void accountChangePasswordSubmitFailurePassword() throws Exception {
				
		Account beforeAccount = accountRepository.findByLoginId(account.getLoginId());
				
		assertNotNull(beforeAccount);
		beforeAccount.generateEmailCheckToken();

		
		mockMvc.perform(post("/change-password")
						.param("email", account.getEmail())
						.param("token", account.getToken())
						.param("newPassword", "1111112")
						.param("newPasswordConfirm", "1111111")
						.with(csrf())
						)
		.andExpect(status().isOk())
		.andExpect(model().attributeExists("message"))
		.andExpect(view().name("mail/changePassword"));
		
		assertFalse(passwordEncoder.matches("1111111", beforeAccount.getPassword()));
		
	}
	
	
	@DisplayName("토큰 인증 성공 후 비밀번호 수정 - 성공")
	@Test
	void accountChangePasswordSubmitSuccess() throws Exception {
				
		Account beforeAccount = accountRepository.findByLoginId(account.getLoginId());
				
		assertNotNull(beforeAccount);
		beforeAccount.generateEmailCheckToken();

		
		mockMvc.perform(post("/change-password")
						.param("email", account.getEmail())
						.param("token", account.getToken())
						.param("newPassword", "1111111")
						.param("newPasswordConfirm", "1111111")
						.with(csrf())
						)
		.andExpect(status().is3xxRedirection());
		
		assertTrue(passwordEncoder.matches("1111111", beforeAccount.getPassword()));
		
	}
	
	@DisplayName("토큰 인증 성공 후 비밀번호 수정 폼 - 실패 - 이메일이 틀릴때")
	@Test
	void accountFindPasswordTokenFailure() throws Exception {
				
		account.generateEmailCheckToken();
		
		mockMvc.perform(get("/check-email-token")
							.param("token", account.getToken())
							.param("email", "aakksios")
						)
		.andExpect(status().isOk());
	}
	
	@DisplayName("토큰 인증 성공 후 비밀번호 수정 폼 - 성공")
	@Test
	void accountFindPasswordTokenSuccess() throws Exception {
				
		account.generateEmailCheckToken();
		
		mockMvc.perform(get("/check-email-token")
							.param("token", account.getToken())
							.param("email", account.getEmail())
						)
		.andExpect(status().isOk())
		.andExpect(model().attributeExists("token"))
		.andExpect(model().attributeExists("email"))
		.andExpect(model().attributeExists("passwordForm"))
		.andExpect(view().name("mail/changePassword"));
	}
	
	@DisplayName("비밀번호 찾기 실패 - 등록되지 않는 이메일로 요청시")
	@Test
	void accountFindPasswordSubmitFailure() throws Exception {
		
		
		mockMvc.perform(post("/find-password")
						.param("email", "ididid@naver.com")
						.with(csrf())
						)
		.andExpect(status().isOk())
		.andExpect(model().attributeExists("message"))
		.andExpect(model().attribute("message", "일치하는 이메일이 존재하지 않습니다."));
		
		assertNull(account.getToken());
	}
	
	@DisplayName("비밀번호 찾기 성공")
	@Test
	void accountFindPasswordSubmitSuccess() throws Exception {
				
		mockMvc.perform(post("/find-password")
						.param("email", "choiyurim2@naver.com")
						.with(csrf())
						)
		.andExpect(status().is3xxRedirection())
		.andExpect(flash().attributeExists("message"))
		.andExpect(flash().attribute("message", "메일이 발송되었습니다."));
		
		then(emailService).should().sendEmail(Mockito.any(Email.class));
		
		assertNotNull(account.getToken());
	}
	
	
	@DisplayName("비밀번호 찾기 폼")
	@Test
	void accountFindPasswordForm() throws Exception {
		
		mockMvc.perform(get("/find-password"))
		.andExpect(status().isOk())
		.andExpect(view().name("account/find-password"));
		
	}
	
	
	@WithAccount("ididid")
	@DisplayName("회원 이미지 변경 - 실패 - 이미지가 아닌것을 업로드 한경우")
	@Test
	void accountProfileImageFormSubmitFailure() throws Exception {
				
		mockMvc.perform(post("/account/profile-image")
						.param("profileImage", "textfile")
						.with(csrf())
				)
		.andExpect(status().is3xxRedirection())
		.andExpect(flash().attributeExists("message"))
		.andExpect(flash().attribute("message", "프로필 이미지 수정이 실패했습니다."));
		
		Account afterAccount = accountRepository.findByLoginId("ididid");
		
		assertNotNull(afterAccount);
		assertNull(afterAccount.getProfileImage());
		
		
	}
	
	
	@WithAccount("ididid")
	@DisplayName("회원 이미지 변경 - 성공")
	@Test
	void accountProfileImageFormSubmitSuccess() throws Exception {
		
		
		mockMvc.perform(post("/account/profile-image")
						.param("profileImage", "data:image/png;base64")
						.with(csrf())
				)
		.andExpect(status().is3xxRedirection())
		.andExpect(flash().attributeExists("message"))
		.andExpect(flash().attribute("message", "프로필 이미지가 수정되었습니다."));
		
		Account afterAccount = accountRepository.findByLoginId("ididid");
		
		assertNotNull(afterAccount);
		assertTrue(afterAccount.getProfileImage().equals("data:image/png;base64"));
		
	}
	
	@WithAccount("ididid")
	@DisplayName("회원 프로필 이미지 변경 폼")
	@Test
	void accountProfileImageForm() throws Exception {
		
		mockMvc.perform(get("/account/profile-image"))
		.andExpect(status().isOk())
		.andExpect(model().attributeExists("account"))
		.andExpect(view().name("account/profile-image-form"));
		
	}
	
	
	@WithAccount("ididid")
	@DisplayName("회원 닉네임 변경 - 성공")
	@Test
	void accountLeaveFormSubmitSuccess() throws Exception {
		
		Account beforeAccount = accountRepository.findByLoginId("ididid");
		
		assertNotNull(beforeAccount);
		
		mockMvc.perform(post("/account/leave")
						.with(csrf())
				)
		.andExpect(status().is3xxRedirection());
		
		Account afterAccount = accountRepository.findByLoginId("ididid");
		
		assertNull(afterAccount);
	}
	
	
	@WithAccount("ididid")
	@DisplayName("회원 탈퇴 폼")
	@Test
	void accountLeaveForm() throws Exception {
		
		mockMvc.perform(get("/account/leave"))
		.andExpect(status().isOk())
		.andExpect(model().attributeExists("account"))
		.andExpect(view().name("account/delete-form"));
		
	}
	
	
	@WithAccount("ididid")
	@DisplayName("회원 닉네임 변경 - 실패 - 허용되지 않는 문자열")
	@Test
	void accountProfileNicknameSubmitFaiulre() throws Exception {
		
		Account beforeAccount = accountRepository.findByLoginId("ididid");
		
		assertNotNull(beforeAccount);
		assertThat(beforeAccount.getNickname()).isEqualTo("idididnickname");
		
		mockMvc.perform(post("/account/nickname")
						.param("nickname" , "★닉네임★")
						.with(csrf())
				)
		.andExpect(model().attributeExists("account"))
		.andExpect(view().name("account/nickname-form"));
		
		assertThat(beforeAccount.getNickname()).isEqualTo("idididnickname");
	}
	
	
	@WithAccount("ididid")
	@DisplayName("회원 닉네임 변경 - 성공")
	@Test
	void accountProfileNicknameSubmitSuccess() throws Exception {
		
		Account beforeAccount = accountRepository.findByLoginId("ididid");
		
		assertNotNull(beforeAccount);
		assertThat(beforeAccount.getNickname()).isEqualTo("idididnickname");
		
		mockMvc.perform(post("/account/nickname")
						.param("nickname" , "닉네임")
						.with(csrf())
				)
		.andExpect(status().is3xxRedirection())
		.andExpect(flash().attributeExists("message"));
		
		
		assertThat(beforeAccount.getNickname()).isEqualTo("닉네임");
	}
	
	@WithAccount("ididid")
	@DisplayName("회원 닉네임 변경 폼")
	@Test
	void accountProfileNicknameForm() throws Exception {
		
		mockMvc.perform(get("/account/nickname"))
		.andExpect(status().isOk())
		.andExpect(model().attributeExists("nicknameForm"))
		.andExpect(model().attributeExists("account"))
		.andExpect(view().name("account/nickname-form"));
		
	}
	
	
	@WithAccount("ididid")
	@DisplayName("회원 패스워드 변경 - 실패 - 새 비밀번호, 새비밀번호 확인이 일치하지 않는 경우")
	@Test
	void accountProfilePasswordSubmitFailure() throws Exception {
		
		Account beforeAccount = accountRepository.findByLoginId("ididid");
		
		assertNotNull(beforeAccount);
		assertThat(passwordEncoder.matches("12312300", beforeAccount.getPassword()));
		
		mockMvc.perform(post("/account/password")
						.param("newPassword" , "12312310")
						.param("newPasswordConfirm", "12312311")
						.with(csrf())
				)
		.andExpect(status().isOk())
		.andExpect(model().attributeExists("account"))
		.andExpect(view().name("account/password-form"));
		
		
		assertThat(passwordEncoder.matches("12312300", beforeAccount.getPassword()));
	}
	
	@WithAccount("ididid")
	@DisplayName("회원 패스워드 변경 - 성공")
	@Test
	void accountProfilePasswordSubmitSuccess() throws Exception {
		
		Account beforeAccount = accountRepository.findByLoginId("ididid");
		
		assertNotNull(beforeAccount);
		assertThat(passwordEncoder.matches("12312300", beforeAccount.getPassword()));
		
		mockMvc.perform(post("/account/password")
						.param("newPassword" , "12312311")
						.param("newPasswordConfirm", "12312311")
						.with(csrf())
				)
		.andExpect(status().is3xxRedirection())
		.andExpect(flash().attributeExists("message"));
		
		
		assertThat(passwordEncoder.matches("12312311", beforeAccount.getPassword()));
	}
	
	@WithAccount("ididid")
	@DisplayName("회원 패스워드 변경 폼")
	@Test
	void accountProfilePasswordForm() throws Exception {
		
		mockMvc.perform(get("/account/password"))
		.andExpect(status().isOk())
		.andExpect(model().attributeExists("passwordForm"))
		.andExpect(model().attributeExists("account"))
		.andExpect(view().name("account/password-form"));
		
	}
	
	
	@WithAccount("ididid")
	@DisplayName("회원 정보 서브밋 - 수정 실패 생년월일 범위 초과")
	@Test
	void accountProfileSubmitFailure() throws Exception {
		
		Account beforeAccount = accountRepository.findByLoginId("ididid");
		
		assertNotNull(beforeAccount);
		assertThat(beforeAccount.getAccountGender()).isEqualTo(AccountGender.male);
		assertThat(beforeAccount.getBirthYear()).isEqualTo(1990);
		
		mockMvc.perform(post("/account/profile")
						.param("accountGender" , "female")
						.param("birthYear", "9999")
						.with(csrf())
				)
		.andExpect(status().isOk())
		.andExpect(model().attributeExists("account"))
		.andExpect(view().name("account/profile-form"));
		
		assertThat(beforeAccount.getAccountGender()).isEqualTo(AccountGender.male);
		assertThat(beforeAccount.getBirthYear()).isEqualTo(1990);
	}
	
	
	@WithAccount("ididid")
	@DisplayName("회원 정보 서브밋 - 수정 성공")
	@Test
	void accountProfileSubmitSuccess() throws Exception {
		
		Account beforeAccount = accountRepository.findByLoginId("ididid");
		
		assertNotNull(beforeAccount);
		assertThat(beforeAccount.getAccountGender()).isEqualTo(AccountGender.male);
		assertThat(beforeAccount.getBirthYear()).isEqualTo(1990);
		
		mockMvc.perform(post("/account/profile")
						.param("accountGender" , "female")
						.param("birthYear", "2000")
						.with(csrf())
				)
		.andExpect(status().is3xxRedirection())
		.andExpect(flash().attributeExists("message"));
		
		assertThat(beforeAccount.getAccountGender()).isEqualTo(AccountGender.female);
		assertThat(beforeAccount.getBirthYear()).isEqualTo(2000);
	}
	
	@WithAccount("ididid")
	@DisplayName("회원 정보 폼")
	@Test
	void accountProfileForm() throws Exception {
		
		mockMvc.perform(get("/account/profile"))
		.andExpect(status().isOk())
		.andExpect(model().attributeExists("profileForm"))
		.andExpect(model().attributeExists("account"))
		.andExpect(view().name("account/profile-form"));
		
	}
	
	
	@DisplayName("회원가입 폼")
	@Test
	void accountRegisterForm() throws Exception {
		mockMvc.perform(get("/register"))
				.andExpect(status().isOk())
				.andExpect(model().attributeExists("accountForm"))
				.andExpect(view().name("account/register"));
	}
	
	@DisplayName("회원가입 서브밋 - 성공")
	@Test
	void accountRegisterSubmitSuccess() throws Exception {
		mockMvc.perform(
					post("/register")
					.param("loginId", "ididid")
					.param("email", "aksdkasdl@naver.com")
					.param("nickname" , "ngh547r798")
					.param("password", "100200")
					.param("birthYear", "2000")
					.param("accountGender", "male")
					.with(csrf())
				)
				.andExpect(status().is3xxRedirection());
		
		Account findByLoginId = accountRepository.findByLoginId("ididid");
		
		assertNotNull(findByLoginId);
		assertThat(findByLoginId.getEmail()).isEqualTo("aksdkasdl@naver.com");
		assertThat(findByLoginId.getPassword()).isNotEqualTo("100200");
		
	}
	
	@DisplayName("회원가입 서브밋 - 실패 - 아이디 허용된 글자가 아닌값을 입력했을 경우")
	@Test
	void accountRegisterSubmitFailureNotAllowedLoginId() throws Exception {
		
		mockMvc.perform(
				post("/register")
				.param("loginId", "ididid가")
				.param("email", "aksdkasdl@naver.com")
				.param("nickname" , "닉네임")
				.param("password", "100200")
				.param("birthYear", "2000")
				.param("accountGender", "male")
				.with(csrf())
			)
			.andExpect(status().isOk())
			.andExpect(view().name("account/register"));
		
		Account findByLoginId = accountRepository.findByLoginId("ididid가");
		
		assertNull(findByLoginId);
	}
	
	@DisplayName("회원가입 서브밋 - 실패 - 이메일 허용된 글자가 아닌값을 입력했을 경우")
	@Test
	void accountRegisterSubmitFailureNotAllowedEmail() throws Exception {
		
		mockMvc.perform(
				post("/register")
				.param("loginId", "ididid")
				.param("email", "aksdkasd가l@n기aver.com")
				.param("nickname" , "닉네임")
				.param("password", "100200")
				.param("birthYear", "2000")
				.param("accountGender", "male")
				.with(csrf())
			)
			.andExpect(status().isOk())
			.andExpect(view().name("account/register"));
		
		Account findByLoginId = accountRepository.findByLoginId("ididid");
		
		assertNull(findByLoginId);
	}
	
	@DisplayName("회원가입 서브밋 - 실패 - 닉네임 허용된 글자가 아닌값을 입력했을 경우")
	@Test
	void accountRegisterSubmitFailureNotAllowedNickname() throws Exception {
		
		mockMvc.perform(
				post("/register")
				.param("loginId", "ididid")
				.param("email", "aksdkasdl@n기aver.com")
				.param("nickname" , "닉★네_임")
				.param("password", "100200")
				.param("birthYear", "2000")
				.param("accountGender", "male")
				.with(csrf())
			)
			.andExpect(status().isOk())
			.andExpect(view().name("account/register"));
		
		Account findByLoginId = accountRepository.findByLoginId("ididid");
		
		assertNull(findByLoginId);
	}
}
