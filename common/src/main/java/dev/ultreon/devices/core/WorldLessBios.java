package dev.ultreon.devices.core;

import net.minecraft.client.Minecraft;

public class WorldLessBios implements Bios {

    public static final Bios INSTANCE = new WorldLessBios();

    @Override
    public void systemExit(PowerMode state) {
        if (state == PowerMode.SHUTDOWN) Minecraft.getInstance().setScreen(null);
        else Minecraft.getInstance().setScreen(new ComputerScreen(null, true));
    }
}
