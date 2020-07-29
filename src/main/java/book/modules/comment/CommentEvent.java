package book.modules.comment;

import org.springframework.context.ApplicationEvent;

import lombok.Data;
import lombok.Getter;

@Getter
public class CommentEvent {

	private Comment comment;
	
	public CommentEvent(Comment comment) {
		this.comment = comment;
	}

}
