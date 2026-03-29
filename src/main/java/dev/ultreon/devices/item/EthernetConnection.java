package dev.ultreon.devices.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;

import java.util.Optional;
import java.util.UUID;

public record EthernetConnection(Optional<BlockPos> pos, Optional<UUID> connectedTo, Optional<String> name) {
    public static final Codec<EthernetConnection> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BlockPos.CODEC.optionalFieldOf("pos").forGetter(EthernetConnection::pos),
            Codec.STRING.optionalFieldOf("connected_to").xmap(s -> s.map(UUID::fromString), object -> object.map(UUID::toString)).forGetter(EthernetConnection::connectedTo),
            Codec.STRING.optionalFieldOf("name").forGetter(EthernetConnection::name)
    ).apply(instance, EthernetConnection::new));
}
