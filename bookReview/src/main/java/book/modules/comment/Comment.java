package book.modules.comment;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import book.modules.account.AccountGender;
import book.modules.account.AccountType;
import book.modules.base.BaseEntity;
import book.modules.post.Post;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(of = "id" , callSuper = true)
@Entity @AllArgsConstructor @NoArgsConstructor(access = AccessLevel.PROTECTED)
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
