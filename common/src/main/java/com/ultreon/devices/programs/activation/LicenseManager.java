package com.ultreon.devices.programs.activation;

import com.ultreon.devices.core.Laptop;
import dev.architectury.utils.EnvExecutor;

public class LicenseManager {

    public static boolean isActivated() {
        return EnvExecutor.getEnvSpecific(() -> () -> Laptop.getInstance().isActivated(), () -> () -> false);
    }
}
