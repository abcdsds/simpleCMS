package book.modules.post.form;

import book.modules.comment.form.DeleteType;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PostDeleteForm {

	private String message;
	
	private DeleteType type;
}
