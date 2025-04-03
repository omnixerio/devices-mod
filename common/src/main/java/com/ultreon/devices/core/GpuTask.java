package com.ultreon.devices.core;

import com.ultreon.devices.Devices;

import java.lang.reflect.Method;
import java.util.List;

public record GpuTask(String name, List<Object> args) {
    public Object run(Gpu of) {
        Class<? extends Gpu> aClass = of.getClass();
        for (Method method : aClass.getDeclaredMethods()) {
            if (method.getName().equals(name)) {
                try {
                    return method.invoke(of, args.toArray(Object[]::new));
                } catch (Exception e) {
                    of.errno = Gpu.ERRNO_ILLEGAL_ARGUMENT;
                    if (e.getCause() != null) {
                        of.error = e.getCause().getMessage();
                    } else {
                        of.error = e.getMessage();
                    }

                    Devices.LOGGER.error("GPU Error {} [{}]: {}", of.errno, name, e.getMessage(), e);
                }
            }
        }
        return null;
    }
}
