package book.modules.admin;

import java.util.List;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;

import book.modules.account.Account;
import book.modules.account.CurrentAccount;
import book.modules.board.Board;
import book.modules.menu.Menu;
import book.modules.menu.MenuService;
import book.modules.menu.form.MenuForm;
import book.modules.menu.form.MenuFormValidator;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestMapping("/admin")
@RequiredArgsConstructor
@Controller
public class AdminMenuController {

	private final AdminService adminService;
	private final MenuService menuService;
	private final ModelMapper modelMapper;
	private final MenuFormValidator menuFormValidator;
	
	@InitBinder("menuForm")
	public void initBinder(WebDataBinder webDataBinder) {
		webDataBinder.addValidators(menuFormValidator);
	}
	
	@GetMapping("/menu")
	public String adminMenuManagement(@CurrentAccount Account account , Model model) {
		
		List<Menu> allMenus = adminService.getAllMenus();
		
		model.addAttribute("menuList", allMenus);
		
		return "admin/menu/menu";
	}
	
	@GetMapping("/menu/add")
	public String adminMenuManageAddForm(@CurrentAccount Account account , Model model) {
		
		
		List<Board> allBoards = adminService.getAllBoards();
		List<Menu> allMenus = adminService.getAllMenus();
		model.addAttribute(new MenuForm());
		model.addAttribute("allBoardList", allBoards);
		model.addAttribute("allMenuList", allMenus);
		
		return "admin/menu/add";
	}
	
	@PostMapping("/menu/add")
	public String adminMenuManageAddSubmit(@CurrentAccount Account account , Model model , @Valid MenuForm menuForm, Errors errors) throws NotFoundException {

		if (errors.hasErrors()) {
			return "redirect:/admin/menu/add";
		}
		
		menuService.addMenu(menuForm);
		
		return "redirect:/admin/menu";
	}
	
	@GetMapping("/menu/update/{id}")
	public String adminMenuManageUpdateForm(@CurrentAccount Account account , Model model , @PathVariable Long id) throws NotFoundException {
		
		
		List<Board> allBoards = adminService.getAllBoards();
		List<Menu> allMenus = adminService.getAllMenusWithOutId(id);
		Menu menu = adminService.getMenu(id);
		MenuForm menuForm = modelMapper.map(menu, MenuForm.class);
		
		if (menu.getBoard() != null) {
			menuForm.setBoardName(menu.getBoard().getName());
			menuForm.setBoardId(menu.getBoard().getId());
		}
		model.addAttribute(menuForm);
		
		model.addAttribute("allBoardList", allBoards);
		
		return "admin/menu/update";
	}
	
	@PostMapping("/menu/update/{id}")
	public String adminMenuManageUpdateSubmit(@CurrentAccount Account account , Model model , @Valid MenuForm menuForm, Errors errors, @PathVariable Long id) throws NotFoundException {

		if (errors.hasErrors()) {
			return "redirect:/admin/menu/update/" + id;
		}
		
		menuService.updateMenu(id,menuForm);
		
		return "redirect:/admin/menu";
	}
	
	
	@PostMapping("/menu/delete")
	@ResponseBody
	public ResponseEntity<String> adminMenuManagerDeleteSutmit(Long accountId , Long menuId) throws JsonProcessingException {

		String message = menuService.deleteMenu(menuId);
		
		return new ResponseEntity<>(message , HttpStatus.OK);
	}
	
	
}
