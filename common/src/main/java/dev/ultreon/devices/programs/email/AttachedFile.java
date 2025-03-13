package dev.ultreon.devices.programs.email;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

public record AttachedFile(String name, byte[] data) {

    public Tag toTag() {
        CompoundTag tag = new CompoundTag();
        tag.putString("file_name", name);
        tag.putByteArray("data", data);
        return tag;
    }

    public static AttachedFile fromTag(CompoundTag tag) {
        return new AttachedFile(tag.getString("file_name"), tag.getByteArray("data"));
    }
}
