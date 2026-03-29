package com.ultreon.devices.core.laptop.common;

import com.ultreon.devices.core.laptop.client.ClientLaptop;
import com.ultreon.devices.debug.DebugLog;
import dev.ultreon.mods.xinexlib.network.Networker;
import dev.ultreon.mods.xinexlib.network.packet.PacketToClient;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

public class S2CUpdatePacket implements PacketToClient<S2CUpdatePacket> {
    private final CompoundTag nbt;

    public S2CUpdatePacket(UUID laptop, String type, CompoundTag nbt) {
        this.nbt = new CompoundTag();
        this.nbt.putLongArray("uuid", new long[]{laptop.getMostSignificantBits(), laptop.getLeastSignificantBits()});
        this.nbt.putString("type", type);
        this.nbt.put("data", nbt);
    }

    public S2CUpdatePacket(FriendlyByteBuf buf) {
        this.nbt = buf.readNbt();
    }

    @Override
    public void handle(Networker connection) {
        Optional<long[]> uuidLongs = this.nbt.getLongArray("uuid");
        if (uuidLongs.isEmpty()) return;
        long[] longs = uuidLongs.get();
        UUID uuid = new UUID(longs[0], longs[1]);
        ClientLaptop.laptops.get(uuid).handlePacket(this.nbt.getString("type").orElse(null), this.nbt.getCompoundOrEmpty("data"));
        DebugLog.log("SQUARE: " + Arrays.toString(ClientLaptop.laptops.get(uuid).square));
    }

    @Override
    public void write(RegistryFriendlyByteBuf buffer) {

    }
}
