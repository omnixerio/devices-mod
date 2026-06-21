package dev.ultreon.devices.network.serverbound;

import dev.ultreon.devices.OmnixerioDevicesMod;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

/**
 * @author MrCrayfish
 */
public record C2SSyncBlockPacket(
        BlockPos routerPos
) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<C2SSyncBlockPacket> TYPE = new CustomPacketPayload.Type<>(OmnixerioDevicesMod.id("serverbound/sync_block"));
    public static final StreamCodec<RegistryFriendlyByteBuf, C2SSyncBlockPacket> CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, C2SSyncBlockPacket::routerPos,
            C2SSyncBlockPacket::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
