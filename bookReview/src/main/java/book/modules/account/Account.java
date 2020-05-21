package book.modules.account;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import book.modules.base.BaseTimeEntity;
import book.modules.post.Post;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode(of = "id" , callSuper = true)
@Getter @Builder
@Entity
public class Account extends BaseTimeEntity{

	@Id @GeneratedValue
	@Column(name = "account_id")
	private Long id;
	
	@Column(unique = true, nullable = false)
	private String nickname;
	
	@Column(unique = true, nullable = false)
	private String email;
	
	private boolean ban;
	
	private int birthYear;
	
	private SimpleGrantedAuthority role;
	
	private AccountType accountType;
	
	private AccountGender accountGender;
	
	private String password;
	
	@OneToMany(mappedBy = "createdBy")
	private List<Post> posts;
	
	
	
}
