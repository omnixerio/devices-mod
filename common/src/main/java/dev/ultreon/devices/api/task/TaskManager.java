package dev.ultreon.devices.api.task;

import dev.ultreon.devices.UltreonDevices;
import dev.ultreon.devices.network.PacketHandler;
import dev.ultreon.devices.network.task.RequestPacket;
import net.minecraft.client.Minecraft;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public final class TaskManager {
    private static TaskManager instance = null;

    private final Map<Class<?>, String> taskTypeMap = new HashMap<>();
    private final Map<String, Supplier<Task>> registeredRequests = new HashMap<>();
    private final Map<Integer, Task> requests = new ConcurrentHashMap<>();
    private int currentId = 0;

    private TaskManager() {
    }

    private static TaskManager get() {
        if (instance == null) {
            instance = new TaskManager();
        }
        return instance;
    }

    public static void registerTask(String name, Supplier<Task> factory, Class<? extends Task> type) {
        try {
            if (get().registeredRequests.containsKey(name))
                throw new RuntimeException("Task '" + name + "' is already registered!");

            UltreonDevices.LOGGER.info("Registering task '{}'", name);
            get().registeredRequests.put(name, factory);
            get().taskTypeMap.put(type, name);
        } catch (Exception e) {
            UltreonDevices.LOGGER.error("Failed to register task:", e);
        }
    }

    public static void sendTask(Task task) {
        TaskManager manager = get();
        String key = get().taskTypeMap.get(task.getClass());
        if (key == null)
            throw new RuntimeException("Unregistered Task: " + task.getClass().getName() + ". Use TaskManager#registerTask to register your task.");
        if (!manager.registeredRequests.containsKey(key)) {
            throw new RuntimeException("Unregistered Task: " + task.getClass().getName() + ". Use TaskManager#requestRequest to register your task.");
        }

        int requestId = manager.currentId++;
        if (manager.requests.containsKey(requestId))
            UltreonDevices.LOGGER.warn("Request ID collision! Request ID: {}", requestId, new Throwable());
        manager.requests.put(requestId, task);
        if (Minecraft.getInstance().getConnection() != null)
            PacketHandler.sendToServer(new RequestPacket(requestId, task));
    }

    public static Supplier<Task> getTask(String name) {
        return get().registeredRequests.get(name);
    }

    public static Task getTaskAndRemove(int id) {
        return get().requests.remove(id);
    }

    public static String getTaskName(Task task) {
        return get().taskTypeMap.get(task.getClass());
    }
}
