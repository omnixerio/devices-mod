package com.ultreon.devices.event;

import com.ultreon.devices.Devices;
import com.ultreon.devices.api.WorldSavedData;
import com.ultreon.devices.api.utils.BankUtil;
import com.ultreon.devices.programs.email.EmailManager;
import dev.architectury.event.events.common.LifecycleEvent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.ServerLevelData;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
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
        final File modData = Objects.requireNonNull(minecraftServer, "World loaded without server").getWorldPath(DEVICES_MOD_DATA).toFile();
        if (!modData.exists()) {
            try {
                Files.createDirectories(modData.toPath());
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
        File modData = server.getWorldPath(DEVICES_MOD_DATA).toFile();
        if (!modData.exists()) {
            try {
                Files.createDirectories(modData.toPath());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        saveData(modData, "emails.dat", EmailManager.INSTANCE);
        saveData(modData, "bank.dat", BankUtil.INSTANCE);
    }

    private static void loadData(File modData, String fileName, WorldSavedData data) {
        File dataFile = new File(modData, fileName);
        if (!dataFile.exists()) {
            return;
        }
        try {
            CompoundTag nbt = NbtIo.readCompressed(dataFile);
            data.load(nbt);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void saveData(File modData, String fileName, WorldSavedData data) {
        try {
            File dataFile = new File(modData, fileName);
            if (!dataFile.exists()) {
                Files.createFile(dataFile.toPath());
            }

            CompoundTag nbt = new CompoundTag();
            data.save(nbt);
            NbtIo.writeCompressed(nbt, dataFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
