package book.modules.board;

import java.nio.file.AccessDeniedException;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
	public String boardView(@CurrentAccount Account account , Model model , @PathVariable String boardPath, @RequestParam (defaultValue = "") String keyword,
							@PageableDefault(size = 12, page = 0 ,
							sort = "id" , direction = Sort.Direction.DESC) Pageable pageable) throws AccessDeniedException {
		
		Board board = boardService.getBoard(account, boardPath);

		model.addAttribute(board);
		model.addAttribute("keyword", keyword);
		model.addAttribute("postList" , boardService.getPostList(board, keyword, pageable));
		model.addAttribute(account);
		
		return "post/list";
	}
	
	
}
