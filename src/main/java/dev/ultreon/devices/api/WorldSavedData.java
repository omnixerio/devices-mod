package dev.ultreon.devices.api;

import net.minecraft.nbt.CompoundTag;

public interface WorldSavedData {
    void save(CompoundTag tag);

    void load(CompoundTag tag);
}
