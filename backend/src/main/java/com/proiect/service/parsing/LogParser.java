package com.proiect.service.parsing;

import com.proiect.model.LogEntry;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class LogParser {
    private static final DateTimeFormatter LOCAL_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Parseaza fisierul de log din calea data.
     * @param filePath calea catre fisierul de log
     * @return lista de intrari de log
     */
    public List<LogEntry> parseLogFile(String filePath) {
        Path path = Path.of(filePath);
        if (!Files.exists(path)) {
            return List.of();
        }

        try (Stream<String> lines = Files.lines(path)) {
            return parseStream(lines);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return List.of();
    }

    /**
     * Parseaza log-ul din fluxul de intrare.
     * @param stream fluxul de intrare
     * @return lista de intrari de log
     */
    public List<LogEntry> parseInputStream(InputStream stream) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            return parseStream(reader.lines());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return List.of();
    }

    /**
     * Incearca sa parseze o singura linie.
     * @param line linia de parsat
     * @return intrarea de log optionala
     */
    private Optional<LogEntry> tryParseLine(String line) {
        try {
            return Optional.of(parseLine(line));
        } catch (IllegalArgumentException ignored) {
            return Optional.empty();
        }
    }

    /**
     * Parseaza o singura linie intr-o intrare de log.
     * @param line linia de parsat
     * @return intrarea de log
     */
    private LogEntry parseLine(String line) {
        String[] tokens = line.split(",");
        if (tokens.length < 3) {
            throw new IllegalArgumentException("Linie invalidă: " + line);
        }

        LocalDateTime timestamp = parseTimestamp(tokens[0].trim());
        String ip = tokens[1].trim();
        String status = tokens[2].trim().toUpperCase();
        return new LogEntry(ip, status, timestamp);
    }

    /**
     * Parseaza timestamp-ul din sir.
     * @param raw sirul brut al timestamp-ului
     * @return LocalDateTime parsat
     */
    private LocalDateTime parseTimestamp(String raw) {
        try {
            return OffsetDateTime.parse(raw).toLocalDateTime();
        } catch (DateTimeParseException ignored) {
        }

        try {
            return LocalDateTime.parse(raw, LOCAL_FORMAT);
        } catch (DateTimeParseException ignored) {
        }

        return LocalDateTime.now();
    }

    /**
     * Parseaza fluxul de linii.
     * @param lines fluxul de linii
     * @return lista de intrari de log
     */
    private List<LogEntry> parseStream(Stream<String> lines) {
        List<LogEntry> entries = new ArrayList<>();
        lines.map(String::trim)
                .filter(line -> !line.isEmpty() && !line.startsWith("#"))
                .map(this::tryParseLine)
                .flatMap(Optional::stream)
                .forEach(entries::add);

        return entries;
    }
}
