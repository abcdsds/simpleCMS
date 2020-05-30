package book.modules.comment;

import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import book.modules.account.Account;
import book.modules.account.CurrentAccount;
import book.modules.comment.form.CommentForm;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/comment")
@RequiredArgsConstructor
public class CommentController {

	private final CommentService commentService;
	
	@PostMapping("/add/{id}")
	public String commentSubmit(@CurrentAccount Account account , Model model , @Valid CommentForm form , Errors errors, @PathVariable Long id) throws NotFoundException {
		
		
		if (errors.hasErrors()) {
			return "redirect:/post/view/"+form.getPostId();
		}
		
		form.setPostId(id);
		
		commentService.addComment(account,form);
		
		return "redirect:/post/view/"+id;
	}
	
	@PostMapping("/add/{postId}/{commentId}")
	public String commentReplySubmit(@CurrentAccount Account account , Model model , 
									 @Valid CommentForm form , Errors errors, @PathVariable Long postId,
									 @PathVariable Long commentId ) throws NotFoundException {
		
		if (errors.hasErrors()) {
			return "redirect:/post/view/"+form.getPostId();
		}
		
		form.setPostId(postId);
		form.setParentCommentId(commentId);
		
		commentService.addComment(account,form);
		
		return "redirect:/post/view/"+postId;
	}
}
