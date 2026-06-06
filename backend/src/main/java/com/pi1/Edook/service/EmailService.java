package com.pi1.Edook.service;

import jakarta.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    @Value("${app.base-url}")
    private String baseUrl;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void enviarConfirmacaoEmail(String para, String token) {

        String link = baseUrl + "/funcionarios/confirmar-email?token=" + token;

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(para);
            helper.setSubject("Confirmação de Email");

            String html = """
                <div style="font-family: Arial; padding: 20px;">
                    <h2 style="color:#2c3e50;">Confirmação de Conta</h2>

                    <p>Obrigado por se cadastrar.</p>

                    <p>Clique no botão abaixo para confirmar seu email:</p>

                    <a href="%s"
                       style="
                            display:inline-block;
                            padding:12px 20px;
                            background:#3498db;
                            color:#fff;
                            text-decoration:none;
                            border-radius:6px;
                            margin-top:10px;
                       ">
                       Confirmar Email
                    </a>

                    <p style="margin-top:20px; font-size:12px; color:gray;">
                        Se você não criou essa conta, ignore este email.
                    </p>
                </div>
            """.formatted(link);

            helper.setText(html, true);

            mailSender.send(message);

        } catch (Exception e) {
            throw new RuntimeException("Erro ao enviar email", e);
        }
    }
}