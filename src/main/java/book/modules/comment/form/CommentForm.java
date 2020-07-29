package book.modules.comment.form;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import com.querydsl.core.annotations.QueryProjection;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentForm {
		
	private Long id;
	
	private Long postId;
	
	private Long parentCommentId;
	
	@NotNull
	private int depth;
	
	@Length(max = 50)
	@NotBlank
	private String content;
	
	private boolean deleted = false;

	
	
}
