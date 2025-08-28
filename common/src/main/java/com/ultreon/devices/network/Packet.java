package com.ultreon.devices.network;

import dev.architectury.networking.NetworkManager;
import net.minecraft.network.FriendlyByteBuf;

import java.util.function.Supplier;

public abstract class Packet<T extends Packet<T>> {
    public Packet() {

    }

    public abstract void toBytes(FriendlyByteBuf buf);

    @Deprecated
    public void fromBytes(FriendlyByteBuf buf) {

    }

    public abstract boolean onMessage(Supplier<NetworkManager.PacketContext> ctx);
}
