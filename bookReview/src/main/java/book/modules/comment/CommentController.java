package book.modules.comment;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;

import book.modules.account.Account;
import book.modules.account.CurrentAccount;
import book.modules.comment.form.CommentForm;
import book.modules.post.Post;
import book.modules.post.PostService;
import book.modules.post.vote.PostVote;
import book.modules.post.vote.PostVoteForm;
import book.modules.post.vote.VoteType;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/comment")
@RequiredArgsConstructor
public class CommentController {

	private final CommentService commentService;
	private final PostService postService;
	
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
	
	@PostMapping("/update/{postId}/{commentId}")
	public String commentUpdateSubmit(@CurrentAccount Account account , Model model , 
									 @Valid CommentForm form , Errors errors, @PathVariable Long postId,
									 @PathVariable Long commentId ) throws NotFoundException {
		
		if (errors.hasErrors()) {
			return "redirect:/post/view/"+form.getPostId();
		}
		
		form.setParentCommentId(commentId);
		
		commentService.updateComment(account,form);
		
		return "redirect:/post/view/"+postId;
	}
	
	@PostMapping("/delete")
	@ResponseBody
	public ResponseEntity<String> commentDelete(@CurrentAccount Account account, Model model, Long commentId , Long postId) throws JsonProcessingException, NotFoundException {
	
		String message = commentService.delete(commentId,postId,account);

		
		if (message.indexOf("댓글이 존재하지 않습니다.") != -1 || message.indexOf("글이 존재하지 않습니다.") != -1 ) {
			return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
		}
		
		return new ResponseEntity<>(message , HttpStatus.OK);
	}
	
	
	@PostMapping("/vote/{type}")
	@ResponseBody
	public ResponseEntity<String> commentUpAndDown(@CurrentAccount Account account, Model model, @PathVariable("type") VoteType type , Long commentId , Long postId) throws JsonProcessingException, NotFoundException {
	
		String message = commentService.vote(commentId,postId,account,type);

		
		if (message.indexOf("댓글이 존재하지 않습니다.") != -1) {
			return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
		}
		
		return new ResponseEntity<>(message , HttpStatus.OK);
	}
}
