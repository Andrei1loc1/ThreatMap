package com.proiect.service;

import com.resend.Resend;
import com.resend.services.emails.model.SendEmailRequest;
import com.resend.services.emails.model.SendEmailResponse;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final String apiKey;

    public EmailService() {
        Dotenv dotenv = Dotenv.load();
        this.apiKey = dotenv.get("RESEND_API_KEY");
    }

    public void sendConnectionEmail(String email) {
        Resend resend = new Resend(apiKey);
        SendEmailRequest sendEmailRequest = SendEmailRequest.builder()
                .from("onboarding@resend.dev")
                .to(email)
                .subject("Email Connected for Alerts")
                .html("<p>Your email has been connected for real-time threat alerts.</p>")
                .build();
        SendEmailResponse data = resend.emails().send(sendEmailRequest);
    }

    public void sendAlertEmail(String email, String htmlContent) {
        Resend resend = new Resend(apiKey);
        SendEmailRequest sendEmailRequest = SendEmailRequest.builder()
                .from("onboarding@resend.dev")
                .to(email)
                .subject("Threat Alert")
                .html(htmlContent)
                .build();
        SendEmailResponse data = resend.emails().send(sendEmailRequest);
    }
}