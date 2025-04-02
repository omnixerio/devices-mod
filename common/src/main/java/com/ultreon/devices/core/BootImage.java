package com.ultreon.devices.core;

import com.ultreon.devices.core.io.drive.AbstractDrive;
import net.minecraft.world.level.storage.LevelResource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.UUID;

public class BootImage {
    private final byte[] bytes;

    public BootImage(byte[] bytes) {
        this.bytes = bytes;
    }

    public void write(UUID uuid) throws IOException {
        if (uuid == null) return;
        Path file = AbstractDrive.getDrivePath(uuid);
        Files.createDirectories(file.getParent());
        Files.write(file, bytes, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }
}
