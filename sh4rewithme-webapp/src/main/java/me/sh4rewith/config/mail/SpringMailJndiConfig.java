package me.sh4rewith.config.mail;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

@Configuration
@Profile("prod-mail")
public class SpringMailJndiConfig {

	@Bean
	public MailSender mailSender() throws MailException, MessagingException, NamingException, UnknownHostException {
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		mailSender.setSession(jndiMailSession());
		mailSender.send(initMessage(mailSender));
		return mailSender;
	}

	private MimeMessage initMessage(JavaMailSenderImpl mailSender) throws MessagingException, UnknownHostException {
		final MimeMessage mimeMessage = mailSender.createMimeMessage();
		final MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
		message.setFrom("admin@sh4rewith.me");
		message.setTo("admin@sh4rewith.me");
		message.setSubject("Sh4rewith.me: Application Bootstrap");
		message.setText("Application started: " + new SimpleDateFormat().format(new Date()) + " from " + InetAddress.getLocalHost().getHostName());
		return mimeMessage;
	}

	@Bean
	public Session jndiMailSession() throws NamingException {
		Context initCtx = new InitialContext();
		Context envCtx = (Context) initCtx.lookup("java:comp/env");
		return (Session) envCtx.lookup("mail/Session");
	}
}