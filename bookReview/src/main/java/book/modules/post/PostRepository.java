package book.modules.post;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import book.modules.account.Account;


@Transactional(readOnly = true)
public interface PostRepository extends JpaRepository<Post, Long>{

	Post findByIdAndCreatedBy(Long id, Account account);


}
