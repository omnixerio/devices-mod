package dev.ultreon.devices;

import dev.ultreon.devices.api.device.DeviceSerializer;
import dev.ultreon.devices.core.jobs.Job;
import dev.ultreon.mods.xinexlib.registrar.Registrar;
import dev.ultreon.mods.xinexlib.registrar.RegistrarManager;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

import java.util.function.Supplier;

public class UltreonDevicesRegistries {
    public static final ResourceKey<Registry<DeviceSerializer>> DEViCE_SERIALIZER = ResourceKey.createRegistryKey(UltreonDevices.res("device_serializer"));
    public static final ResourceKey<Registry<Job<?, ?>>> JOB = ResourceKey.createRegistryKey(UltreonDevices.res("job"));

    private final Supplier<RegistrarManager> registries;
    private Registrar<DeviceSerializer> deviceSerializer;
    private Registrar<Job<?, ?>> job;

    public UltreonDevicesRegistries(Supplier<RegistrarManager> registries) {
        this.registries = registries;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public void createRegistries() {
        RegistrarManager manager = registries.get();
        deviceSerializer = manager.createRegistrar(DEViCE_SERIALIZER, DeviceSerializer.class);
        job = manager.createRegistrar(JOB, (Class<Job<?, ?>>) (Class) Job.class);
    }

    public Registrar<DeviceSerializer> deviceSerializer() {
        return deviceSerializer;
    }

    public Registrar<Job<?, ?>> job() {
        return job;
    }
}
