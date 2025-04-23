package com.codeloon.ems.util;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class UserUtils {

    private UserUtils() {
        throw new UnsupportedOperationException("UserUtils class cannot be instantiated");
    }

    public static String generateCustomUUID(String role, String userName) {

        String rolePrefix = switch (role.toUpperCase()) {
            case "ADMIN" -> "ADM";
            case "EMPLOYEE" -> "EMP";
            case "CLIENT" -> "CLI";
            default -> "USR";
        };

        //userName
        String namePrefix = userName.substring(0,3).toUpperCase();

        String timestamp = new SimpleDateFormat("yyMMdd").format(new Date());

        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder randomPart = new StringBuilder(3);
        for (int i = 0; i < 3; i++) {
            randomPart.append(characters.charAt(random.nextInt(characters.length())));
        }

        return rolePrefix + "-" + namePrefix + timestamp + randomPart;
    }

    public static String generateRandomTempPassword() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder result = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < 8; i++) {
            int index = random.nextInt(characters.length());
            result.append(characters.charAt(index));
        }

        return result.toString();
    }
}
