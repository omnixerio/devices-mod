package dev.ultreon.devices.util;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.world.item.DyeColor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public abstract class DyeableRegistration<T> implements Iterable<RegistrySupplier<T>> {
    private final HashMap<DyeColor, RegistrySupplier<T>> map = new HashMap<>();
    private final List<RegistrySupplier<T>> l = new ArrayList<>();
    public DyeableRegistration() {
        var registrar = this.autoInit();
        if (registrar != null) {
            register(registrar, this);
        }
    }
    public static <T> void register(Registrar<T> registrar, DyeableRegistration<T> dyeableRegistration) {
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
    public abstract RegistrySupplier<T> register(Registrar<T> registrar, DyeColor color);

    public ImmutableMap<DyeColor, RegistrySupplier<T>> getMap() {
        return ImmutableMap.copyOf(map);
    }

    protected Registrar<T> autoInit() {
        return null;
    }

    public RegistrySupplier<T> of(DyeColor dyeColor) {
        return map.get(dyeColor);
    }

    @NotNull
    @Override
    public Iterator<RegistrySupplier<T>> iterator() {
        return l.iterator();
    }
}
