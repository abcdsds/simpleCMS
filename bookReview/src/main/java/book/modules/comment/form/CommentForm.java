package book.modules.comment.form;

import lombok.Data;

@Data
public class CommentForm {
	
	private Long postId;
	
	private Long parentCommentId;
	
	private int depth;
	
	private String content;
	
}
