package book.modules.account;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
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
import lombok.Setter;


@EqualsAndHashCode(of = "id" , callSuper = true)
@Getter @Setter
@Builder
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
	
	@Enumerated(EnumType.STRING)
	private AccountType accountType;
	
	@Enumerated(EnumType.STRING)
	private AccountGender accountGender;
	
	private String password;
	
	private String token;
	
	private LocalDateTime tokenGeneratedAt;
	
	@Lob @Basic(fetch = FetchType.LAZY)
	private String profileImage;
	
	@OneToMany(mappedBy = "createdBy")
	private List<Post> posts = new ArrayList<Post>();
	
	@OneToMany(mappedBy = "createdBy")
	private List<Comment> comments = new ArrayList<Comment>();

	public void changePassword(String encode) {
		// TODO Auto-generated method stub
		this.password = encode;
	}

	public void changeNickname(String nickname) {
		// TODO Auto-generated method stub
		this.nickname = nickname;
	}

	public void changeProfileImage(String profileImage) {
		// TODO Auto-generated method stub
		this.profileImage = profileImage;
	}
	
	public void generateEmailCheckToken() {
		// TODO Auto-generated method stub
		this.token = UUID.randomUUID().toString();
		this.tokenGeneratedAt = LocalDateTime.now();
	}

	
}
