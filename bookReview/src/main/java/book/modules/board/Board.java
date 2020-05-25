package book.modules.board;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import book.modules.account.AccountGender;
import book.modules.account.AccountType;
import book.modules.comment.Comment;
import book.modules.post.Post;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(of = "id")
@Entity @AllArgsConstructor @NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Board {
	
	@Id @GeneratedValue
	@Column(name = "board_id")
	private Long id;
	
	private String name;
	
	private SimpleGrantedAuthority role;
	
	@ManyToOne
	private Board parent;
	
	@OneToMany(mappedBy = "parent")
	private List<Board> child;
	

}
