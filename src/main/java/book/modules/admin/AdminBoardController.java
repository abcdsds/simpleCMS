package book.modules.admin;

import java.nio.file.AccessDeniedException;
import java.util.List;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.core.JsonProcessingException;

import book.modules.account.Account;
import book.modules.account.CurrentAccount;
import book.modules.account.form.AccountListForm;
import book.modules.board.Board;
import book.modules.board.BoardService;
import book.modules.board.form.BoardForm;
import book.modules.board.form.BoardFormValidator;
import book.modules.comment.Comment;
import book.modules.post.Post;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestMapping("/admin")
@RequiredArgsConstructor
@Controller
public class AdminBoardController {

	private final AdminService adminService;
	private final BoardService boardService;
	private final BoardFormValidator boardFormValidator;
	private final ModelMapper modelMapper;
	
	@InitBinder("boardForm")
	public void initBinderBoardForm (WebDataBinder webDataBinder) {
		webDataBinder.addValidators(boardFormValidator);
	}
	
	
	@GetMapping("/board")
	public String adminBoardManagement(@CurrentAccount Account account , Model model) {
		
		List<Board> allBoards = adminService.getTop10Boards();
		
		model.addAttribute("boardList", allBoards);
		
		return "admin/board/board";
	}
	
	@GetMapping("/board/add")
	public String adminBoardManagementAddForm(@CurrentAccount Account account , Model model) {
		
		model.addAttribute(new BoardForm());
		
		return "admin/board/add";
	}
	
	@PostMapping("/board/add")
	public String adminBoardManagementAddSubmit(@CurrentAccount Account account , Model model , @Valid BoardForm boardForm , Errors errors) throws NotFoundException {
		
		if (errors.hasErrors()) {
			model.addAttribute(account);
			return "admin/board/add";
		}
		
		boardService.addBoard(account,boardForm);
		
		return "redirect:/admin/board";
	}
	
	@GetMapping("/board/update/{id}")
	public String adminBoardManagementUpdateForm(@CurrentAccount Account account , Model model , @PathVariable Long id, 
												@PageableDefault(size = 12, page = 0 , sort = "managedAt" , direction = Sort.Direction.DESC) 
												Pageable pageable) throws NotFoundException {
		
		Board board = boardService.getBoardWithId(account, id);
		Page<AccountListForm> accounts = adminService.getAccountsWithBoardId(id, pageable);
		List<Account> allAccounts = adminService.getAllAccounts();
		
		model.addAttribute(modelMapper.map(board, BoardForm.class));
		model.addAttribute(board);
		model.addAttribute("accountListWithBoard", accounts);
		model.addAttribute("allAccountList", allAccounts);
				
		return "admin/board/update";
	}
	
	@PostMapping("/board/update/")
	public String adminBoardManagementUpdateSubmit(@CurrentAccount Account account , Model model , 
													@Valid BoardForm boardForm , Errors errors , 
													RedirectAttributes redirect) throws AccessDeniedException, NotFoundException {
		
		
		if (errors.hasErrors()) {
			redirect.addFlashAttribute("message", "수정에 실패했습니다.");
			return "redirect:/admin/board/update/" + boardForm.getId();
		}
		
		boardService.updateBoard(boardForm);
		
		redirect.addFlashAttribute("message", "수정에 성공했습니다.");
		return "redirect:/admin/board/update/" + boardForm.getId();
	}
	
	@PostMapping("/board/manager/add")
	@ResponseBody
	public ResponseEntity<String> adminBoardManagermentManagerAddSubmit(Long accountId , Long boardId) throws JsonProcessingException {

		String message = boardService.addBoardManager(accountId, boardId);
		
		return new ResponseEntity<>(message , HttpStatus.OK);
	}
	
	@PostMapping("/board/manager/delete")
	@ResponseBody
	public ResponseEntity<String> adminBoardManagermentManagerDeleteSubmit(Long accountId , Long boardId) throws JsonProcessingException {

		String message = boardService.deleteBoardManager(accountId, boardId);
		
		return new ResponseEntity<>(message , HttpStatus.OK);
	}
	
	@PostMapping("/board/delete")
	@ResponseBody
	public ResponseEntity<String> adminMenuManagerDeleteSutmit(Long boardId) throws JsonProcessingException {

		String message = boardService.deleteBoard(boardId);
		
		return new ResponseEntity<>(message , HttpStatus.OK);
	}
}
