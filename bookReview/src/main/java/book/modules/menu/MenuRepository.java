package book.modules.menu;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;


@Transactional(readOnly = true)
public interface MenuRepository extends JpaRepository<Menu, Long> , MenuRepositoryExtension{

	@EntityGraph(attributePaths = {"parent" , "board" , "subMenus"})
	List<Menu> findAll();

	@EntityGraph(attributePaths = {"subMenus"})
	Menu findMenuAndSubMenuById(Long menuId);

	Menu findTop1ByOrderByIdDesc();
	

}
