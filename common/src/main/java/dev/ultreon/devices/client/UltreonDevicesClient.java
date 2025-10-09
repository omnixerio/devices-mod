package dev.ultreon.devices.client;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.ultreon.devices.api.device.RemoteDevice;
import dev.ultreon.devices.api.hardware.Callbacks;
import dev.ultreon.devices.core.UltreonDevicesConn;
import dev.ultreon.devices.core.jobs.JobManager;
import dev.ultreon.mods.xinexlib.Env;
import dev.ultreon.mods.xinexlib.client.event.LocalPlayerJoinEvent;
import dev.ultreon.mods.xinexlib.client.event.LocalPlayerQuitEvent;
import dev.ultreon.mods.xinexlib.event.system.EventSystem;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.ApiStatus;

public abstract class UltreonDevicesClient implements UltreonDevicesConn {
    private static UltreonDevicesClient instance;
    private Callbacks callbacks;
    private JobManager jobManager;
    private RemoteDevice openDevice;

    @ApiStatus.Internal
    protected UltreonDevicesClient() {
        instance = this;
        EventSystem.MAIN.on(LocalPlayerJoinEvent.class, event -> {
            callbacks = new Callbacks();
            jobManager = new JobManager();
        });
        EventSystem.MAIN.on(LocalPlayerQuitEvent.class, event -> {
            jobManager = null;
            callbacks = null;
        });
    }

    public static UltreonDevicesClient getInstance() {
        return instance;
    }

    public static boolean isOnRenderThread() {
        return RenderSystem.isOnRenderThread();
    }

    public static void postRenderCall(Runnable task) {
        if (UltreonDevicesClient.isOnRenderThread()) task.run();
        else RenderSystem.recordRenderCall(task::run);
    }

    @Override
    public Callbacks getCallbacks() {
        return callbacks;
    }

    @Override
    public JobManager getJobs() {
        return jobManager;
    }

    public RemoteDevice getOpenDevice() {
        return openDevice;
    }

    public void openDevice(RemoteDevice device) {
        if (openDevice != null) {
            openDevice.close();
        }

        openDevice = device;
    }
}
