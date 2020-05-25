package book.modules.account;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import book.modules.base.BaseTimeEntity;
import book.modules.comment.Comment;
import book.modules.post.Post;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;


@EqualsAndHashCode(of = "id" , callSuper = true)
@Getter @Builder
@Entity @AllArgsConstructor @NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Account extends BaseTimeEntity{

	@Id @GeneratedValue
	@Column(name = "account_id")
	private Long id;
	
	@Column(unique = true, nullable = false)
	private String loginId;
		
	@Column(unique = true, nullable = false)
	private String email;
	
	@Column(nullable = false)
	private String nickname;
	
	private boolean ban;
	
	private int birthYear;
	
	private String role;
	
	private AccountType accountType;
	
	private AccountGender accountGender;
	
	private String password;
	
	@OneToMany(mappedBy = "createdBy")
	private List<Post> posts = new ArrayList<Post>();
	
	@OneToMany(mappedBy = "createdBy")
	private List<Comment> comments = new ArrayList<Comment>();

	
}
