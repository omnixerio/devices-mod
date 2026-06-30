package dev.ultreon.devices.debug;

import dev.ultreon.devices.platform.Services;
import net.minecraft.resources.Identifier;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class DebugUtils {
    public static void dump(DumpType type, Identifier resource, DumpWriter dumpFunc) throws IOException {
        Path outputFile = Services.PLATFORM.getGameDir()
                .resolve("debug")
                .resolve("dump")
                .resolve(type.name().toLowerCase())
                .resolve(resource.getNamespace())
                .resolve(resource.getPath());

        Files.createDirectories(outputFile.getParent());
        try (OutputStream stream = Files.newOutputStream(outputFile)) {
            dumpFunc.dump(stream);
        }
    }

    @FunctionalInterface
    public interface DumpWriter {
        void dump(OutputStream stream) throws IOException;
    }
}
