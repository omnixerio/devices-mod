package com.ultreon.devices.debug;

import dev.architectury.platform.Platform;
import net.minecraft.resources.ResourceLocation;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * The DebugUtils class provides utility methods for debugging purposes.
 */
public class DebugUtils {
    public static void dump(DumpType type, ResourceLocation resource, DumpWriter dumpFunc) throws IOException {
        if (!Platform.isDevelopmentEnvironment()) return;

        File namespaceFile = new File("debug/dump/" + type.name().toLowerCase(), resource.getNamespace());
        File outputFile = new File(namespaceFile, resource.getPath());
        File outputDir = outputFile.getParentFile();
        if (!outputDir.exists() && !outputDir.mkdirs()) {
            throw new IOException("Creating output directory failed for: " + outputFile.getPath());
        }
        try (FileOutputStream stream = new FileOutputStream(outputFile)) {
            dumpFunc.dump(stream);
        }
    }

    /**
     * The DumpWriter interface is a functional interface
     * that represents a writer capable of dumping data to an OutputStream.
     */
    @FunctionalInterface
    public interface DumpWriter {
        void dump(OutputStream stream) throws IOException;
    }
}
