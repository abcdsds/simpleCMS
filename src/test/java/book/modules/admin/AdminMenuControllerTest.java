package book.modules.admin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import book.MockMvcTest;
import book.modules.WithAccount;
import book.modules.account.Account;
import book.modules.account.AccountGender;
import book.modules.account.AccountRepository;
import book.modules.account.AccountService;
import book.modules.account.form.AccountForm;
import book.modules.board.Board;
import book.modules.board.BoardRepository;
import book.modules.board.BoardService;
import book.modules.board.form.BoardForm;
import book.modules.menu.Menu;
import book.modules.menu.MenuRepository;
import book.modules.menu.MenuService;
import book.modules.menu.MenuType;
import book.modules.menu.form.MenuForm;
import javassist.NotFoundException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Rollback(true)
@MockMvcTest
class AdminMenuControllerTest {

	@Autowired private MockMvc mockMvc;
	@Autowired private BoardRepository boardRepository;
	@Autowired private MenuRepository menuRepository;
	@Autowired private PasswordEncoder passwordEncoder;
	@Autowired private AccountService accountService;
	@Autowired private AccountRepository accountRepository;
	@Autowired private BoardService boardService;
	@Autowired private MenuService menuService;
	
	private Board board;
	private Account account;
	private Menu mainMenu;
	private Menu subMenu;
	
	
	@BeforeEach
	void before() throws NotFoundException {
		
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
		
		BoardForm boardform = new BoardForm();
		boardform.setName("테스트게시판1");
		boardform.setPath("ppppppp");
		boardform.setRole("ROLE_USER");
		
		board = boardService.addBoard(account, boardform);
		
		MenuForm menuForm = new MenuForm();
		menuForm.setName("메뉴1111");
		menuForm.setPath("경로1111");
		menuForm.setRole("ROLE_USER");
		menuForm.setType(MenuType.BOARD);
		menuForm.setBoardId(board.getId());
		menuForm.setBoardName(board.getName());
		

		mainMenu = menuService.addMenu(menuForm);
		
		MenuForm subForm = new MenuForm();
		subForm.setName("메뉴서브1111");
		subForm.setPath("경로서브1111");
		subForm.setRole("ROLE_USER");
		subForm.setType(MenuType.LINK);
		subForm.setParentId(mainMenu.getId());
		
		subMenu = menuService.addMenu(subForm);
		
	}
	
	@WithAccount("admintest01")
	@DisplayName("어드민 페이지 메뉴 관리 메인")
	@Test
	void adminMenuList() throws Exception {
		
		mockMvc.perform(get("/admin/menu"))
		.andExpect(status().isOk())
		.andExpect(model().attributeExists("menuList"))
		.andExpect(view().name("admin/menu/menu"));
	}
	
	@WithAccount("admintest01")
	@DisplayName("어드민 페이지 메뉴 관리 - 메뉴 추가 form")
	@Test
	void adminMenuAddForm() throws Exception {
		
		mockMvc.perform(get("/admin/menu/add"))
		.andExpect(status().isOk())
		.andExpect(model().attributeExists("allBoardList"))
		.andExpect(model().attributeExists("allBoardList"))
		.andExpect(model().attributeExists("allMenuList"))
		.andExpect(view().name("admin/menu/add"));
	}
	
	@WithAccount("admintest01")
	@DisplayName("어드민 페이지 게시판 관리 - 추가 submit - 성공")
	@Test
	void adminBoardAddSubmitSuccess() throws Exception {
		
		mockMvc.perform(
					post("/admin/menu/add")
					.param("name", "wioie")
					.param("role", "ROLE_USER")
					.param("type", "BOARD")
					.param("boardId", board.getId().toString())
					.param("boardName", board.getName())
					.with(csrf())
				)
		.andExpect(status().is3xxRedirection());
		
		Menu lastMenu = menuRepository.findTop1ByOrderByIdDesc();
		
		assertThat(lastMenu.getName()).isEqualTo("wioie");
		assertThat(lastMenu.getPath()).isEqualTo(board.getPath());
		assertThat(lastMenu.getRole().toString()).isEqualTo("ROLE_USER");
		assertThat(lastMenu.getType()).isEqualTo(MenuType.BOARD);
	}
	
