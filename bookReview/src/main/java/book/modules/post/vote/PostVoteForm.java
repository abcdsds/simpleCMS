package book.modules.post.vote;

import lombok.Data;

@Data
public class PostVoteForm {

	private int voteCount;
	
	private String message;
}
