package book.modules.comment;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import book.modules.account.Account;
import book.modules.comment.form.CommentForm;
import book.modules.post.Post;
import book.modules.post.PostRepository;
import book.modules.post.vote.PostVote;
import book.modules.post.vote.PostVoteForm;
import book.modules.post.vote.PostVoteRepository;
import book.modules.post.vote.VoteType;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {

	private final PostRepository postRepository;
	private final CommentRepository commentRepository;
	private final PostVoteRepository postVoteRepository; 
	private final ObjectMapper objectMapper;
	
	public void addComment(Account account, CommentForm form) throws NotFoundException {
		// TODO Auto-generated method stub
		Optional<Post> findById = postRepository.findById(form.getPostId());
				
		Post post = findById.orElseThrow(() -> new NotFoundException("글이 존재하지 않습니다."));
		
		
		
		Comment comment = Comment.builder()
									.best(false)
									.content(form.getContent())
									.depth(form.getDepth())
									.down(0)
									.up(0)
									.post(post)
									.build();

		
		if (form.getDepth() > 0) {
			Optional<Comment> findComment = commentRepository.findById(form.getParentCommentId());
			Comment orElseComment = findComment.orElseThrow(() -> new NotFoundException("댓글이 존재하지 않습니다."));
			
			comment.updateParent(orElseComment);
			orElseComment.updateChild(comment);
			comment.updateGroup(orElseComment.getGroup());
		} else {
			comment.updateGroup(comment);
		}
	
		commentRepository.save(comment);
	}

	public List<Comment> getCommentList(Post post) {
		// TODO Auto-generated method stub		
		return commentRepository.findAllByPostAndDeletedAndDepthOrderByIdAsc(post,false,0);
	}
	
	public PostVote createVote(Comment comment, VoteType voteType) {
		PostVote postVote = PostVote.builder().comment(comment).voteType(voteType).build();
		return postVote;
	}
	

	public String vote(Long commentId, Long postId, Account account, VoteType type) throws NotFoundException, JsonProcessingException {
		// TODO Auto-generated method stub
		
		PostVoteForm form = new PostVoteForm();
		
		Optional<Comment> findById = commentRepository.findById(commentId);
		
		if (!findById.isPresent()) {
			form.setMessage("댓글이 존재하지 않습니다.");
			return objectMapper.writeValueAsString(form);
		}
		
		Comment findComment = findById.orElseThrow(null);
		
		PostVote findByCommentAndCreatedBy = postVoteRepository.findByCommentAndCreatedBy(findComment, account);
		
		if (findByCommentAndCreatedBy != null) {
			form.setMessage("이미 추천했습니다.");
			form.setVoteUpCount(findComment.getUp());
			form.setVoteDownCount(findComment.getDown());
			return objectMapper.writeValueAsString(form);
		}
		
		findByCommentAndCreatedBy = createVote(findComment,type);
		postVoteRepository.save(findByCommentAndCreatedBy);
		
		if (type.equals(VoteType.UP)) {
			findComment.voteUp();
		} else {
			findComment.voteDown();
		}
		
		form.setMessage("추천 되었습니다.");			
		form.setVoteUpCount(findComment.getUp());
		form.setVoteDownCount(findComment.getDown());
		
		return objectMapper.writeValueAsString(form);
		
	}

}
