package com.ozu.chat.auth;

import com.ozu.chat.config.AppProperties;
import com.ozu.chat.user.model.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

	private static final Logger log = LoggerFactory.getLogger(EmailService.class);

	private final JavaMailSender mailSender;
	private final AppProperties appProperties;
	private final String fromAddress;

	public EmailService(JavaMailSender mailSender, AppProperties appProperties,
						@Value("${spring.mail.username}") String fromAddress) {
		this.mailSender = mailSender;
		this.appProperties = appProperties;
		this.fromAddress = fromAddress;
	}

	public void sendVerificationEmail(User user, String rawToken) {
		String verifyUrl = appProperties.app().frontendUrl() + "/verify-email?token=" + rawToken;
		String plainText = buildPlainText(user.getDisplayName(), verifyUrl);
		String html = buildHtml(user.getDisplayName(), verifyUrl);

		try {
			MimeMessage mimeMessage = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
			helper.setFrom("Ozu Chat <" + fromAddress + ">");
			helper.setTo(user.getEmail());
			helper.setSubject("Verify your Ozu Chat account");
			helper.setText(plainText, html);
			mailSender.send(mimeMessage);
		} catch (MailException | MessagingException exception) {
			log.warn("Failed to send verification email to {}: {}", user.getEmail(), exception.getMessage());
		}
	}

	private String buildPlainText(String displayName, String verifyUrl) {
		return """
                Welcome to Ozu Chat, %s.

                Verify your account using this link:
                %s

                This link expires soon. If you did not create this account, ignore this email.
                """.formatted(displayName, verifyUrl);
	}

	private String buildHtml(String displayName, String verifyUrl) {
		return """
                <!DOCTYPE html>
                <html lang="vi">
                <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Verify your Ozu Chat account</title>
                </head>
                <body style="margin:0; padding:0; background-color:#f4f5f7; font-family:-apple-system,BlinkMacSystemFont,'Segoe UI',Roboto,Helvetica,Arial,sans-serif;">
                  <table role="presentation" width="100%%" cellpadding="0" cellspacing="0" style="background-color:#f4f5f7; padding:40px 0;">
                    <tr>
                      <td align="center">
                        <table role="presentation" width="560" cellpadding="0" cellspacing="0" style="background-color:#ffffff; border-radius:12px; overflow:hidden; box-shadow:0 1px 3px rgba(0,0,0,0.08);">
                          <tr>
                            <td style="background-color:#6366f1; padding:28px 40px;">
                              <span style="color:#ffffff; font-size:20px; font-weight:700; letter-spacing:0.3px;">Ozu Chat</span>
                            </td>
                          </tr>
                          <tr>
                            <td style="padding:40px 40px 24px 40px;">
                              <h1 style="margin:0 0 16px 0; font-size:22px; color:#111827;">Xin chào %s 👋</h1>
                              <p style="margin:0 0 24px 0; font-size:15px; line-height:1.6; color:#4b5563;">
                                Cảm ơn bạn đã đăng ký Ozu Chat. Nhấn nút bên dưới để xác thực email và bắt đầu sử dụng tài khoản.
                              </p>
                              <table role="presentation" cellpadding="0" cellspacing="0">
                                <tr>
                                  <td style="border-radius:8px; background-color:#6366f1;">
                                    <a href="%s" target="_blank" style="display:inline-block; padding:14px 32px; font-size:15px; font-weight:600; color:#ffffff; text-decoration:none; border-radius:8px;">
                                      Xác thực email
                                    </a>
                                  </td>
                                </tr>
                              </table>
                              <p style="margin:28px 0 0 0; font-size:13px; line-height:1.6; color:#9ca3af;">
                                Nếu nút không hoạt động, sao chép đường dẫn sau vào trình duyệt:<br>
                                <a href="%s" style="color:#6366f1; word-break:break-all;">%s</a>
                              </p>
                            </td>
                          </tr>
                          <tr>
                            <td style="padding:20px 40px 32px 40px; border-top:1px solid #f0f0f0;">
                              <p style="margin:0; font-size:12px; line-height:1.6; color:#9ca3af;">
                                Liên kết này sẽ hết hạn sau một thời gian ngắn. Nếu bạn không tạo tài khoản này, vui lòng bỏ qua email này.
                              </p>
                            </td>
                          </tr>
                        </table>
                      </td>
                    </tr>
                  </table>
                </body>
                </html>
                """.formatted(displayName, verifyUrl, verifyUrl, verifyUrl);
	}
}