package me.sh4rewith.config.mail;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

@Configuration
@Profile("trap-mail")
@PropertySource("classpath:javamail-trap.properties")
public class SpringMailTrapConfig {
	@Autowired
	private Environment env;

	@Bean
	public MailSender mailSender() throws MailException, MessagingException, NamingException, UnknownHostException {
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		mailSender.setPort(env.getProperty("dev.mail.port", Integer.class));
		mailSender.setHost(env.getProperty("dev.mail.host", String.class));
		mailSender.setUsername(env.getProperty("dev.mail.username", String.class));
		mailSender.setPassword(env.getProperty("dev.mail.password", String.class));
		Properties javaMailProperties = new Properties();
		javaMailProperties.put("mail.debug", env.getProperty("dev.mail.debug", String.class));
		javaMailProperties.put("mail.user", env.getProperty("dev.mail.username", String.class));
		javaMailProperties.put("mail.password", env.getProperty("dev.mail.password", String.class));
		javaMailProperties.put("mail.transport.protocol", env.getProperty("dev.mail.protocol", String.class));
		javaMailProperties.put("mail.smtp.host", env.getProperty("dev.mail.host", String.class));
		javaMailProperties.put("mail.smtp.port", env.getProperty("dev.mail.port", String.class));
		javaMailProperties.put("mail.smtp.auth", env.getProperty("dev.mail.auth", String.class));
		javaMailProperties.put("mail.smtp.starttls.enable", env.getProperty("dev.mail.starttls.enable", String.class));
		mailSender.setJavaMailProperties(javaMailProperties);
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
	public Session mailSession() throws NamingException {
		Context initCtx = new InitialContext();
		Context envCtx = (Context) initCtx.lookup("java:comp/env");
		return (Session) envCtx.lookup("mail/Session");
	}
}