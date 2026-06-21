package dev.ultreon.devices.event;

import dev.architectury.event.events.common.LifecycleEvent;
import dev.ultreon.devices.api.WorldSavedData;
import dev.ultreon.devices.api.utils.BankUtil;
import dev.ultreon.devices.programs.email.EmailManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.storage.LevelResource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

//TODO
public class WorldDataHandler {
    private static final LevelResource DEVICES_MOD_DATA = new LevelResource("data/devices-mod");

    static {
        LifecycleEvent.SERVER_STARTING.register(WorldDataHandler::load);
        LifecycleEvent.SERVER_LEVEL_SAVE.register(WorldDataHandler::save);
    }

    public static void init() {
        // No-op
    }

    private static void load(MinecraftServer minecraftServer) {
        final Path modData = Objects.requireNonNull(minecraftServer, "World loaded without server").getWorldPath(DEVICES_MOD_DATA);
        if (Files.notExists(modData)) {
            try {
                Files.createDirectories(modData);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        loadData(modData, "emails.dat", EmailManager.INSTANCE);
        loadData(modData, "bank.dat", BankUtil.INSTANCE);
    }

    private static void save(ServerLevel serverLevel) {
        if (!serverLevel.dimension().equals(ServerLevel.OVERWORLD)) return;


        final MinecraftServer server = serverLevel.getServer();
        Path modData = server.getWorldPath(DEVICES_MOD_DATA);
        if (Files.notExists(modData)) {
            try {
                Files.createDirectories(modData);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        saveData(modData, "emails.dat", EmailManager.INSTANCE);
        saveData(modData, "bank.dat", BankUtil.INSTANCE);
    }

    private static void loadData(Path modData, String fileName, WorldSavedData data) {
        Path dataFile = modData.resolve(fileName);
        if (Files.notExists(dataFile))
            return;

        try {
            CompoundTag nbt = NbtIo.readCompressed(dataFile, NbtAccounter.unlimitedHeap());
            data.load(nbt);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void saveData(Path modData, String fileName, WorldSavedData data) {
        try {
            Path dataFile = modData.resolve(fileName);
            if (Files.notExists(dataFile)) {
                Files.createFile(dataFile);
            }

            CompoundTag nbt = new CompoundTag();
            data.save(nbt);
            NbtIo.writeCompressed(nbt, dataFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
