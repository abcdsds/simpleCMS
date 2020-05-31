package book.modules.post.vote;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import book.modules.base.BaseEntity;
import book.modules.comment.Comment;
import book.modules.post.Post;
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
public class PostVote extends BaseEntity {

	@Id @GeneratedValue
	private Long id;
	
	@JoinColumn(name = "post_id")
	@ManyToOne
	private Post post;
	
	@JoinColumn(name = "comment_id")
	@ManyToOne
	private Comment comment;
	
	@Enumerated(EnumType.STRING)
	private VoteType voteType;
	
	
	
	
}
