package dev.ultreon.devices.core;

import java.nio.file.Path;

@SuppressWarnings("ClassCanBeRecord")
public class LockKey {
    private final Path path;

    public LockKey(Path path) {
        this.path = path;
    }

    public Path getPath() {
        return path;
    }
}
