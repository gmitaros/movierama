package com.mitaros.movierama.service;

import com.mitaros.movierama.dto.enums.EmailTemplate;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.mail.javamail.MimeMessageHelper.MULTIPART_MODE_MIXED;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {

    @Value("${movierama.email.sender}")
    private String emailFromAddress;

    private final JavaMailSender mailSender;

    /**
     * Send an email asynchronously
     *
     * @param to              the recipient email address
     * @param username        the username of the recipient
     * @param emailTemplate   the email template to use
     * @param confirmationUrl the confirmation URL
     * @param subject         the email subject
     * @throws MessagingException if an error occurs while sending the email
     */
    @Async
    public void sendEmail(
            String to,
            String username,
            EmailTemplate emailTemplate,
            String confirmationUrl,
            String subject
    ) throws MessagingException {
        String template;
        switch (emailTemplate) {
            case ACTIVATE_ACCOUNT:
                template = buildConfirmEmailTemplate(username, confirmationUrl);
                break;
            default:
                throw new IllegalArgumentException("Invalid email template");
        }
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(
                mimeMessage,
                MULTIPART_MODE_MIXED,
                UTF_8.name()
        );

        helper.setFrom(emailFromAddress);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(template, true);
        mailSender.send(mimeMessage);
    }

    /**
     * Build the email template for confirming the email address
     *
     * @param username        the username of the recipient
     * @param confirmationUrl the confirmation URL
     * @return the email template
     */
    private String buildConfirmEmailTemplate(String username, String confirmationUrl) {
        return String.format(
                "<html><body>" +
                        "<p>Hello %s,</p>" +
                        "<p>Thank you for registering on Movierama. Please click on the following link to confirm your email address:</p>" +
                        "<a href=\"%s\">Confirm Email</a>" +
                        "<p>If the button doesn't work click here: %s</p>" +
                        "</body></html>",
                username, confirmationUrl, confirmationUrl
        );
    }
}