package book.modules.post;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import book.modules.base.BaseEntity;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(of = "id" , callSuper = true)
@Entity
public class Post extends BaseEntity {

	@Id @GeneratedValue
	@Column(name = "post_id")
	private Long id;
	
	private int views;
	
	private int up;
	
	private int down;
	
	private boolean best;
	
	private boolean lock;
	
	private String category;
	
	private boolean deleted;
	
}
