package book.modules.admin;

import java.util.List;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import book.modules.board.Board;
import book.modules.board.BoardService;
import book.modules.post.Post;
import book.modules.post.PostService;
import book.modules.post.form.PostForm;
import book.modules.post.form.PostListForm;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestMapping("/admin")
@RequiredArgsConstructor
@Controller
public class AdminPostController {

	private final AdminService adminService;
	private final PostService postService;
	private final ModelMapper modelMapper;
	
	@GetMapping("/post")
	public String adminPostManagement(Model model, @PageableDefault(page = 0, size = 10, sort = "createdAt" , direction = Sort.Direction.DESC) Pageable pageable, @RequestParam (defaultValue = "") String keyword) {
	
		if (keyword == null) {
			keyword = "";
		}
		
		Page<PostListForm> postPaging = adminService.getPostPaging(keyword, pageable);
				
		model.addAttribute("keyword", keyword);
		model.addAttribute("postList", postPaging);
		
		return "admin/post/post";
	}
	
	@GetMapping("/post/update/{id}")
	public String adminPostManageUpdateForm(Model model, @PathVariable Long id) throws NotFoundException {
		
		Post post = adminService.getPost(id);
		PostForm map = modelMapper.map(post, PostForm.class);
		map.setBoardName(post.getBoard().getName());
		map.setBoardId(post.getBoard().getId());

		List<Board> allBoards = adminService.getAllBoards();
		
		model.addAttribute("allBoardList", allBoards);
		model.addAttribute(map);
		
		return "admin/post/update";
	}
	
	@PostMapping("/post/update")
	public String adminPostManageUpdateSubmit(Model model, @Valid PostForm postForm, Errors errors, RedirectAttributes redirect) throws NotFoundException {
		
		if (errors.hasErrors()) {
			redirect.addFlashAttribute("message", "수정에 실패했습니다.");
			return "redirect:/admin/post/update/" + postForm.getId();
		}
		
		postService.updatePost(postForm);
		
		redirect.addFlashAttribute("message", "성공적으로 수정됐습니다.");
		return "redirect:/admin/post/update/" + postForm.getId();
	}
}
