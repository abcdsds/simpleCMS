package book.modules.post;

import java.nio.file.AccessDeniedException;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import book.modules.account.Account;
import book.modules.account.CurrentAccount;
import book.modules.post.form.PostForm;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/post")
@Controller
public class PostController {

	private final PostRepository postRepository;
	private final PostService postService;
	private final ModelMapper modelMapper;
	
	@GetMapping("/add")
	public String postAddForm(@CurrentAccount Account account, Model model) {
		
		model.addAttribute(account);
		model.addAttribute(new PostForm());
		
		return "post/add";
	}
	
	@PostMapping("/add")
	public String postAddSubmit(@CurrentAccount Account account, Model model,
								@Valid PostForm form, Errors errors,
								RedirectAttributes redirect) {
		
		if (errors.hasErrors()) {
			model.addAttribute(account);
			return "post/add";
		}
		
		postService.add(account, form);
		
		return "redirect:/post/list";
	}
	
	
	@GetMapping("/view/{id}")
	public String view(@CurrentAccount Account account, Model model, @PathVariable Long id) {
	
		Post post = postService.getPost(id);
		
		model.addAttribute("post" , post);
		model.addAttribute(account);
		
		return "post/view";
	}
	
	@GetMapping("/update/{id}")
	public String updateForm(@CurrentAccount Account account, Model model, @PathVariable Long id) throws AccessDeniedException {
				
		Post post = postService.getPostWithAccount(id,account);
		PostForm map = modelMapper.map(post, PostForm.class);
		
		model.addAttribute(map);
		model.addAttribute(account);
		model.addAttribute("post_id" , id);
		
		return "post/update";
	}
	
	@PostMapping("/update/{id}")
	public String updateSubmit(@CurrentAccount Account account, Model model,
								@Valid PostForm form, Errors errors,
								RedirectAttributes redirect, @PathVariable Long id) throws AccessDeniedException {
		
		if (errors.hasErrors()) {
			return "redirect:/post/update/"+id;
		}
		
		Post post = postService.getPostWithAccount(id,account);
		postService.updatePost(post , form);
		
		return "redirect:/post/view/"+id;
	}
}
