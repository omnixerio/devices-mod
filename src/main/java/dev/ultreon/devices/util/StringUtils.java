package dev.ultreon.devices.util;

public class StringUtils {
    public static boolean isNullOrEmpty(String text) {
        return text == null || text.isEmpty();
    }

    public static boolean isNotNullOrEmpty(String text) {
        return !isNullOrEmpty(text);
    }

    public static boolean containsIgnoreCase(String name, String text) {
        return name.toLowerCase().contains(text.toLowerCase());
    }
}
