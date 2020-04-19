package curso.api.rest.service;

import java.util.Properties;

import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.stereotype.Service;

@Service
public class EnvioEmailService {
	private String userName = "akirauekita2002@gmail.com";
	private String senha = "Kadeira2.0";
	public void enviarEmail(String assunto, String email,String mensagem) throws MessagingException {
		Properties propertie = new Properties();
		propertie.put("mail.smtp.ssl.trust", "*");
		propertie.put("mail.smtp.auth","true");
		propertie.put("mail.smtp.starttls", "true");
		propertie.put("mail.smtp.host", "smtp.gmail.com");
		propertie.put("mail.smtp.port", "465");
		propertie.put("mail.smtp,socketFactory.port", "465");
		propertie.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		
		Session session = Session.getInstance(propertie, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				// TODO Auto-generated method stub
				return new PasswordAuthentication(userName, senha);
			}
		});
		Address[] toUser = InternetAddress.parse(email);
		Message message = new MimeMessage(session);
		message.setFrom(new InternetAddress(userName));
		message.setRecipients(Message.RecipientType.TO, toUser);
		message.setSubject(assunto);
		message.setText(mensagem);
		Transport.send(message);
	}
}
