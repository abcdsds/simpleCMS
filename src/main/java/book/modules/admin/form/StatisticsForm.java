package book.modules.admin.form;

import java.util.List;

import book.modules.account.Account;
import book.modules.comment.Comment;
import book.modules.post.Post;
import lombok.Data;

@Data
public class StatisticsForm {
	
	private Integer month;
	private Long count;

}
