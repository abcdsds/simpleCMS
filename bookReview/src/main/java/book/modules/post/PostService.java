package book.modules.post;

import java.nio.file.AccessDeniedException;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import book.modules.account.Account;
import book.modules.account.AccountRepository;
import book.modules.post.form.PostForm;
import book.modules.post.vote.PostVote;
import book.modules.post.vote.PostVoteForm;
import book.modules.post.vote.PostVoteRepository;
import book.modules.post.vote.VoteType;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class PostService {

	private final PostRepository postRepository;
	private final PostVoteRepository postVoteRepository; 
	private final AccountRepository accountRepository;
	private final ModelMapper modelMapper;
	private final ObjectMapper objectMapper;

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

	public Post getPostWithAccount(Long id, Account account,boolean deleted) throws AccessDeniedException {
		// TODO Auto-generated method stub
		Post findByIdAndAccount = postRepository.findByIdAndCreatedByAndDeleted(id,account,deleted);
		
		if (findByIdAndAccount == null) {
			throw new AccessDeniedException("잘못된 접근입니다.");
		}
		return findByIdAndAccount;
	}
	public void updatePost(Post post, PostForm form) {
		// TODO Auto-generated method stub
		modelMapper.map(form, post);
	}

	public void UpdateDeleteStatus(Post post) {
		// TODO Auto-generated method stub
		post.updateDeleteStatus(true);
	}

	public PostVote createVote(Post post, VoteType voteType) {
		PostVote postVote = PostVote.builder().post(post).voteType(voteType).build();
		return postVote;
	}
	
	public String vote(Account account, Post post, VoteType voteType) throws JsonProcessingException {
		// TODO Auto-generated method stub
		PostVoteForm form = new PostVoteForm();
		
		PostVote findByPostAndCreatedBy = postVoteRepository.findByPostAndCreatedBy(post, account);
		if (findByPostAndCreatedBy != null) {
			form.setMessage("이미 추천하셨습니다.");			
			form.setVoteCount(voteType.equals(VoteType.up) ? post.getUp() : post.getDown());
			return objectMapper.writeValueAsString(form);
		}
		
		findByPostAndCreatedBy = createVote(post,voteType);
		postVoteRepository.save(findByPostAndCreatedBy);
		
		if (voteType.equals(VoteType.up)) {
			post.voteUp();
		} else {
			post.voteDown();
		}
		
		form.setMessage("추천 되었습니다.");			
		form.setVoteCount(voteType.equals(VoteType.up) ? post.getUp() : post.getDown());
		
		
		
		return objectMapper.writeValueAsString(form);
	}
	
	
}
