package com.ultreon.devices.api.video;

import com.mojang.blaze3d.platform.Window;
import com.ultreon.devices.core.Laptop;
import com.ultreon.devices.programs.system.DisplayResolution;
import com.ultreon.devices.programs.system.PredefinedResolution;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;

import java.util.Arrays;
import java.util.Collection;

public class VideoInfo {
    private DisplayResolution resolution = PredefinedResolution.PREDEFINED_384x216;

    public VideoInfo(CompoundTag videoInfoData) {
        if (videoInfoData.contains("resolution"))
            resolution = DisplayResolution.load(videoInfoData.getCompound("resolution").orElseThrow());
    }

    public Collection<PredefinedResolution> getResolutionList() {
        Window window = Minecraft.getInstance().getWindow();
        return Arrays.stream(PredefinedResolution.values())
                .filter(r -> r.width() <= window.getGuiScaledWidth() && r.height() <= window.getGuiScaledHeight())
                .toList();
    }

    public void setResolution(DisplayResolution value) {
        this.resolution = value;

        Laptop.getInstance().revalidateDisplay();
    }

    public DisplayResolution getResolution() {
        return resolution;
    }

    public void save(CompoundTag tag) {
        if (resolution != null) {
            resolution.save(tag);
        }
    }
}
