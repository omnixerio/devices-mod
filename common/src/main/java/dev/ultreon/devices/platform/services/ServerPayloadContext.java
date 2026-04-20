package dev.ultreon.devices.platform.services;

import dev.ultreon.devices.platform.client.PayloadContext;
import net.minecraft.server.level.ServerPlayer;

public record ServerPayloadContext(
        ServerPlayer player,
        net.minecraft.server.MinecraftServer server) implements PayloadContext {

}
