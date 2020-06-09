package book.modules.admin;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import book.modules.comment.Comment;
import book.modules.comment.CommentService;
import book.modules.comment.form.CommentForm;
import book.modules.comment.form.CommentListForm;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestMapping("/admin")
@RequiredArgsConstructor
@Controller
public class AdminCommentController {

	private final AdminService adminService;
	private final CommentService commentService;
	private final ModelMapper modelMapper;
	
	@GetMapping("/comment")
	public String adminCommentManagement(Model model, @PageableDefault(page = 0, size = 10, sort = "createdAt" , direction = Sort.Direction.DESC) Pageable pageable, String keyword) {
	
		if (keyword == null) {
			keyword = "";
		}
		
		Page<CommentListForm> commentPaging = adminService.getCommentPaging(keyword, pageable);
				
		model.addAttribute("keyword", keyword);
		model.addAttribute("resultList", commentPaging);
		
		return "admin/comment/comment";
	}
	
	@GetMapping("/comment/update/{id}")
	public String adminCommentManageUpdateForm(Model model, @PathVariable Long id) throws NotFoundException {
		
		Comment comment = commentService.getComment(id);
		CommentForm map = modelMapper.map(comment, CommentForm.class);

		map.setPostId(comment.getPost().getId());
		model.addAttribute(map);
		
		return "admin/comment/update";
	}
	
	@PostMapping("/comment/update")
	public String adminCommentManagementUpdateSubmit(Model model, @Valid CommentForm commentForm , Errors errors , RedirectAttributes redirect) throws NotFoundException {
		
		if (errors.hasErrors()) {
			model.addAttribute("message", "수정에 실패했습니다.");
			return "admin/comment/update";
		}
		
		
		commentService.updateComment(commentForm);
		
		redirect.addFlashAttribute("message", "수정에 성공했습니다.");
		return "redirect:/admin/comment/update/" + commentForm.getId();
	}
}
