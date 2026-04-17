package dev.ultreon.devices.api.task;

import dev.ultreon.devices.OmnixerioDevicesCommon;
import dev.ultreon.devices.network.task.ServerboundRequestPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public final class TaskManager {
    private static TaskManager instance = null;

    private final Map<String, Task> registeredRequests = new HashMap<>();
    private final Map<Integer, Task> requests = new HashMap<>();
    private int currentId = 0;

    private TaskManager() {
    }

    private static TaskManager get() {
        if (instance == null) {
            instance = new TaskManager();
        }
        return instance;
    }

    public static void registerTask(Supplier<Task> clazz) {
        var task = clazz.get();
        try {
            OmnixerioDevicesCommon.LOGGER.info("Registering task '" + task.getName() + "'");
            get().registeredRequests.put(task.getName(), task);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sendTask(Task task) {
        TaskManager manager = get();
        if (!manager.registeredRequests.containsKey(task.getName())) {
            throw new RuntimeException("Unregistered Task: " + task.getClass().getName() + ". Use TaskManager#requestRequest to register your task.");
        }

        int requestId = manager.currentId++;
        manager.requests.put(requestId, task);
        if(Minecraft.getInstance().getConnection() != null) {
            CompoundTag tag = new CompoundTag();
            task.prepareRequest(tag);
            ClientPlayNetworking.send(new ServerboundRequestPacket(requestId, task.getName(), tag));
        }
    }

    public static Task getTask(String name) {
        return get().registeredRequests.get(name);
    }

    public static Task getTaskAndRemove(int id) {
        return get().requests.remove(id);
    }
}
