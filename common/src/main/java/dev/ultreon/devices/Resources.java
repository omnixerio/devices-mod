package dev.ultreon.devices;

import net.minecraft.resources.ResourceLocation;

public final class Resources {
    private Resources() {
        throw new UnsupportedOperationException("Instantiating utility class");
    }


    public static final ResourceLocation ENDER_MAIL_ICONS = Devices.id("textures/gui/ender_mail.png");
    public static final ResourceLocation ENDER_MAIL_BACKGROUND = Devices.id("textures/gui/ender_mail_background.png");
}
