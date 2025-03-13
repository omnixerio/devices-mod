package dev.ultreon.devices.network;

import dev.ultreon.mods.xinexlib.network.Networker;
import dev.ultreon.mods.xinexlib.network.PacketInfo;
import dev.ultreon.mods.xinexlib.network.packet.PacketToClient;
import dev.ultreon.mods.xinexlib.network.packet.PacketToServer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

@Deprecated
public abstract class Packet<T extends Packet<T>> implements PacketToClient<T>, PacketToServer<T> {
    protected Packet() {

    }

    public abstract void toBytes(RegistryFriendlyByteBuf buf);

    @Override
    public void write(RegistryFriendlyByteBuf buf) {
        toBytes(buf);
    }

    public abstract boolean onMessage(Networker networker, PacketInfo info);

    @Override
    public void handle(Networker networker, ServerPlayer serverPlayer) {
        onMessage(networker, new PacketInfo(serverPlayer));
    }

    @Override
    public void handle(Networker networker) {
        onMessage(networker, new PacketInfo());
    }
}
