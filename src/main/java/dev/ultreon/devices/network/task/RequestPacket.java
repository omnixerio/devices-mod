package dev.ultreon.devices.network.task;

import dev.ultreon.devices.api.task.Task;
import dev.ultreon.devices.api.task.TaskManager;
import dev.ultreon.devices.network.DevicesCommonNetworker;
import dev.ultreon.mods.xinexlib.network.Networker;
import dev.ultreon.mods.xinexlib.network.packet.PacketToServer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

public class RequestPacket implements PacketToServer<RequestPacket> {
    private final int id;
    private final Task request;
    private CompoundTag tag;

    public RequestPacket(FriendlyByteBuf buf) {
        this.id = buf.readInt();
        String name = buf.readUtf();
        this.request = TaskManager.getTask(name);
        this.tag = buf.readNbt();
        //DebugLog.log("decoding");
    }

    public RequestPacket(int id, Task request) {
        this.id = id;
        this.request = request;
    }

    public int getId() {
        return id;
    }

    @Override
    public void handle(Networker connection, ServerPlayer player) {
        //DebugLog.log("RECEIVED from " + ctx.get().getPlayer().getUUID());
        request.processRequest(tag, player.level(), player);
        DevicesCommonNetworker.INSTANCE.sendToClient(new ResponsePacket(id, request), player);
    }

    @Override
    public void write(RegistryFriendlyByteBuf buffer) {
        buffer.writeInt(this.id);
        buffer.writeUtf(this.request.getName());
        CompoundTag tag = new CompoundTag();
        this.request.prepareRequest(tag);
        buffer.writeNbt(tag);
    }

    public Task getRequest() {
        return request;
    }

    public CompoundTag getTag() {
        return tag;
    }
}
