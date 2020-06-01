package book.modules.post.form;

import lombok.Data;

@Data
public class PostPrevNextForm {

	private Long prevPostId;
	
	private Long nextPostId;
}
