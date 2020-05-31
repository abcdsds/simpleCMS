package book.modules.post;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import book.modules.account.Account;
import book.modules.base.BaseEntity;
import book.modules.board.Board;
import book.modules.comment.Comment;
import book.modules.post.vote.PostVote;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@EqualsAndHashCode(of = "id" , callSuper = true)
@Builder @Getter @Setter
@Entity @AllArgsConstructor @NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post extends BaseEntity {

	@Id @GeneratedValue
	@Column(name = "post_id")
	private Long id;
	
	private int views;
	
	private int up;
	
	private int down;
	
	private boolean best;
	
	private LocalDateTime bestDate;
	
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
	
	@OneToMany(mappedBy = "post")
	private List<PostVote> voteList = new ArrayList<PostVote>();
	
	public void updateDeleteStatus(boolean b) {
		// TODO Auto-generated method stub
		this.deleted = b;
	}

	public void voteUp() {
		// TODO Auto-generated method stub
		this.up++;
		if (up - down > 10) {
			best = true;
			bestDate = LocalDateTime.now();
		}
	}
	
	public void voteDown() {
		// TODO Auto-generated method stub
		this.down++;
		if (down - up > 20) {
			deleted = true;
		}
	}

	public void increaseView() {
		// TODO Auto-generated method stub
		this.views++;
	}
	
	
	
}
