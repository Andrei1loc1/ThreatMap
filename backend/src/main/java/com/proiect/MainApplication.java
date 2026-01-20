package com.proiect;

import com.resend.Resend;
import com.resend.services.emails.model.SendEmailRequest;
import com.resend.services.emails.model.SendEmailResponse;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MainApplication {
    /**
     * Metoda main pentru a porni aplicatia.
     * @param args argumentele liniei de comanda
     */
    public static void main(String[] args) {
        SpringApplication.run(MainApplication.class, args);
    }
}