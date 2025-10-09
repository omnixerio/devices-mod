package dev.ultreon.devices.network.packets;

import dev.ultreon.devices.UltreonDevices;
import dev.ultreon.devices.api.hardware.Callback;
import dev.ultreon.devices.api.hardware.Callbacks;
import dev.ultreon.devices.client.UltreonDevicesClient;
import dev.ultreon.mods.xinexlib.network.Networker;
import dev.ultreon.mods.xinexlib.network.packet.PacketToClient;
import dev.ultreon.mods.xinexlib.network.packet.PacketToServer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

public class RegisterCallback implements PacketToClient<RegisterCallback>, PacketToServer<RegisterCallback> {
    private final int replyId;
    private final Callbacks.Type<?>[] args;
    private final Callbacks.Type<?>[] returnTypes;

    public RegisterCallback(RegistryFriendlyByteBuf buf) {
        replyId = buf.readInt();
        int argCount = buf.readByte();
        int returnCount = buf.readByte();
        args = new Callbacks.Type[argCount];
        returnTypes = new Callbacks.Type[returnCount];
        for (int i = 0; i < argCount; i++) {
            args[i] = Callbacks.Type.fromId(buf.readByte());
        }
        for (int i = 0; i < returnCount; i++) {
            returnTypes[i] = Callbacks.Type.fromId(buf.readByte());
        }
    }

    public RegisterCallback(int replyId, Callback callback) {
        this.replyId = replyId;
        args = callback.getArgumentTypes();
        returnTypes = callback.getReturnTypes();
    }

    @Override
    public void write(RegistryFriendlyByteBuf buf) {
        buf.writeInt(replyId);
        buf.writeByte(args.length);
        buf.writeByte(returnTypes.length);
        for (Callbacks.Type<?> type : args) {
            buf.writeByte(type.id());
        }
        for (Callbacks.Type<?> type : returnTypes) {
            buf.writeByte(type.id());
        }
    }

    @Override
    public void handle(Networker networker) {
        networker.sendToServer(UltreonDevicesClient.getInstance().getCallbacks().onRegisterCallback(replyId, args, returnTypes));
    }

    @Override
    public void handle(Networker networker, ServerPlayer serverPlayer) {
        networker.sendToClient(UltreonDevices.getInstance().getPlayer(serverPlayer).getCallbacks().onRegisterCallback(replyId, args, returnTypes), serverPlayer);
    }

    public static class Reply implements PacketToClient<Reply>, PacketToServer<Reply> {
        private final int replyId;
        private final int remoteCallbackId;

        public Reply(int replyId, int remoteCallbackId) {
            this.replyId = replyId;
            this.remoteCallbackId = remoteCallbackId;
        }

        public Reply(RegistryFriendlyByteBuf buf) {
            replyId = buf.readInt();
            remoteCallbackId = buf.readInt();
        }

        @Override
        public void handle(Networker networker) {
            UltreonDevicesClient.getInstance().getCallbacks().onRegisterReply(replyId, remoteCallbackId);
        }

        @Override
        public void handle(Networker networker, ServerPlayer serverPlayer) {
            UltreonDevices.getInstance().getPlayer(serverPlayer).getCallbacks().onRegisterReply(replyId, remoteCallbackId);
        }

        @Override
        public void write(RegistryFriendlyByteBuf registryFriendlyByteBuf) {

        }
    }
}
