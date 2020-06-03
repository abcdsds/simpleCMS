package book.modules.board;

import java.nio.file.AccessDeniedException;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import book.modules.account.Account;
import book.modules.account.CurrentAccount;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/board")
@Controller
public class BoardController {

	
	private final BoardService boardService;
	
	@GetMapping("/test/test/test")
	public String boardTest(@CurrentAccount Account account) {
		
		boardService.boardCreateTest(account);
		return "redirect:/";
	}
	
	@GetMapping("/{boardPath}")
	public String boardView(@CurrentAccount Account account , Model model , @PathVariable String boardPath) throws AccessDeniedException {
		Board board = boardService.getBoard(account, boardPath);

		model.addAttribute(board);
		model.addAttribute(account);
		
		return "post/list";
	}
	
	
}
