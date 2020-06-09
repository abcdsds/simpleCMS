package book.modules.comment.form;

import java.time.LocalDateTime;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import com.querydsl.core.annotations.QueryProjection;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CommentListForm {

	private Long id;
		
	@NotNull
	private Long postId;
		
	@Length(max = 50)
	@NotBlank
	private String content;
	
	private LocalDateTime createdAt;
	
	private String createdBy;

	@QueryProjection
	public CommentListForm(Long id, Long postId, String content,
			LocalDateTime createdAt, String createdBy) {
		this.id = id;
		this.postId = postId;
		this.content = content;
		this.createdAt = createdAt;
		this.createdBy = createdBy;
	}
	
	
}
