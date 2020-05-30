package book.modules.comment;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import book.modules.account.Account;
import book.modules.comment.form.CommentForm;
import book.modules.post.Post;
import book.modules.post.PostRepository;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {

	private final PostRepository postRepository;
	private final CommentRepository commentRepository;
	
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
		}
	
		commentRepository.save(comment);
	}

}
