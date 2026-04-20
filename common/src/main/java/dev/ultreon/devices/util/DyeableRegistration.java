package dev.ultreon.devices.util;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import dev.ultreon.devices.platform.services.RegistrySupplier;
import net.minecraft.world.item.DyeColor;

import java.util.*;

public abstract class DyeableRegistration<T> implements Iterable<RegistrySupplier<T>> {
    private final HashMap<DyeColor, RegistrySupplier<T>> map = new HashMap<>();
    private final List<RegistrySupplier<T>> l = new ArrayList<>();

    protected DyeableRegistration() {
        for (DyeColor dye : getDyes()) {
            RegistrySupplier<T> register = register(dye);
            map.put(dye, register);
            l.add(register);
        }
    }

    private static ImmutableList<DyeColor> getDyes() {
        return ImmutableList.of(
                DyeColor.WHITE,
                DyeColor.LIGHT_GRAY,
                DyeColor.GRAY,
                DyeColor.BLACK,
                DyeColor.BROWN,
                DyeColor.RED,
                DyeColor.ORANGE,
                DyeColor.YELLOW,
                DyeColor.LIME,
                DyeColor.GREEN,
                DyeColor.CYAN,
                DyeColor.LIGHT_BLUE,
                DyeColor.BLUE,
                DyeColor.PURPLE,
                DyeColor.MAGENTA,
                DyeColor.PINK
        );
    }

    public abstract RegistrySupplier<T> register(DyeColor color);

    public ImmutableMap<DyeColor, RegistrySupplier<T>> getMap() {
        return ImmutableMap.copyOf(map);
    }

    public T of(DyeColor dyeColor) {
        return map.get(dyeColor).get();
    }

    @Override
    public Iterator<RegistrySupplier<T>> iterator() {
        return l.iterator();
    }
}
