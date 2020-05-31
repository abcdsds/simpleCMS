package book.modules.post.vote;

import lombok.Data;

@Data
public class PostVoteForm {

	private int voteUpCount;
	
	private int voteDownCount;
	
	private String message;

}
