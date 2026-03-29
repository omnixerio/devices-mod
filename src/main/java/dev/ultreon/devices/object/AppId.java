package dev.ultreon.devices.object;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

public class AppId {
    public static final StreamCodec<ByteBuf, AppId> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8.map(Identifier::parse, Identifier::toString), AppId::getId,
            ByteBufCodecs.BOOL, AppId::isSystemApp,
            AppId::new
    );

    private final Identifier id;
    private final boolean systemApp;

    public AppId(Identifier id, boolean systemApp) {
        this.id = id;
        this.systemApp = systemApp;
    }

    public Identifier getId() {
        return id;
    }

    public boolean isSystemApp() {
        return systemApp;
    }
}
