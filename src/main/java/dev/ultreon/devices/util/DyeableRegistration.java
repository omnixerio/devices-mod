package dev.ultreon.devices.util;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.world.item.DyeColor;
import org.jspecify.annotations.NonNull;

import java.util.*;

public abstract class DyeableRegistration<T> implements Iterable<T> {
    private final HashMap<DyeColor, T> map = new HashMap<>();
    private final List<T> l = new ArrayList<>();

    protected DyeableRegistration() {
        for (DyeColor dye : getDyes()) {
            T register = register(dye);
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

    public abstract T register(DyeColor color);

    public Map<DyeColor, T> getMap() {
        return ImmutableMap.copyOf(map);
    }

    public T of(DyeColor dyeColor) {
        return map.get(dyeColor);
    }

    @Override
    public @NonNull Iterator<T> iterator() {
        return l.iterator();
    }
}
