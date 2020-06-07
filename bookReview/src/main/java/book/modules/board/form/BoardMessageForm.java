package book.modules.board.form;

import lombok.Data;

@Data
public class BoardMessageForm {

	private String message;
	private BoardMessageType messageType;
}
