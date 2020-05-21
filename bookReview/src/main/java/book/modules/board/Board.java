package book.modules.board;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(of = "id")
@Entity
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
