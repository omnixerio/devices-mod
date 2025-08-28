package dev.ultreon.devices.block;

import dev.ultreon.devices.block.entity.computer.ComputerBlockEntity;
import dev.ultreon.devices.core.ComputerScreen;
import net.minecraft.client.Minecraft;

public class ClientLaptopWrapper {

    public static void execute(ComputerBlockEntity laptop) {
        Minecraft.getInstance().setScreen(new ComputerScreen(laptop));
    }
}
