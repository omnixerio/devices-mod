package dev.ultreon.devices.event;

import dev.ultreon.devices.api.WorldSavedData;
import dev.ultreon.devices.api.utils.BankUtil;
import dev.ultreon.devices.programs.email.EmailManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.storage.LevelResource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;

//TODO
public class WorldDataHandler {
    private static final LevelResource DEVICES_MOD_DATA = new LevelResource("data/devices-mod");

    static {

    }

    public static void init() {
        // No-op
    }

    public static void load(MinecraftServer minecraftServer) {
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

    public static void save(MinecraftServer server, ServerLevel level) {
        if (!level.dimension().equals(ServerLevel.OVERWORLD)) return;

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
            CompoundTag nbt = NbtIo.readCompressed(dataFile.toPath(), NbtAccounter.unlimitedHeap());
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
            NbtIo.writeCompressed(nbt, dataFile.toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
