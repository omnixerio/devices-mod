package com.ultreon.devices.core.laptop.common;

import com.ultreon.devices.core.laptop.server.ServerLaptop;
import dev.ultreon.mods.xinexlib.network.Networker;
import dev.ultreon.mods.xinexlib.network.packet.PacketToServer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;
import java.util.UUID;

public class C2SUpdatePacket implements PacketToServer<C2SUpdatePacket> {
    private final CompoundTag nbt;

    public C2SUpdatePacket(UUID laptop, String type, CompoundTag nbt) {
        this.nbt = new CompoundTag();
        this.nbt.putLongArray("uuid", new long[]{laptop.getMostSignificantBits(), laptop.getLeastSignificantBits()}); // laptop uuid
        this.nbt.putString("type", type);
        this.nbt.put("data", nbt);
    }

    public C2SUpdatePacket(FriendlyByteBuf buf) {
        this.nbt = buf.readNbt();
    }

    @Override
    public void handle(Networker connection, ServerPlayer player) {
        Optional<long[]> uuid = this.nbt.getLongArray("uuid");
        if (uuid.isEmpty()) return;
        long[] longs = uuid.get();
        UUID uuid1 = new UUID(longs[0], longs[1]);
        ServerLaptop.laptops.get(uuid).handlePacket(player, this.nbt.getString("type").orElse(null), this.nbt.getCompoundOrEmpty("data"));
    }

    @Override
    public void write(RegistryFriendlyByteBuf buffer) {
        buffer.writeNbt(nbt);
    }
}
