package com.ndinhchien.m4y.global.util;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import lombok.Getter;

public class CommonUtils {
    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public static String hash(String password) {
        return passwordEncoder.encode(password);
    }

    public static boolean compare(String password, String hashedPassword) {
        return passwordEncoder.matches(password, hashedPassword);
    }

    private static final String ALPHANUMERIC_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final String SPECIAL_CHARACTERS = "!@#$%^&*()-_=+";
    private static final SecureRandom RANDOM = new SecureRandom();

    private static String generateRandomString(int length, String characters) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int randomIndex = RANDOM.nextInt(characters.length());
            sb.append(characters.charAt(randomIndex));
        }
        return sb.toString();
    }

    public static String generateSecureString(int length) {
        return generateRandomString(length, ALPHANUMERIC_CHARACTERS + SPECIAL_CHARACTERS);
    }

    public static String generateUserName(int length) {
        return generateRandomString(length, ALPHANUMERIC_CHARACTERS);
    }

    private static ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    public static String stringify(Object obj) {
        if (obj == null)
            return "null";
        try {
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (Exception e) {
            return "Failed to parse to json string";
        }
    }

    @Getter
    public static class InstantRange {
        private Instant startOfDay;

        private Instant endOfDay;

        public InstantRange(Instant instant) {
            // Define the time zone you care about (e.g., system default or a specific one)
            ZoneId zoneId = ZoneId.systemDefault();

            // Convert Instant to LocalDate using the zone
            LocalDate date = instant.atZone(zoneId).toLocalDate();

            // Start of the day
            ZonedDateTime startOfDay = date.atStartOfDay(zoneId);
            Instant startInstant = startOfDay.toInstant();

            // End of the day (23:59:59.999999999)
            ZonedDateTime endOfDay = date.plusDays(1).atStartOfDay(zoneId).minusNanos(1);
            Instant endInstant = endOfDay.toInstant();

            this.startOfDay = startInstant;
            this.endOfDay = endInstant;
        }

    }

}
