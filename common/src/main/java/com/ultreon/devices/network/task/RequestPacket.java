package com.ultreon.devices.network.task;

import com.ultreon.devices.Devices;
import com.ultreon.devices.api.task.Task;
import com.ultreon.devices.api.task.TaskManager;
import com.ultreon.devices.network.Packet;
import com.ultreon.devices.network.PacketHandler;
import dev.architectury.networking.NetworkManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.Objects;
import java.util.function.Supplier;

public class RequestPacket extends Packet<RequestPacket> {
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

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(this.id);
        buf.writeUtf(this.request.getName());
        CompoundTag tag = new CompoundTag();
        this.request.prepareRequest(tag);
        buf.writeNbt(tag);
    }

    @Override
    public boolean onMessage(Supplier<NetworkManager.PacketContext> ctx) {
        ctx.get().getPlayer().level().getServer().submit(() -> request.processRequest(tag, Objects.requireNonNull(ctx.get().getPlayer()).level(), ctx.get().getPlayer())).join();
        if (ctx.get().getPlayer() instanceof ServerPlayer player) {
            PacketHandler.sendToClient(new ResponsePacket(id, request), player);
        }
        return true;
    }

    public int getId() {
        return id;
    }

}
