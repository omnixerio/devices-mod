package dev.ultreon.devices.api.device;

import dev.ultreon.devices.core.jobs.HardwareRequest;
import dev.ultreon.devices.core.jobs.HardwareResponse;
import net.minecraft.world.phys.Vec3;

public interface Device {
    DeviceOrigin getOrigin();

    Vec3 getPosition();

    <T, R> HardwareResponse<R> onHardwareRequest(HardwareRequest<T> request, Class<R> type);
}
