package dev.ultreon.devices.core.jobs;

import dev.ultreon.devices.network.PacketHandler;
import dev.ultreon.devices.network.packets.JobPacket;
import dev.ultreon.mods.xinexlib.Env;
import net.minecraft.server.level.ServerPlayer;

import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class JobManager {
    private final Map<Integer, CompletableFuture<?>> jobs = new HashMap<>();
    private final BitSet runningJobs = new BitSet();

    private final Env env;
    private final ServerPlayer player;

    public JobManager() {
        env = Env.CLIENT;
        player = null;
    }

    public JobManager(ServerPlayer player) {
        env = Env.SERVER;
        this.player = player;
    }

    public <T, R> CompletableFuture<R> sendJob(SimpleJob<T, R> job, T value) {
        CompletableFuture<R> future = new CompletableFuture<>();
        int id;
        synchronized (runningJobs) {
            id = runningJobs.nextClearBit(0);
            runningJobs.set(id);
        }
        jobs.put(id, future);
        if (env == Env.CLIENT)
            PacketHandler.sendToServer(new JobPacket<>(id, job, value));
        return future;
    }

    public int requestId() {
        synchronized (runningJobs) {
            int i = runningJobs.nextClearBit(0);
            runningJobs.set(i);
            return i;
        }
    }

    public void freeId(int requestId) {
        synchronized (runningJobs) {
            runningJobs.clear(requestId);
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public <R> void onReply(int id, R data) {
        CompletableFuture future = jobs.remove(id);
        if (future != null) {
            future.complete(data);
        }
    }
}
