package book.modules.menu.form;

import book.modules.board.form.BoardMessageType;
import lombok.Data;

@Data
public class MenuMessageForm {

	private String message;
	
	private BoardMessageType type;
}
