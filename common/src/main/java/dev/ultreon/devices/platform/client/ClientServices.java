package dev.ultreon.devices.platform.client;

import static dev.ultreon.devices.platform.Services.load;

// Service loaders are a built-in Java feature that allow us to locate implementations of an interface that vary from one
// environment to another. In the context of MultiLoader we use this feature to access a mock API in the common code that
// is swapped out for the platform specific implementation at runtime.
public class ClientServices {
    /**
     * The client platform helper is used to provide utilities for the client environment.
     */
    public static final IClientPlatformHelper PLATFORM = load(IClientPlatformHelper.class);
}