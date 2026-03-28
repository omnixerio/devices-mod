package com.ultreon.devices.core.client.debug;

import com.ultreon.devices.block.entity.LaptopBlockEntity;
import com.ultreon.devices.core.Laptop;
import com.ultreon.devices.core.laptop.client.ClientLaptop;
import com.ultreon.devices.core.laptop.client.ClientLaptopScreen;
import com.ultreon.devices.core.laptop.server.ServerLaptop;
import com.ultreon.devices.init.DeviceBlocks;
import dev.ultreon.mods.xinexlib.client.event.screen.ClientScreenPostInitEvent;
import dev.ultreon.mods.xinexlib.event.system.EventSystem;
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
        EventSystem.MAIN.on(ClientScreenPostInitEvent.class, event -> {
            var screen = event.getScreen();
//            if (DeviceConfig.DEBUG_BUTTON.get()) {
            {
                if (!(screen instanceof TitleScreen)) return;
                var rowHeight = 24;
                var y = screen.height / 4 + 48;

                var a = Button.builder(Component.literal("DV TEST"), (button) -> Minecraft.getInstance().setScreen(new Laptop(new LaptopBlockEntity(new BlockPos(0, 0, 0), DeviceBlocks.LAPTOPS.of(DyeColor.WHITE).get().defaultBlockState()), true))).bounds(screen.width / 2 - 100, y + rowHeight * -1, 200, 20)
                        .createNarration((output) -> Component.empty())
                        .build();
//                event.getScreen().addRenderableWidget(a);
            }
//            }

//            if (DeviceConfig.DEBUG_BUTTON.get()) {
            {
                var rowHeight = 24;
                var y = screen.height / 4 + 48;

                var a = Button.builder(Component.literal("DV TEST #2"), (button) -> {
                            var serverLaptop = new ServerLaptop();
                            ServerLaptop.laptops.put(serverLaptop.getUuid(), serverLaptop);
                            var clientLaptop = new ClientLaptop();
                            clientLaptop.setUuid(serverLaptop.getUuid());
                            ClientLaptop.laptops.put(clientLaptop.getUuid(), clientLaptop);
                            Minecraft.getInstance().setScreen(new ClientLaptopScreen(clientLaptop));
                        }).bounds(screen.width / 2 - 100, y + rowHeight * -2, 200, 20)
                        .createNarration((output) -> Component.empty())
                        .build();
//                access.addRenderableWidget(a);
            }
//            }

        });
    }
}

