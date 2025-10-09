package dev.ultreon.devices.core.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;

public record WiFiNetwork(
        String ssid,
        boolean passwordProtected,
        WifiStrength strength
) {
    public static WiFiNetwork read(FriendlyByteBuf buf) {
        return new WiFiNetwork(buf.readUtf(), buf.readBoolean(), WifiStrength.fromOrdinal(buf.readVarInt()));
    }

    public void write(RegistryFriendlyByteBuf buf) {
        buf.writeUtf(ssid);
        buf.writeBoolean(passwordProtected);
        buf.writeVarInt(strength.ordinal());
    }
}
