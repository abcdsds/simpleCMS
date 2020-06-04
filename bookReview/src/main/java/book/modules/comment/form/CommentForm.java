package book.modules.comment.form;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import lombok.Data;

@Data
public class CommentForm {
	
	@NotNull
	private Long postId;
	
	private Long parentCommentId;
	
	@NotNull
	private int depth;
	
	@Length(max = 50)
	@NotBlank
	private String content;
	
}
