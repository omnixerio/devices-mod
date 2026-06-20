package com.ultreon.devices.item.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;

import java.util.UUID;

public record EthernetConnection(
        BlockPos devicePos,
        UUID deviceUUID,
        String deviceName
) {
    public static final Codec<EthernetConnection> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BlockPos.CODEC.fieldOf("devicePos").forGetter(EthernetConnection::devicePos),
            UUIDUtil.CODEC.fieldOf("deviceUUID").forGetter(EthernetConnection::deviceUUID),
            Codec.STRING.fieldOf("deviceName").forGetter(EthernetConnection::deviceName)
    ).apply(instance, EthernetConnection::new));
}
