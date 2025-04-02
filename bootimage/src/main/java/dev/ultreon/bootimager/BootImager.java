package dev.ultreon.bootimager;

import org.jnode.fs.FileSystemException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class BootImager {
    public static void main(String[] args) throws FileSystemException, IOException {
        if (!Files.exists(Path.of("build"))) {
            Files.createDirectory(Path.of("build"));
        }

        if (Files.exists(Path.of("common/src/main/resources/data/devices/filesystems/main.ext2"))) {
            Files.delete(Path.of("common/src/main/resources/data/devices/filesystems/main.ext2"));
        }

        try (Ext2FS fs = Ext2FS.format(Path.of("common/src/main/resources/data/devices/filesystems/main.ext2"), 16L * 1024L * 1024L)) {
            try (var walk = Files.walk(Path.of("bootimage/src/fs"))) {
                walk.forEach(path -> {
                    try {
                        String replace = path.toString().replace("bootimage/src/fs/", "");
                        if (replace.startsWith(".")) {
                            return;
                        }
                        Path rel = Path.of("/" + replace);
                        if (Files.isDirectory(path)) {
                            fs.createDirectory(rel);
                            return;
                        }
                        byte[] data = Files.readAllBytes(path);
                        fs.createFile(rel, data);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
            fs.flush();
        }
    }
}