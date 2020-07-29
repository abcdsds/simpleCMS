package book.modules.account.form;

import book.modules.simple.SimpleMessageType;
import lombok.Data;

@Data
public class AccountMessageForm {

	private String message;
	private SimpleMessageType type;
}
