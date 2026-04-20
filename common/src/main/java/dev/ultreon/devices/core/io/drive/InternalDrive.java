package dev.ultreon.devices.core.io.drive;

import dev.ultreon.devices.core.io.ServerFolder;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;

/**
 * @author MrCrayfish
 */
public final class InternalDrive extends AbstractDrive {
    public InternalDrive(String name) {
        super(name);
    }

    public static @NotNull AbstractDrive fromTag(CompoundTag driveTag) {
        AbstractDrive drive = new InternalDrive(driveTag.getString("name").orElse(null));
        if (driveTag.contains("root")) {
            CompoundTag folderTag = driveTag.getCompoundOrEmpty("root");
            drive.root = ServerFolder.fromTag(folderTag.getString("file_name").orElse(null), folderTag.getCompoundOrEmpty("data"));
        }
        return drive;
    }

    @Override
    public CompoundTag toTag() {
        CompoundTag driveTag = new CompoundTag();
        driveTag.putString("name", name);

        CompoundTag folderTag = new CompoundTag();
        folderTag.putString("file_name", root.getName());
        folderTag.put("data", root.toTag());
        driveTag.put("root", folderTag);

        return driveTag;
    }

    @Override
    public Type getType() {
        return Type.INTERNAL;
    }
}
