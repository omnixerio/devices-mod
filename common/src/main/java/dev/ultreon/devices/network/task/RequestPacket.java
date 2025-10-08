package dev.ultreon.devices.network.task;

import dev.ultreon.devices.api.task.Task;
import dev.ultreon.devices.api.task.TaskManager;
import dev.ultreon.devices.network.PacketHandler;
import dev.ultreon.mods.xinexlib.network.Networker;
import dev.ultreon.mods.xinexlib.network.packet.PacketToServer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

import java.util.Objects;

public class RequestPacket implements PacketToServer<RequestPacket> {
    private final int id;
    private final Task request;
    private CompoundTag tag;

    public RequestPacket(RegistryFriendlyByteBuf buf) {
        id = buf.readInt();
        String name = buf.readUtf();
        request = TaskManager.getTask(name).get();
        tag = buf.readNbt();
        //DebugLog.log("decoding");
    }

    public RequestPacket(int id, Task request) {
        this.id = id;
        this.request = request;
    }

    @Override
    public void write(RegistryFriendlyByteBuf buf) {
        buf.writeInt(id);
        buf.writeUtf(request.getName());
        CompoundTag tag = new CompoundTag();
        request.prepareRequest(buf.registryAccess(), tag);
        buf.writeNbt(tag);
    }

    @Override
    public void handle(Networker networker, ServerPlayer player) {
        request.processRequest(player.level().registryAccess(), tag, Objects.requireNonNull(player).level(), player);
        PacketHandler.sendToClient(new ResponsePacket(id, request), player);
    }

    public int getId() {
        return id;
    }
}
