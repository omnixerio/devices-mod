package dev.ultreon.devices.core.laptop.common;

import dev.ultreon.devices.core.laptop.server.ServerLaptop;
import dev.ultreon.mods.xinexlib.network.Networker;
import dev.ultreon.mods.xinexlib.network.packet.PacketToServer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;

public class C2SUpdatePacket implements PacketToServer<C2SUpdatePacket> {
    private final CompoundTag nbt;

    public C2SUpdatePacket(UUID laptop, String type, CompoundTag nbt) {
        this.nbt = new CompoundTag();
        this.nbt.putUUID("uuid", laptop); // laptop uuid
        this.nbt.putString("type", type);
        this.nbt.put("data", nbt);
    }

    @Deprecated // do not call
    public C2SUpdatePacket(RegistryFriendlyByteBuf buf) {
        this.nbt = buf.readNbt();
    }

    @Override
    public void write(RegistryFriendlyByteBuf buf) {
        buf.writeNbt(nbt);
    }

    @Override
    public void handle(Networker networker, ServerPlayer player) {
        ServerLaptop.laptops.get(this.nbt.getUUID("uuid")).handlePacket(player, this.nbt.getString("type"), this.nbt.getCompound("data"));
    }
}
