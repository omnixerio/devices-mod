package dev.ultreon.devices.util;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class StreamUtils {
    public static String convertToString(InputStream inputStream) throws IOException {
        if (inputStream != null) {
            Writer writer = new StringWriter();
            char[] buffer = new char[1024];
            try (inputStream) {
                Reader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8), 1024);
                int n;
                while ((n = reader.read(buffer)) != -1) {
                    writer.write(buffer, 0, n);
                }
            }
            return writer.toString();
        }
        return "";
    }
}
