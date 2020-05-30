package book.modules.comment;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import book.modules.base.BaseEntity;
import book.modules.post.Post;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@EqualsAndHashCode(of = "id" , callSuper = true)
@Entity @AllArgsConstructor @NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter @Setter @Builder
public class Comment extends BaseEntity {

	@Id @GeneratedValue
	@Column(name = "comment_id")
	private Long id;
	
	private String content;
	
	private int up;
	
	private int down;
	
	private boolean best;
	
	private boolean deleted;
	
	private int depth;
	
	@JoinColumn(name = "post_id")
	@ManyToOne
	private Post post;
	
	@ManyToOne
	@JoinColumn(name = "parent_id")
	private Comment parent;
	
	@OneToMany(mappedBy = "parent")
	private List<Comment> childList = new ArrayList<Comment>();
	
	public void updateParent (Comment parent) {
		this.parent = parent;
	}
	
	public void updateChild (Comment child) {
		this.childList.add(child);
	}
}
