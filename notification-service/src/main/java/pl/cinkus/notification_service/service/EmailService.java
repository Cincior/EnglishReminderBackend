package pl.cinkus.notification_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import pl.cinkus.messages.SendEmailWithCodeMessage;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String senderEmail;

    public void sendEmail(SendEmailWithCodeMessage message) {
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setFrom(senderEmail);
        mail.setTo(message.getEmail());

        switch (message.getEmailType()) {
            case RESET_PASSWORD -> {
                mail.setSubject("Password Reset");
                mail.setText("""
                        Use this code to reset your password:
                        %s
                        It was not you? Let us know
                        """.formatted(message.getCode()));
            }
            case VERIFY_REGISTRATION -> {
                mail.setSubject("Verify your Account");
                mail.setText("""
                        Use this code to verify your account:
                        %s
                        It was not you? Let us know
                        """.formatted(message.getCode()));
            }
            default -> throw new IllegalArgumentException(
                    "Unsupported email type: " + message.getEmailType());
        }

        mailSender.send(mail);
    }
}
