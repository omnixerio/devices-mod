package dev.ultreon.devices.programs.system;

import dev.ultreon.devices.api.video.CustomResolution;
import net.minecraft.nbt.CompoundTag;

public interface DisplayResolution {
    static DisplayResolution load(CompoundTag resolution) {
        var width = resolution.getInt("width");
        var height = resolution.getInt("height");

        for (PredefinedResolution predefinedResolution : PredefinedResolution.values()) {
            if (predefinedResolution.width() == width && predefinedResolution.height() == height) {
                return predefinedResolution;
            }
        }
        return new CustomResolution(width, height);
    }

    int width();

    int height();

    default void save(CompoundTag tag) {
        tag.putInt("width", width());
        tag.putInt("height", height());
    }
}
