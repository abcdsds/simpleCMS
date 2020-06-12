package book.modules.comment;

import java.util.Optional;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import book.modules.account.Account;
import book.modules.account.AccountRepository;
import book.modules.notification.Notification;
import book.modules.notification.NotificationRepository;
import book.modules.notification.NotificationType;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Async
@Component
@Transactional
@RequiredArgsConstructor
public class CommentEventListener {

	private final CommentRepository commentRepository;
	private final AccountRepository accountRepository;
	private final NotificationRepository notificationRepository;
	
	@EventListener
	public void handleCommentEvent(CommentEvent commentEvent) throws NotFoundException {
		
		Comment parentComment = commentRepository.findCommentById(commentEvent.getComment().getId());		
		
		log.info("==== ========== {} ================" , parentComment == null);
		
		if (parentComment != null) {
			
			Long accountId = parentComment.getCreatedBy().getId();
			
			Optional<Account> findById = accountRepository.findById(accountId);
			
			Account account = findById.orElseThrow(() -> new NotFoundException("아이디가 존재하지 않습니다."));
			
			Notification notification = Notification.builder()
											.message("댓글이 달렸습니다.")
											.link("/post/view/" + parentComment.getPost().getId())
											.type(NotificationType.COMMENT_REPLY)
											.account(account)
											.build();
			
			notificationRepository.save(notification);
		}
		
	}
}
