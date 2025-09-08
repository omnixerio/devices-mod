package dev.ultreon.devices.network.task;

import dev.ultreon.devices.Devices;
import dev.ultreon.mods.xinexlib.Env;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.HolderLookup;

public class SidedSystem {
    public static HolderLookup.Provider getRegistryProvider(Env env) {
        if (env == Env.CLIENT) {
            ClientPacketListener connection = Minecraft.getInstance().getConnection();
            if (connection == null)
                throw new IllegalStateException("Disconnected state! Can't get regostry provider.");

            return connection.registryAccess();
        } else {
            return Devices.getServer().registryAccess();
        }
    }
}
