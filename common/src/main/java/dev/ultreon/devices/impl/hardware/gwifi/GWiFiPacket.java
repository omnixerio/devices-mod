package dev.ultreon.devices.impl.hardware.gwifi;

import com.mojang.datafixers.util.Either;
import dev.ultreon.devices.api.device.RemoteDevice;
import dev.ultreon.devices.api.driver.KnownProductIDs;
import dev.ultreon.devices.api.driver.KnownVendorIDs;
import dev.ultreon.devices.api.hardware.Callback;
import dev.ultreon.devices.api.hardware.Callbacks;
import dev.ultreon.devices.client.UltreonDevicesClient;
import dev.ultreon.devices.core.UltreonDevicesConn;
import dev.ultreon.devices.core.jobs.HardwareRequest;
import dev.ultreon.devices.core.jobs.Jobs;
import dev.ultreon.devices.core.network.NetworkState;
import dev.ultreon.devices.core.network.WiFiNetwork;
import dev.ultreon.devices.impl.hardware.HardwarePacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public record GWiFiPacket(Type type, Object... data) implements HardwarePacket<GWiFiPacket.GWiFiResponse> {
    @Override
    public GWiFiResponse process(RegistryFriendlyByteBuf buf) {
        return type.receive(this, buf);
    }

    @Override
    public CompletableFuture<GWiFiResponse> send(UltreonDevicesConn conn) {
        return type.send(this);
    }

    public enum Type {
        CLOSE(Close::process, GWiFiPacket::sendClose, GWiFiPacket::receiveClose),
        CONNECT(Connect::process, GWiFiPacket::sendConnect, GWiFiPacket::receiveConnect),
        DISCONNECT(Disconnect::process, GWiFiPacket::sendDisconnect, GWiFiPacket::receiveClose),
        INITIALIZE(Initialize::process, GWiFiPacket::sendInitialize, GWiFiPacket::receiveInitialize),
        SCAN(Scan::process, GWiFiPacket::sendScan, GWiFiPacket::receiveScan);
        private final Function<RegistryFriendlyByteBuf, GWiFiResponse> processor;
        private final Function<GWiFiPacket, CompletableFuture<GWiFiResponse>> sender;

        private final BiFunction<GWiFiPacket, RegistryFriendlyByteBuf, GWiFiResponse> receiver;

        Type(Function<RegistryFriendlyByteBuf, GWiFiResponse> processor, Function<GWiFiPacket, CompletableFuture<GWiFiResponse>> sender, BiFunction<GWiFiPacket, RegistryFriendlyByteBuf, GWiFiResponse> receiver) {
            this.processor = processor;
            this.sender = sender;
            this.receiver = receiver;
        }

        public GWiFiResponse process(RegistryFriendlyByteBuf buffer) {
            return processor.apply(buffer);
        }

        public CompletableFuture<GWiFiResponse> send(GWiFiPacket packet) {
            return sender.apply(packet);
        }

        public GWiFiResponse receive(GWiFiPacket packet, RegistryFriendlyByteBuf buffer) {
            return receiver.apply(packet, buffer);
        }

    }

    private GWiFiResponse receiveClose(RegistryFriendlyByteBuf registryFriendlyByteBuf) {
        return new Close();
    }

    private GWiFiResponse receiveInitialize(RegistryFriendlyByteBuf registryFriendlyByteBuf) {
        return new Initialize();
    }

    private GWiFiResponse receiveScan(RegistryFriendlyByteBuf registryFriendlyByteBuf) {
        return new Scan(registryFriendlyByteBuf.readList(WiFiNetwork::read));
    }

    private GWiFiResponse receiveConnect(RegistryFriendlyByteBuf registryFriendlyByteBuf) {
        return new Connect(registryFriendlyByteBuf.readEnum(NetworkState.class));
    }

    public record Connection(
            NetworkState state
    ) {



    }
    private CompletableFuture<GWiFiResponse> sendInitialize() {
        var ref = new Object() {
            Consumer<String> onConnected = (Consumer<String>) data[0];
            Runnable onDisconnected = (Runnable) data[1];
            Consumer<Integer> onStrength = (Consumer<Integer>) data[2];
        };

        UltreonDevicesClient client = UltreonDevicesClient.getInstance();
        Callbacks callbacks = client.getCallbacks();
        if (callbacks == null) {
            return CompletableFuture.failedFuture(new IOException("No callbacks registered."));
        }

        if (ref.onConnected == null) ref.onConnected = (s) -> {
        };
        if (ref.onDisconnected == null) ref.onDisconnected = () -> {
        };
        if (ref.onStrength == null) ref.onStrength = (i) -> {
        };

        RemoteDevice openDevice = client.getOpenDevice();

        return client.sendHwPacket(openDevice, KnownVendorIDs.UltreonStudios, KnownProductIDs.GWiFi, response -> CompletableFuture.allOf(
                callbacks.registerCallback(Callback.createConsumer(Callbacks.Type.STR, ref.onConnected)),
                callbacks.registerCallback(Callback.createRunnable(ref.onDisconnected)),
                callbacks.registerCallback(Callback.createConsumer(Callbacks.Type.INT, ref.onStrength))
        )).thenApply(response -> new Initialize());
    }

    private CompletableFuture<GWiFiResponse> sendClose() {
        UltreonDevicesClient client = UltreonDevicesClient.getInstance();
        RemoteDevice openDevice = client.getOpenDevice();
        int requestId = client.getJobs().requestId();
        return client.sendJob(Jobs.HW_GWIFI_CLOSE, new HardwareRequest<>(requestId, KnownVendorIDs.UltreonStudios, KnownProductIDs.GWiFi, Either.left(openDevice.getOrigin()), null))
                .thenApply(response -> {
                    client.getJobs().freeId(requestId);
                    return response.data();
                });
    }

    private CompletableFuture<GWiFiResponse> sendScan() {
        UltreonDevicesClient client = UltreonDevicesClient.getInstance();
        RemoteDevice openDevice = client.getOpenDevice();
        int requestId = client.getJobs().requestId();
        return client.sendJob(Jobs.HW_GWIFI_SCAN, new HardwareRequest<>(requestId, KnownVendorIDs.UltreonStudios, KnownProductIDs.GWiFi, Either.left(openDevice.getOrigin()), null))
                .thenApply(response -> {
                    client.getJobs().freeId(requestId);
                    if (response == null) return null;
                    return response.data();
                });
    }

    private CompletableFuture<GWiFiResponse> sendConnect() {
        UltreonDevicesClient client = UltreonDevicesClient.getInstance();
        RemoteDevice openDevice = client.getOpenDevice();
        int requestId = client.getJobs().requestId();
        String ssid = (String) data[0];
        return client.sendJob(Jobs.HW_GWIFI_CONNECT, new HardwareRequest<>(requestId, KnownVendorIDs.UltreonStudios, KnownProductIDs.GWiFi, Either.left(openDevice.getOrigin()), ssid))
                .handle((response, throwable) -> {
                    client.getJobs().freeId(requestId);
                    if (response == null) return null;
                    return response.data();
                });
    }

    private CompletableFuture<GWiFiResponse> sendDisconnect() {
        UltreonDevicesClient client = UltreonDevicesClient.getInstance();
        RemoteDevice openDevice = client.getOpenDevice();
        int requestId = client.getJobs().requestId();
        return client.sendJob(Jobs.HW_GWIFI_DISCONNECT, new HardwareRequest<>(requestId, KnownVendorIDs.UltreonStudios, KnownProductIDs.GWiFi, Either.left(openDevice.getOrigin()), null))
                .handle((response, throwable) -> {
                    client.getJobs().freeId(requestId);
                    if (response == null) return null;
                    return response.data();
                });
    }

    public sealed interface GWiFiResponse extends Response {

        void encode(RegistryFriendlyByteBuf buffer);

        static GWiFiResponse decode(RegistryFriendlyByteBuf buffer) {
            Type type = buffer.readEnum(Type.class);
            return type.process(buffer);
        }
    }

    public record Close() implements GWiFiResponse {
        @Override
        public void encode(RegistryFriendlyByteBuf buffer) {
            buffer.writeEnum(Type.CLOSE);
        }

        public static Close process(RegistryFriendlyByteBuf buffer) {
            return new Close();
        }
    }

    public record Initialize() implements GWiFiResponse {
        @Override
        public void encode(RegistryFriendlyByteBuf buffer) {
            buffer.writeEnum(Type.INITIALIZE);
        }

        public static Initialize process(RegistryFriendlyByteBuf buffer) {
            return new Initialize();
        }
    }

    public record Connect(
            NetworkState state
    ) implements GWiFiResponse {
        @Override
        public void encode(RegistryFriendlyByteBuf buffer) {
            buffer.writeEnum(Type.CONNECT);
            buffer.writeEnum(state);
        }

        public static Connect process(RegistryFriendlyByteBuf buffer) {
            return new Connect(buffer.readEnum(NetworkState.class));
        }
    }

    public record Disconnect() implements GWiFiResponse {
        @Override
        public void encode(RegistryFriendlyByteBuf buffer) {
            buffer.writeEnum(Type.CLOSE);
        }

        public static Disconnect process(RegistryFriendlyByteBuf buffer) {
            return new Disconnect();
        }
    }

    public record Scan(List<WiFiNetwork> names) implements GWiFiResponse {
        @Override
        public void encode(RegistryFriendlyByteBuf buffer) {
            buffer.writeEnum(Type.SCAN);
            buffer.writeVarInt(names.size());
            for (WiFiNetwork network : names) {
                network.write(buffer);
            }
        }

        public static Scan process(RegistryFriendlyByteBuf buffer) {
            int size = buffer.readVarInt();
            List<WiFiNetwork> networks = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                networks.add(WiFiNetwork.read(buffer));
            }
            return new Scan(networks);
        }
    }
}
