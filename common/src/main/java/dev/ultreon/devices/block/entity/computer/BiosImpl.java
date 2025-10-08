package dev.ultreon.devices.block.entity.computer;

import dev.ultreon.devices.core.Bios;
import dev.ultreon.devices.core.ComputerScreen;
import dev.ultreon.devices.core.PowerMode;
import net.minecraft.client.Minecraft;

public class BiosImpl implements Bios {
    private final ComputerBlockEntity blockEntity;

    public BiosImpl(ComputerBlockEntity blockEntity) {
        this.blockEntity = blockEntity;
    }

    @Override
    public void systemExit(PowerMode state) {
        if (state == PowerMode.SHUTDOWN) {
            if (blockEntity.isPoweredOn()) {
                blockEntity.powerOff();
                Minecraft.getInstance().setScreen(null);
            }
        } else if (!blockEntity.isPoweredOn()) {
            Minecraft.getInstance().setScreen(new ComputerScreen(blockEntity));
        }
    }
}
