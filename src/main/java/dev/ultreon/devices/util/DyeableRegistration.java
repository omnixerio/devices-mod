package dev.ultreon.devices.util;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.world.item.DyeColor;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.*;

public abstract class DyeableRegistration<T> implements Iterable<DeferredHolder<T, ? extends T>> {
    private final HashMap<DyeColor, DeferredHolder<T, ? extends T>> map = new HashMap<>();
    private final List<DeferredHolder<T, ? extends T>> l = new ArrayList<>();

    protected DyeableRegistration(DeferredRegister<T> registrar) {
        register(registrar, this);
    }

    private static <T> void register(DeferredRegister<T> registrar, DyeableRegistration<T> dyeableRegistration) {
        for (DyeColor dye : getDyes()) {
            var dg = dyeableRegistration.register(registrar, dye);
            dyeableRegistration.l.add(dg);
            dyeableRegistration.map.put(dye, dg);
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

    public abstract DeferredHolder<T, ? extends T> register(DeferredRegister<T> registrar, DyeColor color);

    public Map<DyeColor, DeferredHolder<T, ? extends T>> getMap() {
        return ImmutableMap.copyOf(map);
    }

    public DeferredHolder<T, ? extends T> of(DyeColor dyeColor) {
        return map.get(dyeColor);
    }

    @Override
    public Iterator<DeferredHolder<T, ? extends T>> iterator() {
        return l.iterator();
    }
}
