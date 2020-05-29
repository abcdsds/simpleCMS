package book.modules.post.vote;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import book.modules.account.Account;
import book.modules.base.BaseEntity;
import book.modules.base.BaseTimeEntity;
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
	
	private VoteType voteType;
	
	
	
	
}
