package book.modules.menu;

import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

public interface MenuRepositoryExtension {
	
	List<Menu> findAllByWithOutId(Long id);
	
	List<Menu> findMenuWithoutSubMenus();

	List<Menu> findAllByRole(SimpleGrantedAuthority role);
}
