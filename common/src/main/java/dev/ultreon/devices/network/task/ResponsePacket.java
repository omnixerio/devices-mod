package dev.ultreon.devices.network.task;

import dev.ultreon.devices.api.task.Task;
import dev.ultreon.devices.api.task.TaskManager;
import dev.ultreon.mods.xinexlib.network.Networker;
import dev.ultreon.mods.xinexlib.network.packet.PacketToClient;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;

public class ResponsePacket implements PacketToClient<ResponsePacket> {
    private final int id;
    private final Task request;
    private CompoundTag tag;

    public ResponsePacket(RegistryFriendlyByteBuf buf) {
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
    public void write(RegistryFriendlyByteBuf buf) {
        buf.writeInt(this.id);
        buf.writeBoolean(this.request.isSucessful());
        buf.writeUtf(this.request.getName());
        CompoundTag tag = new CompoundTag();
        this.request.prepareResponse(tag);
        buf.writeNbt(tag);
        this.request.complete();
    }

    @Override
    public void handle(Networker networker) {
        request.processResponse(tag);
        request.callback(tag);
    }
}
