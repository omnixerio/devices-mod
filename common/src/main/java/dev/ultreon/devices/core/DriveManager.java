package dev.ultreon.devices.core;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import dev.ultreon.devices.Devices;
import dev.ultreon.devices.core.io.drive.AbstractDrive;
import dev.ultreon.devices.core.io.drive.ExternalDrive;
import dev.ultreon.devices.core.io.drive.InternalDrive;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class DriveManager {
    private static final Cache<UUID, ExternalDrive> EXTERNAL_DRIVE_CACHE = CacheBuilder.newBuilder().expireAfterAccess(5, TimeUnit.MINUTES).build();
    private static final Cache<UUID, InternalDrive> INTERNAL_DRIVE_CACHE = CacheBuilder.newBuilder().expireAfterAccess(5, TimeUnit.MINUTES).build();

    public static InternalDrive getInternalDrive(UUID uuid) {
        try {
            return INTERNAL_DRIVE_CACHE.get(uuid, () -> InternalDrive.load(uuid, AbstractDrive.getDrivePath(uuid)));
        } catch (ExecutionException e) {
            Devices.LOGGER.error("Failed to load internal drive", e);
            return null;
        }
    }

    public static ExternalDrive getExternalDrive(UUID uuid) {
        try {
            return EXTERNAL_DRIVE_CACHE.get(uuid, () -> ExternalDrive.load(uuid, AbstractDrive.getDrivePath(uuid)));
        } catch (ExecutionException e) {
            Devices.LOGGER.error("Failed to load external drive", e);
            return null;
        }
    }

    public static void registerExternalDrive(ExternalDrive externalDrive) throws IOException {
        EXTERNAL_DRIVE_CACHE.put(externalDrive.getUuid(), externalDrive);
        externalDrive.flush();
    }

    public static void registerInternalDrive(InternalDrive internalDrive) throws IOException {
        INTERNAL_DRIVE_CACHE.put(internalDrive.getUuid(), internalDrive);
        internalDrive.flush();
    }
}
