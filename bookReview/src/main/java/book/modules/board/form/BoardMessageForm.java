package book.modules.board.form;

import book.modules.simple.SimpleMessageType;
import lombok.Data;

@Data
public class BoardMessageForm {

	private String message;
	private SimpleMessageType messageType;
}
