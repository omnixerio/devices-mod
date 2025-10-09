package dev.ultreon.devices.core.jobs;

import com.mojang.datafixers.util.Either;
import dev.ultreon.devices.api.device.Device;
import dev.ultreon.devices.api.device.DeviceOrigin;
import dev.ultreon.devices.api.device.DeviceSerializer;
import dev.ultreon.devices.core.network.WiFiNetwork;
import dev.ultreon.devices.impl.hardware.gwifi.GWiFiPacket;
import dev.ultreon.devices.impl.hardware.gwifi.GWiFiPacket.GWiFiResponse;
import io.netty.handler.codec.DecoderException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;

import java.util.UUID;


public class Jobs {
    public static final SimpleJob<HardwareRequest<HardwareRequest.State>, HardwareResponse<Void>> HARDWARE_STATE = new SimpleJobBuilder<HardwareRequest<HardwareRequest.State>, HardwareResponse<Void>>()
            .sendData((i, buf) -> i.write(buf, FriendlyByteBuf::writeEnum))
            .receiveData(buf -> HardwareRequest.read(buf, (requestBuf) -> requestBuf.readEnum(HardwareRequest.State.class)))
            .reply((data, player) -> {
                DeviceOrigin deviceOrigin = data.origin().right().orElseThrow(() -> new DecoderException("Device origin is not a remote device"));
                Device device = deviceOrigin.locate(player.server, player.serverLevel(), player);
                return device.onHardwareRequest(data, void.class);
            })
            .sendReply((data, buf) -> buf.writeVarInt(data.requestId()))
            .receiveReply(buf -> new HardwareResponse<>(buf.readVarInt(), null))
            .build();
    public static final SimpleJob<HardwareRequest<Void>, HardwareResponse<GWiFiPacket.Disconnect>> HW_GWIFI_DISCONNECT = new SimpleJobBuilder<HardwareRequest<Void>, HardwareResponse<GWiFiPacket.Disconnect>>()
            .sendData((request, buf) -> request.writeRemote(buf, (writeBuf, unused) -> {

            }))
            .receiveData(buf -> HardwareRequest.read(buf, readBuf -> null))
            .reply((data, player) -> {
                DeviceOrigin deviceOrigin = data.origin().right().orElseThrow(() -> new DecoderException("Device origin is not a remote device"));
                Device device = deviceOrigin.locate(player.server, player.serverLevel(), player);
                return device.onHardwareRequest(data, GWiFiPacket.Disconnect.class);
            })
            .sendReply((data, buf) -> data.write(buf, (writeBuf, response) -> response.encode(writeBuf)))
            .receiveReply(buf -> HardwareResponse.read(buf, (RegistryFriendlyByteBuf buffer) -> (GWiFiPacket.Disconnect) GWiFiResponse.decode(buffer)))
            .build();
    public static final SimpleJob<HardwareRequest<String>, HardwareResponse<GWiFiPacket.Connect>> HW_GWIFI_CONNECT = new SimpleJobBuilder<HardwareRequest<String>, HardwareResponse<GWiFiPacket.Connect>>()
            .sendData((request, buf) -> request.writeRemote(buf, FriendlyByteBuf::writeUtf))
            .receiveData(buf -> HardwareRequest.read(buf, FriendlyByteBuf::readUtf))
            .reply((data, player) -> {
                DeviceOrigin deviceOrigin = data.origin().right().orElseThrow(() -> new DecoderException("Device origin is not a remote device"));
                Device device = deviceOrigin.locate(player.server, player.serverLevel(), player);
                return device.onHardwareRequest(data, GWiFiPacket.Connect.class);
            })
            .sendReply((data, buf) -> data.write(buf, (writeBuf, response) -> response.encode(writeBuf)))
            .receiveReply(buf -> HardwareResponse.read(buf, (RegistryFriendlyByteBuf buffer) -> (GWiFiPacket.Connect) GWiFiResponse.decode(buffer)))
            .build();
    public static final SimpleJob<HardwareRequest<String>, HardwareResponse<GWiFiPacket.Close>> HW_GWIFI_CLOSE = new SimpleJobBuilder<HardwareRequest<String>, HardwareResponse<GWiFiPacket.Close>>()
            .sendData((request, buf) -> request.writeRemote(buf, FriendlyByteBuf::writeUtf))
            .receiveData(buf -> HardwareRequest.read(buf, FriendlyByteBuf::readUtf))
            .reply((data, player) -> {
                DeviceOrigin deviceOrigin = data.origin().right().orElseThrow(() -> new DecoderException("Device origin is not a remote device"));
                Device device = deviceOrigin.locate(player.server, player.serverLevel(), player);
                return device.onHardwareRequest(data, GWiFiPacket.Close.class);
            })
            .sendReply((data, buf) -> data.write(buf, (writeBuf, response) -> response.encode(writeBuf)))
            .receiveReply(buf -> HardwareResponse.read(buf, (RegistryFriendlyByteBuf buffer) -> (GWiFiPacket.Close) GWiFiResponse.decode(buffer)))
            .build();
    public static final SimpleJob<HardwareRequest<Void>, HardwareResponse<GWiFiPacket.Scan>> HW_GWIFI_SCAN = new SimpleJobBuilder<HardwareRequest<Void>, HardwareResponse<GWiFiPacket.Scan>>()
            .sendData((request, buf) -> request.writeRemote(buf, (writeBuf, unused) -> {}))
            .receiveData(buf -> HardwareRequest.read(buf, readBuf -> null))
            .reply((data, player) -> {
                DeviceOrigin deviceOrigin = data.origin().right().orElseThrow(() -> new DecoderException("Device origin is not a remote device"));
                Device device = deviceOrigin.locate(player.server, player.serverLevel(), player);
                return device.onHardwareRequest(data, GWiFiPacket.Scan.class);
            })
            .sendReply((data, buf) -> data.write(buf, (writeBuf, response) -> response.encode(writeBuf)))
            .receiveReply(buf -> HardwareResponse.read(buf, (RegistryFriendlyByteBuf buffer) -> (GWiFiPacket.Scan) GWiFiResponse.decode(buffer)))
            .build();
    public static final SimpleJob<HardwareRequest<Void>, HardwareResponse<GWiFiPacket.Ping>> HW_GWIFI_PING = new SimpleJobBuilder<HardwareRequest<Void>, HardwareResponse<GWiFiPacket.Ping>>()
            .sendData((request, buf) -> request.writeRemote(buf, (writeBuf, network) -> {}))
            .receiveData(buf -> HardwareRequest.read(buf, readBuf -> null))
            .reply((data, player) -> {
                DeviceOrigin deviceOrigin = data.origin().right().orElseThrow(() -> new DecoderException("Device origin is not a remote device"));
                Device device = deviceOrigin.locate(player.server, player.serverLevel(), player);
                return device.onHardwareRequest(data, GWiFiPacket.Ping.class);
            })
            .sendReply((data, buf) -> data.write(buf, (writeBuf, response) -> response.encode(writeBuf)))
            .receiveReply(buf -> HardwareResponse.read(buf, (RegistryFriendlyByteBuf buffer) -> (GWiFiPacket.Ping) GWiFiResponse.decode(buffer)))
            .build();
}
