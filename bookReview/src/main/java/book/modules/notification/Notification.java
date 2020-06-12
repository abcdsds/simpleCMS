package book.modules.notification;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import book.modules.account.Account;
import book.modules.board.Board;
import book.modules.menu.Menu;
import book.modules.menu.MenuType;
import book.modules.menu.Menu.MenuBuilder;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(of = "id")
@Builder @Getter
@Entity @AllArgsConstructor @NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification {

	@Id @GeneratedValue
	private Long id;
	
	private String message;
	
	@Enumerated(EnumType.STRING)
	private NotificationType type;
	
	private String link;
	
	@JoinColumn(name = "account_id")
	@ManyToOne(fetch = FetchType.LAZY)
    private Account account;

    private LocalDateTime createdDateTime;
	
}
