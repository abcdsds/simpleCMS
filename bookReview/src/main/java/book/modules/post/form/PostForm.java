package book.modules.post.form;

import javax.validation.constraints.NotBlank;

import org.hibernate.validator.constraints.Length;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostForm {

	private Long id;
	
	@NotBlank
    @Length(max = 50)
	private String title;
	
	@NotBlank
	private String content;
	
	private Long boardId;
	
	private String boardName;
	
	private boolean deleted = false;

}
