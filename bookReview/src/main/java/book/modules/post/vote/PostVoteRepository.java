package book.modules.post.vote;

import org.springframework.data.jpa.repository.JpaRepository;

import book.modules.account.Account;
import book.modules.post.Post;

public interface PostVoteRepository extends JpaRepository<PostVote, Long>{

	PostVote findByPost(Post post);

	PostVote findByPostAndCreatedBy(Post post, Account account);

}
