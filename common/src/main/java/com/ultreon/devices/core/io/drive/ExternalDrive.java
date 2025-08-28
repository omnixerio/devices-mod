package com.ultreon.devices.core.io.drive;

import com.ultreon.devices.core.io.ServerFolder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.function.Predicate;

/**
 * @author MrCrayfish
 */
public final class ExternalDrive extends AbstractDrive {
    private static final Predicate<CompoundTag> PREDICATE_DRIVE_TAG = tag -> tag.contains("name", Tag.TAG_STRING) && tag.contains("uuid", Tag.TAG_STRING) && tag.contains("root", Tag.TAG_COMPOUND);

    private ExternalDrive() {
    }

    public ExternalDrive(String displayName) {
        super(displayName);
    }

    @Nullable
    public static ExternalDrive fromTag(CompoundTag driveTag) {
        if (!PREDICATE_DRIVE_TAG.test(driveTag)) return null;

        ExternalDrive drive = new ExternalDrive();
        drive.name = driveTag.getString("name");
        drive.uuid = UUID.fromString(driveTag.getString("uuid"));

        CompoundTag folderTag = driveTag.getCompound("root");
        drive.root = ServerFolder.fromTag(folderTag.getString("file_name"), folderTag.getCompound("data"));

        return drive;
    }

    @Override
    public CompoundTag toTag() {
        CompoundTag driveTag = new CompoundTag();
        driveTag.putString("name", name);
        driveTag.putString("uuid", uuid.toString());

        CompoundTag folderTag = new CompoundTag();
        folderTag.putString("file_name", root.getName());
        folderTag.put("data", root.toTag());
        driveTag.put("root", folderTag);

        return driveTag;
    }

    @Override
    public Type getType() {
        return Type.EXTERNAL;
    }
}
