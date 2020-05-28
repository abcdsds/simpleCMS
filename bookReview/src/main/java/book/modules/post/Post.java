package book.modules.post;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import book.modules.account.Account;
import book.modules.base.BaseEntity;
import book.modules.board.Board;
import book.modules.comment.Comment;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(of = "id" , callSuper = true)
@Builder @Getter
@Entity @AllArgsConstructor @NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post extends BaseEntity {

	@Id @GeneratedValue
	@Column(name = "post_id")
	private Long id;
	
	private int views;
	
	private int up;
	
	private int down;
	
	private boolean best;
	
	private boolean lock;

	private boolean deleted;
	
	private String title;
	
	@Lob
	private String content;
	
	@JoinColumn(name = "board_id")
	@OneToOne(fetch = FetchType.LAZY)
	private Board category = null;

	@OneToMany(mappedBy = "post")
	private List<Comment> comments = new ArrayList<Comment>();
	
	
	
}
