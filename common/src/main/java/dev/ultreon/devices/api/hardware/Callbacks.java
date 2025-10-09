package dev.ultreon.devices.api.hardware;

import dev.ultreon.devices.UltreonDevices;
import dev.ultreon.devices.network.PacketHandler;
import dev.ultreon.devices.network.packets.CallPacket;
import dev.ultreon.devices.network.packets.RegisterCallback;
import dev.ultreon.mods.xinexlib.Env;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Unit;

import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.CompletableFuture;
import java.util.function.*;

public class Callbacks {
    private final Map<Integer, CompletableFuture<?>> REGISTRATIONS = new WeakHashMap<>();
    private final Map<Integer, Callback> CALLBACK_IDS = new HashMap<>();
    private final Map<RemoteCallback, Integer> REMOTE_CALLBACKS = new WeakHashMap<>();
    private final Map<Integer, RemoteCallback> REMOTE_CALLBACK_IDS = new HashMap<>();
    private final Map<Integer, CompletableFuture<Value<?>[]>> CALLS = new WeakHashMap<>();
    private final BitSet calls = new BitSet();
    private final BitSet callbacks = new BitSet();
    private final BitSet remoteCallbacks = new BitSet();
    public final Env env;
    private final ServerPlayer player;
    private Long nextId = 0L;

    public Callbacks() {
        env = Env.CLIENT;
        player = null;
    }

    public Callbacks(ServerPlayer player) {
        env = Env.SERVER;
        this.player = player;
    }

    public int getId() {
        synchronized (calls) {
            int i = calls.nextClearBit(0);
            calls.set(i);
            return i;
        }
    }

    public void clearId(int id) {
        synchronized (calls) {
            calls.clear(id);
        }
    }

    public void sendCall(CompletableFuture<Value<?>[]> future, int id, Value<?>[] args) {
        int callId = getId();
        CALLS.put(callId, future);

        if (env == Env.CLIENT)
            PacketHandler.sendToServer(new CallPacket(callId, id, args));
        else if (env == Env.SERVER)
            PacketHandler.sendToClient(new CallPacket(callId, id, args), player);
    }

    public void onCall(int id, int callbackId, Value<?>[] args) {
        Object[] decodedArgs = new Object[args.length];
        for (int i = 0; i < args.length; i++) {
            decodedArgs[i] = args[i].value();
        }
        Object[] call = CALLBACK_IDS.get(callbackId).call(decodedArgs);
        if (call == null || call.length == 0) {
            sendReturn(id, new Value<?>[0]);
            return;
        }

        Value<?>[] returnValues = new Value<?>[call.length];
        for (int i = 0; i < call.length; i++) {
            returnValues[i] = Type.of(call[i]);
        }

        sendReturn(id, returnValues);
    }

    private void sendReturn(int id, Value<?>[] returnValues) {
        if (env == Env.CLIENT)
            PacketHandler.sendToServer(new CallPacket.Return(id, returnValues));
        else if (env == Env.SERVER)
            PacketHandler.sendToClient(new CallPacket.Return(id, returnValues), player);
    }

    public void onReturn(int id, Value<?>[] returnValues) {
        CompletableFuture<Value<?>[]> remove = CALLS.remove(id);
        if (remove == null) {
            UltreonDevices.LOGGER.warn("Received return for unknown call id {}", id);
            return;
        }
        remove.complete(returnValues);
        clearId(id);
    }

    public RegisterCallback.Reply onRegisterCallback(int replyId, Type<?>[] args, Type<?>[] returnTypes) {
        synchronized (remoteCallbacks) {
            int id = remoteCallbacks.nextClearBit(0);
            remoteCallbacks.set(id);
            REMOTE_CALLBACKS.put(new RemoteCallback(id, args, returnTypes), id);
            return new RegisterCallback.Reply(replyId, id);
        }
    }

    public CompletableFuture<?> registerCallback(Callback callback) {
        synchronized (callbacks) {
            CompletableFuture<?> future = new CompletableFuture<>();
            int id = callbacks.nextClearBit(0);
            callbacks.set(id);
            CALLBACK_IDS.put(id, callback);

            int id1 = getId();
            REGISTRATIONS.put(id1, future);
            if (env == Env.CLIENT) {
                PacketHandler.sendToServer(new RegisterCallback(id1, callback));
            } else if (env == Env.SERVER) {
                PacketHandler.sendToClient(new RegisterCallback(id1, callback), player);
            }
            return future;
        }
    }

    public void onRegisterReply(int replyId, int remoteCallbackId) {
        Integer callback = REMOTE_CALLBACKS.remove(REMOTE_CALLBACK_IDS.get(remoteCallbackId));
        if (callback == null) {
            UltreonDevices.LOGGER.warn("Received reply for unknown callback id {}", remoteCallbackId);
            return;
        }

        CompletableFuture<?> future = REGISTRATIONS.remove(replyId);
        if (future == null) {
            UltreonDevices.LOGGER.warn("Received reply for unknown registration id {}", replyId);
        }
    }

    public enum CallbackType {
        CONSUMER(1, 0),
        BI_CONSUMER(2, 0),
        FUNCTION(1, 1),
        BI_FUNCTION(2, 1),
        RUNNABLE(0, 0),
        SUPPLIER(0, 1);

        private final int argCount;

        private final int returnCount;
        CallbackType(int argCount, int returnCount) {
            this.argCount = argCount;
            this.returnCount = returnCount;
        }

