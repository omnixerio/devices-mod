package com.ultreon.devices.util;

import net.minecraft.nbt.CompoundTag;

public interface DataHandler {

    /**
     * Allows you to load data from a requestData.
     *
     * @param tag the compound requestData where you saved data is
     */
    void load(CompoundTag tag);

    /**
     * Allows you to save data to a requestData.
     *
     * @param tag the compound requestData to save your data to
     */
    void save(CompoundTag tag);
}
