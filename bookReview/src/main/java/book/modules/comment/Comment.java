package book.modules.comment;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import book.modules.base.BaseEntity;
import book.modules.post.Post;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(of = "id" , callSuper = true)
@Entity
public class Comment extends BaseEntity {

	@Id @GeneratedValue
	private Long id;
	
	private String content;
	
	private int up;
	
	private int down;
	
	private boolean best;
	
	private boolean deleted;
	
	@JoinColumn(name = "post_id")
	@ManyToOne()
	private Post post;
}
