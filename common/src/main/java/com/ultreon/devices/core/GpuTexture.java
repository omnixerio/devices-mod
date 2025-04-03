package com.ultreon.devices.core;

import com.mojang.blaze3d.platform.NativeImage;
import com.ultreon.devices.Devices;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;

public class GpuTexture extends DynamicTexture {

    private ResourceLocation id;

    public GpuTexture(NativeImage image) {
        super(image);
    }

    public ResourceLocation getTextureLocation() {
        return id != null ? id : (id = Devices.id("gpu/" + this.getId()));
    }
}
