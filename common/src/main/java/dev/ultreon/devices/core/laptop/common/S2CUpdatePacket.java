package dev.ultreon.devices.core.laptop.common;

import dev.ultreon.devices.core.laptop.client.ClientLaptop;
import dev.ultreon.devices.debug.DebugLog;
import dev.ultreon.mods.xinexlib.network.Networker;
import dev.ultreon.mods.xinexlib.network.packet.PacketToClient;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;

import java.util.Arrays;
import java.util.UUID;

public class S2CUpdatePacket implements PacketToClient<S2CUpdatePacket> {
    private final CompoundTag nbt;

    public S2CUpdatePacket(UUID laptop, String type, CompoundTag nbt) {
        this.nbt = new CompoundTag();
        this.nbt.putUUID("uuid", laptop); // laptop uuid
        this.nbt.putString("type", type);
        this.nbt.put("data", nbt);
    }

    @Deprecated // do not call
    public S2CUpdatePacket(RegistryFriendlyByteBuf buf) {
        this.nbt = buf.readNbt();
    }

    @Override
    public void write(RegistryFriendlyByteBuf buf) {
        buf.writeNbt(nbt);
    }

    @Override
    public void handle(Networker networker) {
        ClientLaptop.laptops.get(this.nbt.getUUID("uuid")).handlePacket(this.nbt.getString("type"), this.nbt.getCompound("data"));
        DebugLog.log("SQUARE: " + Arrays.toString(ClientLaptop.laptops.get(this.nbt.getUUID("uuid")).square));
    }
}
