package com.proiect.simulation;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

public class DataGenerator {
    private static final Random RANDOM = new Random();
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("MMM dd HH:mm:ss");
    private static final List<String> NORMAL_IPS = List.of("192.168.1.1", "10.0.0.1", "172.16.0.1");  // IP-uri locale normale
    private static final List<String> ATTACK_IPS = List.of("185.123.45.67", "203.0.113.1", "89.248.172.10");  // IP-uri suspecte globale

    // Metoda pentru generare log fake
    public static String generateLogEntry(boolean isAttack) {
        LocalDateTime timestamp = LocalDateTime.now().minusSeconds(RANDOM.nextInt(3600));  // Timestamp random în ultima oră
        String ip = isAttack ? ATTACK_IPS.get(RANDOM.nextInt(ATTACK_IPS.size())) : NORMAL_IPS.get(RANDOM.nextInt(NORMAL_IPS.size()));
        String status = isAttack && RANDOM.nextBoolean() ? "Failed password" : "Accepted password";
        String user = List.of("root", "admin", "user").get(RANDOM.nextInt(3));
        return String.format("%s host sshd[123]: %s for %s from %s port 22 ssh2",
            timestamp.format(FORMATTER), status, user, ip);
    }

    // Metoda pentru generare log fake cu IP specific
    public static String generateLogEntry(boolean isAttack, String specificIp) {
        LocalDateTime timestamp = LocalDateTime.now().minusSeconds(RANDOM.nextInt(3600));  // Timestamp random în ultima oră
        String ip = specificIp;
        String status = isAttack ? "Failed password" : "Accepted password";
        String user = List.of("root", "admin", "user").get(RANDOM.nextInt(3));
        return String.format("%s host sshd[123]: %s for %s from %s port 22 ssh2",
            timestamp.format(FORMATTER), status, user, ip);
    }

    // Metodă pentru a decide dacă e atac (bazat pe procent)
    public static boolean isAttackEvent(double attackPercent) {
        return RANDOM.nextDouble() * 100 < attackPercent;
    }
}