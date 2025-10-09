package dev.ultreon.devices.network.packets;

import dev.ultreon.devices.UltreonDevices;
import dev.ultreon.devices.client.UltreonDevicesClient;
import dev.ultreon.devices.core.jobs.Job;
import dev.ultreon.devices.core.jobs.SimpleJob;
import dev.ultreon.mods.xinexlib.network.Networker;
import dev.ultreon.mods.xinexlib.network.packet.PacketToClient;
import dev.ultreon.mods.xinexlib.network.packet.PacketToServer;
import io.netty.handler.codec.DecoderException;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

public class JobPacket<T, R> implements PacketToServer<JobPacket<T, R>> {
    private final int id;
    private final SimpleJob<T, R> job;
    private T value;

    public JobPacket(int id, SimpleJob<T, R> job, T value) {
        this.id = id;
        this.job = job;
        this.value = value;
    }

    public JobPacket(RegistryFriendlyByteBuf buf) {
        id = buf.readInt();
        job = Job.read(buf);
        value = job.readData(buf);
    }

    @Override
    public void handle(Networker connection, ServerPlayer player) {
        connection.sendToClient(new JobPacket.Reply<>(id, job, job.process(value, player)), player);
    }

    @Override
    public void write(RegistryFriendlyByteBuf buffer) {
        buffer.writeInt(id);
        buffer.writeInt(UltreonDevices.getRegistries().job().registry().getId(job));
        job.writeData(buffer, value);
    }

    public static class Reply<T, R> implements PacketToClient<Reply<T, R>> {
        private int id;
        private final Job<T, R> job;
        private final Job.Reply<R> reply;

        public Reply(int id, Job<T, R> job, Job.Reply<R> reply) {
            this.id = id;
            this.job = job;
            this.reply = reply;
        }

        public Reply(RegistryFriendlyByteBuf buffer) {
            Job<T, R> job1 = (Job<T, R>) UltreonDevices.getRegistries().job().registry().byId(buffer.readInt());
            if (job1 == null) throw new DecoderException(String.format("Job with id %d does not exist", buffer.readInt()));
            job = job1;
            reply = job.createReply().read(buffer);
        }

        @Override
        public void handle(Networker connection) {
            UltreonDevicesClient.getInstance().getJobs().onReply(id, reply.data());
        }

        @Override
        public void write(RegistryFriendlyByteBuf buffer) {

        }
    }
}
