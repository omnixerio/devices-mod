package com.ultreon.devices.api.task;

import com.ultreon.devices.Devices;
import com.ultreon.devices.network.PacketHandler;
import com.ultreon.devices.network.task.RequestPacket;
import net.minecraft.client.Minecraft;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public final class TaskManager {
    private static TaskManager instance = null;

    private final Map<String, Task> registeredRequests = new HashMap<String, Task>();
    private final Map<Integer, Task> requests = new HashMap<Integer, Task>();
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
            Devices.LOGGER.info("Registering task '" + task.getName() + "'");
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
        if(Minecraft.getInstance().getConnection() != null)
        PacketHandler.INSTANCE.sendToServer(new RequestPacket(requestId, task));
    }

    public static Task getTask(String name) {
        return get().registeredRequests.get(name);
    }

    public static Task getTaskAndRemove(int id) {
        return get().requests.remove(id);
    }
}
