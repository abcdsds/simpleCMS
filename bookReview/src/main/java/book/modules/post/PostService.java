package book.modules.post;

import java.util.Optional;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import book.modules.account.Account;
import book.modules.account.AccountRepository;
import book.modules.post.form.PostForm;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class PostService {

	private final PostRepository postRepository;
	private final AccountRepository accountRepository;

	public void add(Account account, PostForm form) {
		// TODO Auto-generated method stub
		
		Optional<Account> findById = accountRepository.findById(account.getId());
		
		if (!findById.isPresent()) {
			
			throw new UsernameNotFoundException("사용자가 존재하지 않습니다.");
		}
		
		Account getAccount = findById.orElseThrow(null);

		Post post = Post.builder()
						 .best(false)
						 .title(form.getContent())
						 .content(form.getContent())
						 .down(0)
						 .up(0)
						 .lock(false)
						 .views(0)
						 .deleted(false)
						 .build();
		
		Post save = postRepository.save(post);
		
		getAccount.getPosts().add(save);
		
		
	}

	public Post getPost(Long id) {
		// TODO Auto-generated method stub
		return postRepository.findById(id).orElseThrow(null);
	}
	
	
}
