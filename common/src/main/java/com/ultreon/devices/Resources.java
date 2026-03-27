package com.ultreon.devices;

import net.minecraft.resources.Identifier;

public final class Resources {
    private Resources() {
        throw new UnsupportedOperationException("Instantiating utility class");
    }


    public static final Identifier ENDER_MAIL_ICONS = Devices.id("textures/gui/ender_mail.png");
    public static final Identifier ENDER_MAIL_BACKGROUND = Devices.id("textures/gui/ender_mail_background.png");
}