	@WithAccount("admintest01")
	@DisplayName("어드민 페이지 게시판 관리 - 추가 submit - 실패 (이름 없을경우) ")
	@Test
	void adminBoardAddSubmitFaillure() throws Exception {
		
		mockMvc.perform(
					post("/admin/menu/add")
					.param("role", "ROLE_USER")
					.param("type", "BOARD")
					.param("boardId", board.getId().toString())
					.param("boardName", board.getName())
					.with(csrf())
				)
		.andExpect(status().is3xxRedirection());
		
		Menu lastMenu = menuRepository.findTop1ByOrderByIdDesc();
		
		assertThat(lastMenu.getName()).isNotEqualTo("wioie");
		assertThat(lastMenu.getPath()).isNotEqualTo(board.getPath());
	}
	
	@WithAccount("admintest01")
	@DisplayName("어드민 페이지 메뉴 관리 - 수정 form")
	@Test
	void adminBoardUpdateForm() throws Exception {
		
		mockMvc.perform(get("/admin/menu/update/"+mainMenu.getId()))
		.andExpect(status().isOk())
		.andExpect(model().attributeExists("menuForm"))
		.andExpect(model().attributeExists("allBoardList"))
		.andExpect(view().name("admin/menu/update"));
	}

	@WithAccount("admintest01")
	@DisplayName("어드민 페이지 게시판 관리 - 수정 submit - 성공")
	@Test
	void adminMenuUpdateSubmitSuccess() throws Exception {
		
		mockMvc.perform(
					post("/admin/menu/update/"+mainMenu.getId())
					.param("id", mainMenu.getId().toString())
					.param("name", "name0099")
					.param("role", "ROLE_USER")
					.param("type", "LINK")
					.param("path", "aaaaa")
					.with(csrf())
				)
		.andExpect(status().is3xxRedirection());
		
		
		assertThat(mainMenu.getName()).isEqualTo("name0099");
		assertThat(mainMenu.getPath()).isEqualTo("aaaaa");
		assertThat(mainMenu.getType()).isEqualTo(MenuType.LINK);
	}
	
	@WithAccount("admintest01")
	@DisplayName("어드민 페이지 게시판 관리 - 수정 submit - 실패 (타입 누락)")
	@Test
	void adminMenuUpdateSubmitFailure() throws Exception {
		
		mockMvc.perform(
					post("/admin/menu/update/"+mainMenu.getId())
					.param("type", "LINK")
					.param("role", "ROLE_USER")
					.param("path", "aaaaa")
					.with(csrf())
				)
		.andExpect(status().is3xxRedirection());
		
		
		assertThat(mainMenu.getPath()).isNotEqualTo("aaaaa");
		assertThat(mainMenu.getType()).isNotEqualTo(MenuType.LINK);
	}
	
	@WithAccount("admintest01")
	@DisplayName("어드민 페이지 메뉴 관리 - 메뉴 삭제 submit - 실패 (하위메뉴 존재)")
	@Test
	void adminMenuDeleteSubmitFaillureGetSubMenu() throws Exception {
		
		MvcResult andReturn = mockMvc.perform(
					post("/admin/menu/delete")
					.param("menuId", mainMenu.getId().toString())
					.with(csrf())
				)
		.andExpect(status().isOk())
		.andReturn();
		
		assertThat(andReturn.getResponse().getContentAsString().contains("하위메뉴가 있는 메뉴는 삭제할수 없습니다.")).isTrue();
	}
	
	@WithAccount("admintest01")
	@DisplayName("어드민 페이지 메뉴 관리 - 메뉴 삭제 submit - 실패 (메뉴아이디가 없음)")
	@Test
	void adminMenuDeleteSubmitFailureNoMenu() throws Exception {
		
		
		MvcResult andReturn = mockMvc.perform(
					post("/admin/menu/delete")
					.param("menuId", mainMenu.getId().toString()+"0098")
					.with(csrf())
				)
		.andExpect(status().isOk())
		.andReturn();
		
		assertThat(andReturn.getResponse().getContentAsString().contains("메뉴가 존재하지 않습니다.")).isTrue();
	}
	
	@WithAccount("admintest01")
	@DisplayName("어드민 페이지 메뉴 관리 - 메뉴 삭제 submit - 성공")
	@Test
	void adminMenuDeleteSubmitSuccess() throws Exception {
		
		MvcResult andReturn = mockMvc.perform(
					post("/admin/menu/delete")
					.param("menuId", subMenu.getId().toString())
					.with(csrf())
				)
		.andExpect(status().isOk())
		.andReturn();
		
		assertThat(andReturn.getResponse().getContentAsString().contains("메뉴가 성공적으로 삭제되었습니다.")).isTrue();
	}
}
