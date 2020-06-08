package book.modules.menu;

import java.util.List;

public interface MenuRepositoryExtension {
	
	List<Menu> findAllByWithOutId(Long id);

}
