package com.ozu.chat.auth;

import com.ozu.chat.config.AppProperties;
import com.ozu.chat.user.model.User;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

	private final JavaMailSender mailSender;
	private final AppProperties appProperties;

	public EmailService(JavaMailSender mailSender, AppProperties appProperties) {
		this.mailSender = mailSender;
		this.appProperties = appProperties;
	}

	public void sendVerificationEmail(User user, String rawToken) {
		String verifyUrl = appProperties.app().frontendUrl() + "/verify-email?token=" + rawToken;
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(user.getEmail());
		message.setSubject("Verify your Ozu Chat account");
		message.setText("""
				Welcome to Ozu Chat, %s.

				Verify your account using this link:
				%s

				This link expires soon. If you did not create this account, ignore this email.
				""".formatted(user.getDisplayName(), verifyUrl));
		try {
			mailSender.send(message);
		} catch (MailException exception) {
			// Local development often runs without SMTP. The API still returns the dev token.
		}
	}
}
