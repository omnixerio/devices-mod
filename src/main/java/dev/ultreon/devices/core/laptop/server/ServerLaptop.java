package dev.ultreon.devices.core.laptop.server;

import dev.ultreon.devices.core.laptop.common.ClientboundUpdatePacket;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.UUID;

public class ServerLaptop {
    public static HashMap<UUID, ServerLaptop> laptops = new HashMap<>();
    private final UUID uuid = new UUID(430985038594038L, 493058808830598L);
    ;

    public void sendPacket(ServerPlayer player, String type, CompoundTag nbt) {
        ServerPlayNetworking.send(player, new ClientboundUpdatePacket(this.uuid, type, nbt));
    }

    public UUID getUuid() {
        return uuid;
    }

    public void handlePacket(Player player, String type, CompoundTag data) {
        System.out.printf("Handling %s, %s%n", type, data);
        if (type.equals("mouseMoved")) {
            sendPacket((ServerPlayer) player, "placeSquare", data);
        }
    }
}
