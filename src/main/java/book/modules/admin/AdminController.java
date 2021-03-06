package book.modules.admin;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fasterxml.jackson.core.JsonProcessingException;

import book.modules.account.Account;
import book.modules.account.CurrentAccount;
import book.modules.board.Board;
import book.modules.board.BoardService;
import book.modules.board.form.BoardFormValidator;
import book.modules.comment.Comment;
import book.modules.post.Post;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
public class AdminController {

	private final AdminService adminService;
	
	@GetMapping("/admin")
	public String adminDashBoard(@CurrentAccount Account account, Model model) throws JsonProcessingException {
		
		String accountStatistics = adminService.getAccountStatistics();
		String postStatistics = adminService.getPostStatistics();
		String commentStatistics = adminService.getCommentStatistics();
		
		List<Board> allBoards = adminService.getTop10Boards();
		List<Post> allPosts = adminService.getTop10Posts();
		List<Comment> allComments = adminService.getTop10Comments();
		List<Account> allAccounts = adminService.getTop10Accounts();
		
		model.addAttribute("accountStatistics", accountStatistics);
		model.addAttribute("postStatistics", postStatistics);
		model.addAttribute("commentStatistics", commentStatistics);
		model.addAttribute("boardList", allBoards);
		model.addAttribute("postList", allPosts);
		model.addAttribute("accountList", allAccounts);
		model.addAttribute("commentList", allComments);
		
//		System.out.println("===================================");
//		System.out.println(allBoards.get(0).getRole().getAuthority());
//		System.out.println("===================================");
		
		return "admin/index";
	}
}
