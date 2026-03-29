package dev.ultreon.devices.debug;

import net.minecraft.resources.Identifier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class DebugUtils {
    public static void dump(DumpType type, Identifier resource, DumpWriter dumpFunc) throws IOException {
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

    @FunctionalInterface
    public interface DumpWriter {
        void dump(OutputStream stream) throws IOException;
    }
}
