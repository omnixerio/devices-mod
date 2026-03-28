package com.ultreon.devices.network.task;

import com.ultreon.devices.api.task.Task;
import com.ultreon.devices.api.task.TaskManager;
import dev.architectury.networking.NetworkManager;
import dev.ultreon.mods.xinexlib.network.Networker;
import dev.ultreon.mods.xinexlib.network.packet.PacketToClient;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;

import java.util.function.Supplier;

public class ResponsePacket implements PacketToClient<ResponsePacket> {
    private final int id;
    private final Task request;
    private CompoundTag tag;

    public ResponsePacket(FriendlyByteBuf buf) {
        this.id = buf.readInt();
        boolean successful = buf.readBoolean();
        this.request = TaskManager.getTaskAndRemove(this.id);
        if (successful) this.request.setSuccessful();
        String name = buf.readUtf();
        this.tag = buf.readNbt();
    }

    public ResponsePacket(int id, Task request) {
        this.id = id;
        this.request = request;
    }

    @Override
    public void handle(Networker connection) {
        request.processResponse(tag);
        request.callback(tag);
    }

    @Override
    public void write(RegistryFriendlyByteBuf buffer) {
        buffer.writeInt(this.id);
        buffer.writeBoolean(this.request.isSucessful());
        buffer.writeUtf(this.request.getName());
        CompoundTag tag = new CompoundTag();
        this.request.prepareResponse(tag);
        buffer.writeNbt(tag);
        this.request.complete();
    }
}
