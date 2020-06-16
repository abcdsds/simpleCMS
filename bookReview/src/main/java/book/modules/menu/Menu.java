package book.modules.menu;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import book.modules.board.Board;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(of = "id")
@Builder @Getter
@Entity @AllArgsConstructor @NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Menu {

	@Id @GeneratedValue
	@Column(name = "menu_id")
	private Long id;
		
	@Builder.Default
	@OneToMany(mappedBy = "parent")
	private Set<Menu> subMenus = new LinkedHashSet<Menu>();
	
	@JoinColumn(name = "parent_id")
	@ManyToOne
	private Menu parent;
	
	@JoinColumn(name = "board_id")
	@ManyToOne
	private Board board;
	
	private String name;
	
	private String path;
	
	@Enumerated(EnumType.STRING)
	private MenuType type;
	
	private SimpleGrantedAuthority role;
	
	public void addParentAndSub(Menu parent) {
		this.parent = parent;
		parent.getSubMenus().add(this);
	}
	
	public void addBoardAndRoleAndPath(Board board, SimpleGrantedAuthority role, String path) {
		this.board = board;
		this.role = role;
		this.path = path;
	}

	public void addPathAndRole(String path , SimpleGrantedAuthority role) {
		// TODO Auto-generated method stub
		this.path = path;
		this.role = role;
		
	}
}
