package dev.ultreon.devices.core.io.drive;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jnode.fs.FileSystemException;

import java.io.IOException;
import java.nio.file.Path;
import java.util.UUID;
import java.util.function.Predicate;

/// @author MrCrayfish
public final class ExternalDrive extends AbstractDrive {
    private static final Predicate<CompoundTag> PREDICATE_DRIVE_TAG = tag -> tag.contains("name", Tag.TAG_STRING) && tag.contains("uuid", Tag.TAG_STRING) && tag.contains("root", Tag.TAG_COMPOUND);

    private ExternalDrive() {

    }

    public ExternalDrive(String displayName) {
        super(displayName);
    }

    private ExternalDrive(Path drivePath) throws FileSystemException, IOException {
        super(drivePath);
    }

    private ExternalDrive(String s, UUID uuid) {
        super(s);
        this.uuid = uuid;
    }

    public static ExternalDrive load(Path drivePath) {
        try {
            return new ExternalDrive(drivePath);
        } catch (FileSystemException | IOException e) {
            return null;
        }
    }

    @ApiStatus.Internal
    public static @NotNull ExternalDrive fromTag(CompoundTag driveTag) {
        return new ExternalDrive(driveTag.contains("name") ? driveTag.getString("name") : "Drive", UUID.fromString(driveTag.getString("uuid")));
    }

    @Override
    @ApiStatus.Internal
    public CompoundTag toTag() {
        CompoundTag driveTag = new CompoundTag();
        driveTag.putString("name", name);
        driveTag.putString("uuid", uuid.toString());
        return driveTag;
    }

    @Override
    public Type getType() {
        return Type.EXTERNAL;
    }
}
