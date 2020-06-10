package book.modules.menu;

import java.util.List;

import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

public class MenuRepositoryExtensionImpl extends QuerydslRepositorySupport implements MenuRepositoryExtension{

	private final JPAQueryFactory queryFactory;
	
	public MenuRepositoryExtensionImpl(JPAQueryFactory jpaQueryFactory) {
		super(Menu.class);
		this.queryFactory = jpaQueryFactory;
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<Menu> findAllByWithOutId(Long id) {
		// TODO Auto-generated method stub
		
		QMenu menu = QMenu.menu;
		QMenu subMenu = new QMenu("subMenu");
		QMenu subParentMenu = new QMenu("subParentMenu");
		
		List<Menu> fetch = queryFactory.selectFrom(menu)
						.join(menu.subMenus , subMenu)
						.on(subMenu.parent.id.ne(id))
						.where(menu.id.ne(id))
						.fetch();
		
		return fetch;
	}

	@Override
	public List<Menu> findMenuWithoutSubMenus() {
		// TODO Auto-generated method stub
		QMenu menu = QMenu.menu;
		QMenu parent = new QMenu("parent");
		
		List<Menu> fetch = queryFactory.selectFrom(menu)
						.leftJoin(menu.parent , parent).fetchJoin()
						.where(parent.isNull())
						.fetch();
		
		return fetch;
	}

	@Override
	public List<Menu> findAllByRole(SimpleGrantedAuthority role) {
		// TODO Auto-generated method stub
		QMenu menu = QMenu.menu;
		QMenu subMenu = new QMenu("subMenu");
		QMenu parent = new QMenu("parent");
		
		List<Menu> fetch = queryFactory.select(menu)
					.from(menu)
					.leftJoin(menu.subMenus , subMenu).fetchJoin()
					.leftJoin(menu.parent, parent).fetchJoin()
					.where(parent.isNull())
					.distinct()
					.fetch();
		
		return fetch;
	}

}
