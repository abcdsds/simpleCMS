package book.mail;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.context.annotation.Profile;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class HtmlEmailService implements EmailService {

	private final JavaMailSender javaMailSender;
	
	@Override
	public void sendEmail(Email email) {
		// TODO Auto-generated method stub
		MimeMessage mimeMessage = javaMailSender.createMimeMessage();
		
		try {
			MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false , "UTF-8");
			mimeMessageHelper.setTo(email.getTo());
			mimeMessageHelper.setSubject(email.getSubject());
			mimeMessageHelper.setText(email.getMessage() , true);
			
			javaMailSender.send(mimeMessageHelper.getMimeMessage());
			
			log.info("sent email: {}", email.getMessage());
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			log.error("failed to send mail" , e);
			throw new RuntimeException(e);
		}
	}

}
