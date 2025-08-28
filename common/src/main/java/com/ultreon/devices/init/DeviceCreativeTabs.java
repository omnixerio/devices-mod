package com.ultreon.devices.init;

import com.ultreon.devices.Devices;
import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.Registrar;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;

public class DeviceCreativeTabs {
    private static final Registrar<CreativeModeTab> REGISTER = Devices.REGISTRIES.get().get(Registries.CREATIVE_MODE_TAB);

    public static void register() {
    }

    static {
        //Devices.TAB_DEVICE.

        REGISTER.register(new ResourceLocation("devices:devices_tab_device"), () -> CreativeTabRegistry.create(Component.translatable("itemGroup.devices.devices_tab_device"), () -> new ItemStack(DeviceBlocks.LAPTOPS.of(DyeColor.RED).get())));
    }
}
