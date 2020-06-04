package book.modules.board;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import book.modules.board.manager.BoardManager;
import book.modules.post.Post;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(of = "id")
@Getter @Builder
@Entity @AllArgsConstructor @NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Board {
	
	@Id @GeneratedValue
	@Column(name = "board_id")
	private Long id;
	
	@Column(unique = true , nullable = false)
	private String name;
	
	@Column(unique = true , nullable = false)
	private String path;
	
	private SimpleGrantedAuthority role;
		
	@Builder.Default
	@OneToMany(mappedBy = "board")
	private Set<BoardManager> managers = new HashSet<BoardManager>();
	
	@Builder.Default
	@OneToMany(mappedBy = "board")
	private Set<Post> postList = new LinkedHashSet<Post>();


}
