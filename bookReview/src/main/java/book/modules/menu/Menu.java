package book.modules.menu;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import book.modules.account.Account;
import book.modules.board.Board;
import book.modules.board.manager.BoardManager;
import book.modules.board.manager.BoardManager.BoardManagerBuilder;
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
	
	private String name;
	
	private String path;
}
