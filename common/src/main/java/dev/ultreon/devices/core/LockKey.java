package dev.ultreon.devices.core;

import dev.ultreon.devices.core.io.Path;

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
