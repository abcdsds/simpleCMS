package book.modules.post.form;

import javax.validation.constraints.NotBlank;

import org.hibernate.validator.constraints.Length;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class PostForm {

	@NotBlank
    @Length(max = 50)
	private String title;
	
	@NotBlank
	private String content;
	
	@NotBlank
	private String boardName;

}
