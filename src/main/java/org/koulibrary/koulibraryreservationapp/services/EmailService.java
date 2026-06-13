package org.koulibrary.koulibraryreservationapp.services;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;

    @Async
    public void send(String to, String subject, String body) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(buildHtml(subject, body), true); // true = HTML
            mailSender.send(message);
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage());
        }
    }

    private String buildHtml(String title, String body) {
        String paragraphs = java.util.Arrays.stream(body.split("\n"))
                .filter(line -> !line.isBlank())
                .map(line -> "<p style=\"margin:0 0 12px;color:#374151;font-size:15px;line-height:1.6;\">"
                        + escape(line) + "</p>")
                .collect(java.util.stream.Collectors.joining());
        return TEMPLATE.replace("{{TITLE}}", escape(title)).replace("{{BODY}}", paragraphs);
    }

    private static String escape(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    private static final String TEMPLATE = """
        <!DOCTYPE html>
        <html lang="tr">
        <head>
          <meta charset="UTF-8">
          <meta name="viewport" content="width=device-width, initial-scale=1.0">
        </head>
        <body style="margin:0;padding:0;background-color:#f3f4f6;font-family:-apple-system,'Segoe UI',Roboto,Helvetica,Arial,sans-serif;">
          <table role="presentation" width="100%" cellpadding="0" cellspacing="0" style="background-color:#f3f4f6;padding:32px 16px;">
            <tr><td align="center">
              <table role="presentation" width="480" cellpadding="0" cellspacing="0" style="max-width:480px;width:100%;background-color:#ffffff;border-radius:14px;overflow:hidden;box-shadow:0 1px 4px rgba(0,0,0,0.08);">
                <tr>
                  <td style="background-color:#1e293b;padding:22px 28px;">
                    <span style="color:#ffffff;font-size:16px;font-weight:600;letter-spacing:0.3px;">KOÜ Kütüphane Rezervasyon</span>
                  </td>
                </tr>
                <tr>
                  <td style="padding:28px;">
                    <h1 style="margin:0 0 18px;color:#111827;font-size:19px;font-weight:600;line-height:1.4;">{{TITLE}}</h1>
                    {{BODY}}
                  </td>
                </tr>
                <tr>
                  <td style="padding:18px 28px;background-color:#f9fafb;border-top:1px solid #f0f0f0;">
                    <p style="margin:0;color:#9ca3af;font-size:12px;line-height:1.5;">
                      Bu otomatik bir bildirimdir, lütfen yanıtlamayınız.<br>
                      KOÜ Kütüphane Rezervasyon Sistemi
                    </p>
                  </td>
                </tr>
              </table>
            </td></tr>
          </table>
        </body>
        </html>
        """;
}
