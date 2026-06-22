package dev.ultreon.devices.block;

import dev.ultreon.devices.block.entity.ComputerBlockEntity;
import dev.ultreon.devices.core.Laptop;
import dev.ultreon.devices.core.ModernLaptop;
import net.minecraft.client.Minecraft;

public class ClientLaptopWrapper {

    public static void execute(ComputerBlockEntity laptop) {
//        Minecraft.getInstance().setScreen(new Laptop(laptop));
        Minecraft.getInstance().setScreen(new ModernLaptop(laptop));
    }
}
