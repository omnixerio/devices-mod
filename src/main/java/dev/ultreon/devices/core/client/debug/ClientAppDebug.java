package dev.ultreon.devices.core.client.debug;

import dev.ultreon.devices.DeviceConfig;
import dev.ultreon.devices.block.entity.LaptopBlockEntity;
import dev.ultreon.devices.client.ClientLaptop;
import dev.ultreon.devices.client.ClientLaptopScreen;
import dev.ultreon.devices.core.Laptop;
import dev.ultreon.devices.core.laptop.server.ServerLaptop;
import dev.ultreon.devices.init.DeviceBlocks;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.mixin.screen.ScreenAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.DyeColor;

/**
 * Adds a button to the title screen to test system applications that don't require the system
 */
public class ClientAppDebug {
    public static void register() {
        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            if (DeviceConfig.DEBUG_BUTTON.get()) {
                if (!(screen instanceof TitleScreen)) return;
                var rowHeight = 24;
                var y = screen.height / 4 + 48;

                var a = Button.builder(Component.literal("DV TEST"), button -> Minecraft.getInstance().setScreen(new Laptop(new LaptopBlockEntity(new BlockPos(0, 0, 0), DeviceBlocks.LAPTOPS.of(DyeColor.WHITE).defaultBlockState()), true))).bounds(screen.width / 2 - 100, y + rowHeight * -1, 200, 20)
                        .createNarration(output -> Component.empty())
                        .build();
                screen.addRenderableWidget(a);
            }

            if (DeviceConfig.DEBUG_BUTTON.get()) {
                var rowHeight = 24;
                var y = screen.height / 4 + 48;

                var a = Button.builder(Component.literal("DV TEST #2"), button -> {
                            var serverLaptop = new ServerLaptop();
                            ServerLaptop.laptops.put(serverLaptop.getUuid(), serverLaptop);
                            var clientLaptop = new ClientLaptop(serverLaptop.getUuid());
                            ClientLaptop.laptops.put(clientLaptop.getUuid(), clientLaptop);
                            Minecraft.getInstance().setScreen(new ClientLaptopScreen(clientLaptop));
                        }).bounds(screen.width / 2 - 100, y + rowHeight * -2, 200, 20)
                        .createNarration(output -> Component.empty())
                        .build();
                screen.addRenderableWidget(a);
            }
        });
    }
}

