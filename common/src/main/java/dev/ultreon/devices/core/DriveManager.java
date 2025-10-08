package dev.ultreon.devices.core;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import dev.ultreon.devices.UltreonDevices;
import dev.ultreon.devices.core.io.drive.AbstractDrive;
import dev.ultreon.devices.core.io.drive.ExternalDrive;
import dev.ultreon.devices.core.io.drive.InternalDrive;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.world.level.storage.LevelResource;
import org.jetbrains.annotations.ApiStatus;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class DriveManager {
    private static final Map<UUID, ExternalDrive> EXTERNAL_DRIVES = new ConcurrentHashMap<>();
    private static final Map<UUID, InternalDrive> INTERNAL_DRIVES = new ConcurrentHashMap<>();
    private static final Map<UUID, Long> LAST_ACCESS_TIMES = new ConcurrentHashMap<>();

    public static InternalDrive getInternalDrive(UUID uuid) {
        return INTERNAL_DRIVES.computeIfAbsent(uuid, id -> InternalDrive.load(id, AbstractDrive.getDrivePath(id)));
    }

    public static ExternalDrive getExternalDrive(UUID uuid) {
        return EXTERNAL_DRIVES.computeIfAbsent(uuid, id -> ExternalDrive.load(id, AbstractDrive.getDrivePath(id)));
    }

    public static void registerExternalDrive(ExternalDrive externalDrive) {
        EXTERNAL_DRIVES.put(externalDrive.getUuid(), externalDrive);
    }

    public static void registerInternalDrive(InternalDrive internalDrive) {
        INTERNAL_DRIVES.put(internalDrive.getUuid(), internalDrive);
    }

    public static void removeDrive(UUID uuid) {
        INTERNAL_DRIVES.remove(uuid);
        EXTERNAL_DRIVES.remove(uuid);

        AbstractDrive.deleteDrivePath(uuid);
    }

    @ApiStatus.Internal
    public static void loadDriveMeta() {
        Path resolve = UltreonDevices.getServer().getWorldPath(LevelResource.ROOT).resolve("data/devices/drive_meta.dat");
        if (Files.exists(resolve)) {
            CompoundTag driveMeta = null;
            try {
                driveMeta = NbtIo.readCompressed(resolve, NbtAccounter.unlimitedHeap());
            } catch (IOException e) {
                UltreonDevices.LOGGER.error("Failed to load drive meta!", e);
            }

            if (driveMeta != null) {
                CompoundTag driveMetaInternal = driveMeta.getCompound("Internal");
                for (String key : driveMetaInternal.getAllKeys()) {
                    long lastAccessed = driveMetaInternal.getLong(key);
                    if (lastAccessed > 0) {
                        UUID uuid = UUID.fromString(key);
                        LAST_ACCESS_TIMES.put(uuid, lastAccessed);
                    }
                }

                CompoundTag driveMetaExternal = driveMeta.getCompound("External");
                for (String key : driveMetaExternal.getAllKeys()) {
                    long lastAccessed = driveMetaExternal.getLong(key);
                    if (lastAccessed > 0) {
                        UUID uuid = UUID.fromString(key);
                        LAST_ACCESS_TIMES.put(uuid, lastAccessed);
                    }
                }
            }
        }
    }

    public static void saveDriveMeta() {
        CompoundTag driveMeta = new CompoundTag();
        CompoundTag driveMetaInternal = new CompoundTag();
        for (UUID uuid : INTERNAL_DRIVES.keySet()) {
            long lastAccessed = INTERNAL_DRIVES.get(uuid).getLastAccessed();
            if (lastAccessed > 0) {
                driveMetaInternal.putLong(uuid.toString(), lastAccessed);
            }
        }

        driveMeta.put("Internal", driveMetaInternal);

        CompoundTag driveMetaExternal = new CompoundTag();
        for (UUID uuid : EXTERNAL_DRIVES.keySet()) {
            long lastAccessed = EXTERNAL_DRIVES.get(uuid).getLastAccessed();
            if (lastAccessed > 0) {
                driveMetaExternal.putLong(uuid.toString(), lastAccessed);
            }
        }

        driveMeta.put("External", driveMetaExternal);
        Path resolve = UltreonDevices.getServer().getWorldPath(LevelResource.ROOT).resolve("data/devices/drive_meta.dat");
        if (Files.notExists(resolve.getParent())) {
            try {
                Files.createDirectories(resolve.getParent());
            } catch (IOException e) {
                UltreonDevices.LOGGER.error("Failed to create drive meta directory!", e);
            }
        }

        try {
            NbtIo.writeCompressed(driveMeta, resolve);
        } catch (IOException e) {
            UltreonDevices.LOGGER.error("Failed to save drive meta!", e);
        }
    }

    /**
     * Removes all drives.
     * This is not recommended to use as it's quite a destructive operation.
     * Only use it if you know what you're doing.
     *
     * @deprecated Should not be used!
     */
    @Deprecated
    public static void removeAllDrives() {
        for (UUID uuid : INTERNAL_DRIVES.keySet()) {
            removeDrive(uuid);
        }

        for (UUID uuid : EXTERNAL_DRIVES.keySet()) {
            removeDrive(uuid);
        }
    }

    /**
     * Removes all drives that haven't been accessed in the last {@code duration}.
     *
     * @param duration The duration to check for.
     */
    public static void removeOldDrives(Duration duration) {
        long now = System.currentTimeMillis();
        for (Map.Entry<UUID, Long> entry : LAST_ACCESS_TIMES.entrySet()) {
            if (now - entry.getValue() > duration.toMillis()) {
                removeDrive(entry.getKey());
            }
        }
    }

    /**
     * Updates the last accessed time for the drive with the given {@code uuid}.
     *
     * @param uuid         The UUID of the drive.
     * @param lastAccessed The new last accessed time.
     */
    public static void updateLastAccessed(UUID uuid, long lastAccessed) {
        LAST_ACCESS_TIMES.put(uuid, lastAccessed);
    }
}
