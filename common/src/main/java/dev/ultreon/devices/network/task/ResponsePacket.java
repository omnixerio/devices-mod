package dev.ultreon.devices.network.task;

import dev.ultreon.devices.api.task.Task;
import dev.ultreon.devices.api.task.TaskManager;
import dev.ultreon.mods.xinexlib.Env;
import dev.ultreon.mods.xinexlib.network.Networker;
import dev.ultreon.mods.xinexlib.network.packet.PacketToClient;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;

public class ResponsePacket implements PacketToClient<ResponsePacket> {
    private final int id;
    private final Task request;
    private CompoundTag tag;

    public ResponsePacket(RegistryFriendlyByteBuf buf) {
        id = buf.readInt();
        boolean successful = buf.readBoolean();
        request = TaskManager.getTaskAndRemove(id);
        if (successful) request.setSuccessful();
        String name = buf.readUtf();
        request.setName(name);
        tag = buf.readNbt();
    }

    public ResponsePacket(int id, Task request) {
        this.id = id;
        this.request = request;
    }

    @Override
    public void write(RegistryFriendlyByteBuf buf) {
        buf.writeInt(id);
        buf.writeBoolean(request.isSuccessful());
        buf.writeUtf(request.getName());
        CompoundTag tag = new CompoundTag();
        request.prepareResponse(buf.registryAccess(), tag);
        buf.writeNbt(tag);
    }

    @Override
    public void handle(Networker networker) {
        request.processResponse(SidedSystem.getRegistryProvider(Env.CLIENT), tag);
        request.callback(tag);
    }
}
