package book.modules.comment.form;

import lombok.Data;

@Data
public class CommentDeleteForm {

	private String message;
	private DeleteType messageType;
}
