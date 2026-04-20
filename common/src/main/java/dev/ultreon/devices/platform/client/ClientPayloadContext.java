package dev.ultreon.devices.platform.client;

import net.minecraft.client.player.LocalPlayer;

public record ClientPayloadContext(LocalPlayer player, net.minecraft.client.Minecraft client) implements PayloadContext {
}
