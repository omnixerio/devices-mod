package dev.ultreon.devices.core.io.drive;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jnode.fs.FileSystemException;

import java.io.IOException;
import java.nio.file.Path;
import java.util.UUID;

/// @author MrCrayfish
public final class InternalDrive extends AbstractDrive {
    public InternalDrive(String name) {
        super(name);
    }

    public InternalDrive(String name, UUID uuid) {
        super(uuid);
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

    private InternalDrive(Path drivePath) throws FileSystemException, IOException {
        super(drivePath);
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
            return new InternalDrive(driveTag.contains("name") ? driveTag.getString("name") : "Drive", NbtUtils.loadUUID(driveTag.getCompound("uuid")));
        } catch (Exception e) {
            return new InternalDrive("Drive");
        }
    }

    public static InternalDrive load(Path drivePath) {
        try {
            return new InternalDrive(drivePath);
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
