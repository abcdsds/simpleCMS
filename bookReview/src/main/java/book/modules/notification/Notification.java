package book.modules.notification;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import book.modules.account.Account;
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
    
    private boolean checked;

	public void updateChecked(boolean checked) {
		// TODO Auto-generated method stub
		this.checked = checked;
	}
	
}
