package dev.ultreon.devices.platform.services;

import net.minecraft.resources.Identifier;

import java.util.function.Supplier;

public interface RegistrySupplier<T> extends Supplier<T> {
    Identifier getId();

    boolean isBound();
}