        public int argCount() {
            return argCount;
        }

        public int returnCount() {
            return returnCount;
        }

    }
    public sealed interface Value<T> permits Value.Bool, Value.Double, Value.Float, Value.Int, Value.Long, Value.Str, Value.Void {

        T value();

        void encode(RegistryFriendlyByteBuf buffer);

        default void encodeFull(RegistryFriendlyByteBuf buffer) {
            buffer.writeByte(id());
            encode(buffer);
        }

        byte id();

        static Value<?> decodeFull(RegistryFriendlyByteBuf buffer) {
            return switch (buffer.readByte()) {
                case 0 -> Bool.decode(buffer);
                case 1 -> Int.decode(buffer);
                case 2 -> Str.decode(buffer);
                default -> null;
            };
        }

        record Bool(Boolean value) implements Value<Boolean> {

            private static final byte ID = 0;
            @Override
            public void encode(RegistryFriendlyByteBuf buffer) {
                buffer.writeBoolean(value);
            }

            public static Bool decode(RegistryFriendlyByteBuf buffer) {
                return new Bool(buffer.readBoolean());
            }

            public byte id() {
                return ID;
            }

        }
        record Str(String value) implements Value<String> {

            private static final byte ID = 1;
            @Override
            public void encode(RegistryFriendlyByteBuf buffer) {
                buffer.writeUtf(value);
            }

            public static Str decode(RegistryFriendlyByteBuf buffer) {
                return new Str(buffer.readUtf());
            }

            public byte id() {
                return ID;
            }

        }
        record Int(Integer value) implements Value<Integer> {
            private static final byte ID = 2;

            @Override
            public void encode(RegistryFriendlyByteBuf buffer) {
                buffer.writeVarInt(value);
            }

            public static Int decode(RegistryFriendlyByteBuf buffer) {
                return new Int(buffer.readVarInt());
            }

            public byte id() {
                return ID;
            }

        }
        record Long(java.lang.Long value) implements Value<java.lang.Long> {
            private static final byte ID = 3;
            @Override
            public void encode(RegistryFriendlyByteBuf buffer) {
                buffer.writeVarLong(value);
            }

            public static Long decode(RegistryFriendlyByteBuf buffer) {
                return new Long(buffer.readVarLong());
            }

            public byte id() {
                return ID;
            }

        }
        record Float(java.lang.Float value) implements Value<java.lang.Float> {
            private static final byte ID = 4;
            @Override
            public void encode(RegistryFriendlyByteBuf buffer) {
                buffer.writeFloat(value);
            }

            public static Float decode(RegistryFriendlyByteBuf buffer) {
                return new Float(buffer.readFloat());
            }

            public byte id() {
                return ID;
            }

        }
        record Double(java.lang.Double value) implements Value<java.lang.Double> {

            private static final byte ID = 5;
            @Override
            public void encode(RegistryFriendlyByteBuf buffer) {
                buffer.writeDouble(value);
            }

            public static Double decode(RegistryFriendlyByteBuf buffer) {
                return new Double(buffer.readDouble());
            }

            @Override
            public byte id() {
                return ID;
            }

        }
        record Void() implements Value<Unit> {

            private static final byte ID = 6;
            @Override
            public Unit value() {
                return Unit.INSTANCE;
            }

            @Override
            public void encode(RegistryFriendlyByteBuf buffer) {
            }

            public static Void decode(RegistryFriendlyByteBuf buffer) {
                return new Void();
            }

            @Override
            public byte id() {
                return ID;
            }

        }
    }
    public record Type<T>(Function<RegistryFriendlyByteBuf, Value<T>> decoder, byte id, Class<T> clazz) {

        public static Type<Boolean> BOOL = new Type<>(Value.Bool::decode, Value.Bool.ID, Boolean.class);
        public static Type<Integer> INT = new Type<>(Value.Int::decode, Value.Int.ID, Integer.class);
        public static Type<String> STR = new Type<>(Value.Str::decode, Value.Str.ID, String.class);
        public static Type<Unit> VOID = new Type<>(Value.Void::decode, Value.Void.ID, Unit.class);
        public static Type<Long> LONG = new Type<>(Value.Long::decode, Value.Long.ID, Long.class);
        public static Type<Float> FLOAT = new Type<>(Value.Float::decode, Value.Float.ID, Float.class);
        public static Type<Double> DOUBLE = new Type<>(Value.Double::decode, Value.Double.ID, Double.class);
        public static Type<?> fromId(byte b) {
            return switch (b) {
                case Value.Bool.ID -> BOOL;
                case Value.Int.ID -> INT;
                case Value.Str.ID -> STR;
                case Value.Void.ID -> VOID;
                case Value.Long.ID -> LONG;
                case Value.Float.ID -> FLOAT;
                case Value.Double.ID -> DOUBLE;
                default -> null;
            };
        }

        public static Value<?> of(Object o) {
            return switch (o) {
                case Boolean b -> new Value.Bool(b);
                case Integer i -> new Value.Int(i);
                case String s -> new Value.Str(s);
                case Unit u -> new Value.Void();
                case Long l -> new Value.Long(l);
                case Float f -> new Value.Float(f);
                case Double d -> new Value.Double(d);
                default -> throw new IllegalStateException("Unexpected value: " + o);
            };
        }

        public T cast(Object o) {
            return clazz.cast(o);
        }

    }
}
