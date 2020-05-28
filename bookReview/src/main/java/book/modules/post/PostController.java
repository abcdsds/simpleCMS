package book.modules.post;

import javax.validation.Valid;

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
}
