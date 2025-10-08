package dev.ultreon.devices.core.io.drive;

import dev.ultreon.devices.UltreonDevices;
import dev.ultreon.devices.core.DriveManager;
import dev.ultreon.devices.core.io.Path;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jnode.fs.FileSystemException;

import java.io.IOException;
import java.util.UUID;

/// @author MrCrayfish
public final class InternalDrive extends AbstractDrive {
    public InternalDrive(String name) {
        super(name);
        DriveManager.registerInternalDrive(this);
    }

    public InternalDrive(String name, UUID uuid) {
        super(uuid);
        DriveManager.registerInternalDrive(this);
    }

    @Override
    protected void setup() {
        super.setup();

        try {
            this.createDirectory(Path.of("/Home"));
            this.createDirectory(Path.of("/ApplicationData"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private InternalDrive(UUID uuid, java.nio.file.Path drivePath) throws FileSystemException, IOException {
        super(uuid, drivePath);
    }

    @ApiStatus.Internal
    public static @NotNull AbstractDrive fromTag(CompoundTag driveTag) {
        String string = driveTag.toString();
        System.out.println("string = " + string);
        if (!driveTag.contains("uuid")) {
            if (!driveTag.contains("name"))
                return new InternalDrive("Drive");
            return new InternalDrive(driveTag.getString("name"));
        } else if (!driveTag.contains("name")) {

            return new InternalDrive("Drive", NbtUtils.loadUUID(driveTag.getCompound("uuid")));
        }
        try {
            Tag uuid1 = driveTag.get("uuid");
            if (!(uuid1 instanceof IntArrayTag)) {
                UltreonDevices.LOGGER.warn("Invalid uuid tag in drive tag: {}", uuid1);
                return new InternalDrive(driveTag.contains("name") ? driveTag.getString("name") : "Drive");
            }
            UUID uuid2 = NbtUtils.loadUUID(uuid1);
            return DriveManager.getInternalDrive(uuid2);
        } catch (Exception e) {
            return new InternalDrive("Drive");
        }
    }

    /**
     * Loads an internal drive from a drive path
     *
     * @param uuid      The UUID of the drive
     * @param drivePath The path to the drive
     * @return The loaded drive, or null if it failed to load
     * @deprecated Use {@link DriveManager#getInternalDrive(UUID)} instead
     */
    @Deprecated
    public static InternalDrive load(UUID uuid, java.nio.file.Path drivePath) {
        try {
            return new InternalDrive(uuid, drivePath);
        } catch (FileSystemException | IOException e) {
            return null;
        }
    }

    @Override
    public CompoundTag toTag() {
        CompoundTag driveTag = new CompoundTag();
        driveTag.putString("name", name);
        driveTag.put("uuid", NbtUtils.createUUID(uuid));

        return driveTag;
    }

    @Override
    public Type getType() {
        return Type.INTERNAL;
    }
}
