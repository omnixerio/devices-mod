package dev.ultreon.devices.util;

import com.google.common.collect.ImmutableMap;
import dev.ultreon.mods.xinexlib.registrar.Registrar;
import dev.ultreon.mods.xinexlib.registrar.RegistrySupplier;
import net.minecraft.world.item.DyeColor;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public abstract class DyeableRegistration<T extends O, O> implements Iterable<RegistrySupplier<T, O>> {
    private final Map<DyeColor, RegistrySupplier<T, O>> map = new EnumMap<>(DyeColor.class);
    private final List<RegistrySupplier<T, O>> l = new ArrayList<>();
    protected DyeableRegistration() {
        var registrar = this.autoInit();
        if (registrar != null) {
            register(registrar, this);
        }
    }
    public static <T extends O, O> void register(Registrar<O> registrar, DyeableRegistration<T, O> dyeableRegistration) {
        for (DyeColor dye : getDyes()) {
            var dg = dyeableRegistration.register(registrar, dye);
            dyeableRegistration.l.add(dg);
            dyeableRegistration.map.put(dye, dg);
        }
    }

    private static List<@NotNull DyeColor> getDyes() {
        return List.of(
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
    public abstract RegistrySupplier<T, O> register(Registrar<O> registrar, DyeColor color);

    public ImmutableMap<DyeColor, RegistrySupplier<T, O>> getMap() {
        return ImmutableMap.copyOf(map);
    }

    protected Registrar<O> autoInit() {
        return null;
    }

    public RegistrySupplier<T, O> of(DyeColor dyeColor) {
        return map.get(dyeColor);
    }

    @NotNull
    @Override
    public Iterator<RegistrySupplier<T, O>> iterator() {
        return l.iterator();
    }
}
