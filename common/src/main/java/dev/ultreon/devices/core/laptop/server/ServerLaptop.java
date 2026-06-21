package dev.ultreon.devices.core.laptop.server;

import dev.architectury.networking.NetworkManager;
import dev.ultreon.devices.OmnixerioDevicesMod;
import dev.ultreon.devices.network.clientbound.S2CUpdatePacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.UUID;

public class ServerLaptop {
    public static HashMap<UUID, ServerLaptop> laptops = new HashMap<>();
    private final UUID uuid = new UUID(430985038594038L, 493058808830598L);
    public void sendPacket(Player player, String type, CompoundTag nbt) {
        if (player instanceof ServerPlayer serverPlayer)
            NetworkManager.sendToPlayer(serverPlayer, new S2CUpdatePacket(this.uuid, type, nbt));
        else OmnixerioDevicesMod.LOGGER.error("Tried to send packet '{}' to non-server player", type);
    }

    public UUID getUuid() {
        return uuid;
    }

    public void handlePacket(Player player, String type, CompoundTag data) {
        if (type.equals("mouseMoved")) {
            var x = data.getDouble("x");
            var y = data.getDouble("y");
            sendPacket(player, "placeSquare", data);
        }
    }
}
