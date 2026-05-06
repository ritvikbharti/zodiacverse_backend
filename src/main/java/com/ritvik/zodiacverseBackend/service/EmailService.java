//  Change this line — match YOUR project's package
package com.ritvik.zodiacverseBackend.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String fromAddress;

    @Value("${app.client-url}")
    private String clientUrl;

    @Async
    public void sendWelcomeEmail(String toEmail, String fullName) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromAddress);
            helper.setTo(toEmail);
            helper.setSubject(" Welcome to ZodiacVerse — Your Cosmic Journey Begins!");
            helper.setText(WelcomeEmailTemplate.build(fullName, clientUrl), true);

            mailSender.send(message);
            log.info(" Welcome email sent to {}", toEmail);

        } catch (MessagingException e) {
            log.error("Failed to send welcome email to {}: {}", toEmail, e.getMessage());
        }
    }

    // Add these methods to your existing EmailService.java

    @Async
    public void sendBookingConfirmedEmail(String toEmail, String fullName,
                                          String astrologerName, String sessionTime) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromAddress);
            helper.setTo(toEmail);
            helper.setSubject("🔮 Booking Confirmed — " + astrologerName);
            helper.setText("""
            <body style="font-family:sans-serif;background:#0d0d1a;color:#e2d9f3;padding:40px;">
              <h2>Booking Confirmed! 🔮</h2>
              <p>Hi %s,</p>
              <p>Your session with <strong>%s</strong> is confirmed for <strong>%s</strong>.</p>
              <a href="%s/app/bookings"
                 style="background:linear-gradient(135deg,#7c3aed,#4f46e5);color:#fff;
                        padding:12px 30px;border-radius:50px;text-decoration:none;
                        display:inline-block;margin-top:16px;">
                View Booking
              </a>
            </body>
            """.formatted(fullName.split(" ")[0], astrologerName, sessionTime, clientUrl),
                    true);
            mailSender.send(message);
            log.info("Booking confirmation email sent to {}", toEmail);
        } catch (MessagingException e) {
            log.error("Booking email failed for {}: {}", toEmail, e.getMessage());
        }
    }

    @Async
    public void sendReportReadyEmail(String toEmail, String fullName, String reportName) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromAddress);
            helper.setTo(toEmail);
            helper.setSubject("📊 Your Report is Ready — " + reportName);
            helper.setText("""
            <body style="font-family:sans-serif;background:#0d0d1a;color:#e2d9f3;padding:40px;">
              <h2>Your Report is Ready! 📊</h2>
              <p>Hi %s,</p>
              <p>Your <strong>%s</strong> has been generated and is ready to view.</p>
              <a href="%s/app/reports"
                 style="background:linear-gradient(135deg,#7c3aed,#4f46e5);color:#fff;
                        padding:12px 30px;border-radius:50px;text-decoration:none;
                        display:inline-block;margin-top:16px;">
                View Report
              </a>
            </body>
            """.formatted(fullName.split(" ")[0], reportName, clientUrl),
                    true);
            mailSender.send(message);
            log.info(" Report ready email sent to {}", toEmail);
        } catch (MessagingException e) {
            log.error(" Report email failed for {}: {}", toEmail, e.getMessage());
        }
    }

    @Async
    public void sendPasswordChangedEmail(String toEmail, String fullName) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromAddress);
            helper.setTo(toEmail);
            helper.setSubject("🔒 Your Password Was Changed");
            helper.setText("""
            <body style="font-family:sans-serif;background:#0d0d1a;color:#e2d9f3;padding:40px;">
              <h2>Password Changed 🔒</h2>
              <p>Hi %s,</p>
              <p>Your ZodiacVerse password was successfully updated.</p>
              <p style="color:#f87171;">If you did not make this change,
                 please contact support immediately.</p>
            </body>
            """.formatted(fullName.split(" ")[0]),
                    true);
            mailSender.send(message);
            log.info(" Password changed email sent to {}", toEmail);
        } catch (MessagingException e) {
            log.error(" Password email failed for {}: {}", toEmail, e.getMessage());
        }
    }
}