package book.modules.menu.form;

import book.modules.simple.SimpleMessageType;
import lombok.Data;

@Data
public class MenuMessageForm {

	private String message;
	
	private SimpleMessageType type;
}
