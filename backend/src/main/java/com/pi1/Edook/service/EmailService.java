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

    public void enviarConfirmacaoEmail(String para, String codigo) {

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(para);
            helper.setSubject("Código de Verificação");

            String html = """
                <div style="font-family: Arial; padding: 20px;">
                    <h2>Código de Verificação</h2>

                    <p>Seu código de verificação é:</p>

                    <h1 style="
                        background:#3498db;
                        color:white;
                        display:inline-block;
                        padding:10px 20px;
                        border-radius:8px;">
                        %s
                    </h1>

                    <p>Digite esse código no aplicativo para concluir o cadastro.</p>
                </div>
            """.formatted(codigo);

            helper.setText(html, true);

            mailSender.send(message);

        } catch (Exception e) {
            throw new RuntimeException("Erro ao enviar email", e);
        }
    }
}