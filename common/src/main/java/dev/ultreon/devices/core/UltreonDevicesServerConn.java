package dev.ultreon.devices.core;

import dev.ultreon.devices.api.hardware.Callbacks;
import dev.ultreon.devices.core.jobs.JobManager;
import dev.ultreon.mods.xinexlib.Env;
import net.minecraft.server.level.ServerPlayer;

public class UltreonDevicesServerConn implements UltreonDevicesConn {
    private final ServerPlayer serverPlayer;
    private final Callbacks callbacks;
    private final JobManager jobs;

    public UltreonDevicesServerConn(ServerPlayer serverPlayer) {
        this.serverPlayer = serverPlayer;
        callbacks = new Callbacks(serverPlayer);
        jobs = new JobManager(serverPlayer);
    }

    public ServerPlayer getServerPlayer() {
        return serverPlayer;
    }

    public Callbacks getCallbacks() {
        return callbacks;
    }

    @Override
    public JobManager getJobs() {
        return jobs;
    }
}
