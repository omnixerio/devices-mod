package dev.ultreon.devices.network.packets;

import dev.ultreon.devices.UltreonDevices;
import dev.ultreon.devices.api.hardware.CallbackId;
import dev.ultreon.devices.api.hardware.Callbacks;
import dev.ultreon.devices.client.UltreonDevicesClient;
import dev.ultreon.mods.xinexlib.network.Networker;
import dev.ultreon.mods.xinexlib.network.packet.PacketToClient;
import dev.ultreon.mods.xinexlib.network.packet.PacketToServer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

public class CallPacket implements PacketToClient<CallPacket>, PacketToServer<CallPacket> {
    private final int id;
    private final int callbackId;
    private final Callbacks.Value<?>[] args;

    public CallPacket(RegistryFriendlyByteBuf buf) {
        id = buf.readInt();
        callbackId = buf.readInt();
        args = new Callbacks.Value[buf.readInt()];
        for (int i = 0; i < args.length; i++) {
            args[i] = Callbacks.Value.decodeFull(buf);
        }
    }

    public CallPacket(int id, int callbackId, Callbacks.Value<?>[] args) {
        this.id = id;
        this.callbackId = callbackId;
        this.args = args;
    }

    @Override
    public void write(RegistryFriendlyByteBuf buf) {
        buf.writeInt(id);
        buf.writeInt(callbackId);
        buf.writeInt(args.length);
        for (Callbacks.Value<?> arg : args) {
            arg.encode(buf);
        }
    }

    @Override
    public void handle(Networker networker) {
        UltreonDevicesClient.getInstance().getCallbacks().onCall(id, callbackId, args);
    }

    @Override
    public void handle(Networker networker, ServerPlayer serverPlayer) {
        UltreonDevices.getInstance().getPlayer(serverPlayer).getCallbacks().onCall(id, callbackId, args);
    }

    public static class Return implements PacketToClient<Return>, PacketToServer<Return> {
        private final int id;
        private final Callbacks.Value<?>[] returnValues;

        public Return(int id, Callbacks.Value<?>[] returnValues) {
            this.id = id;
            this.returnValues = returnValues;
        }

        public Return(RegistryFriendlyByteBuf buf) {
            id = buf.readInt();
            returnValues = new Callbacks.Value[buf.readInt()];
            for (int i = 0; i < returnValues.length; i++) {
                returnValues[i] = Callbacks.Value.decodeFull(buf);
            }
        }

        @Override
        public void write(RegistryFriendlyByteBuf registryFriendlyByteBuf) {
            registryFriendlyByteBuf.writeInt(id);
            registryFriendlyByteBuf.writeInt(returnValues.length);
            for (Callbacks.Value<?> returnValue : returnValues) {
                returnValue.encodeFull(registryFriendlyByteBuf);
            }
        }

        @Override
        public void handle(Networker networker) {
            UltreonDevicesClient.getInstance().getCallbacks().onReturn(id, returnValues);
        }

        @Override
        public void handle(Networker networker, ServerPlayer serverPlayer) {

        }
    }
}
