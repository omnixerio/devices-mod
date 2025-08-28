package com.ultreon.devices.api.print;

import com.google.common.collect.HashBiMap;
import com.ultreon.devices.Devices;
import dev.architectury.injectables.annotations.PlatformOnly;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resources.ResourceLocation;

import org.jetbrains.annotations.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * @author MrCrayfish
 */
public class PrintingManager {
    private static final HashBiMap<String, Class<? extends IPrint>> registeredPrints = HashBiMap.create();

    @Environment(EnvType.CLIENT)
    private static Map<String, IPrint.Renderer> registeredRenders;

    @PlatformOnly("fabric")
    public static Map<String, IPrint.Renderer> getRegisteredRenders() {
        return registeredRenders;
    }

    @PlatformOnly("fabric")
    public static void setRegisteredRenders(Map<String, IPrint.Renderer> registeredRenders) {
        PrintingManager.registeredRenders = registeredRenders;
    }

    public static void registerPrint(ResourceLocation identifier, Class<? extends IPrint> classPrint) {
        try {
            classPrint.getConstructor().newInstance();
            if (Devices.getInstance().registerPrint(identifier, classPrint)) {
                Devices.LOGGER.info("Registering print '" + classPrint.getName() + "'");
                registeredPrints.put(identifier.toString(), classPrint);
            } else {
                Devices.LOGGER.error("The print '" + classPrint.getName() + "' could not be registered due to a critical error!");
            }
        } catch (NoSuchMethodException e) {
            Devices.LOGGER.error("The print '" + classPrint.getName() + "' is missing an empty constructor and could not be registered!");
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            Devices.LOGGER.error("The print '" + classPrint.getName() + "' could not be registered due to a critical error!");
        }
    }

    public static boolean isRegisteredPrint(Class<? extends IPrint> clazz) {
        return registeredPrints.containsValue(clazz);
    }

    @Nullable
    public static IPrint getPrint(String identifier) {
        Class<? extends IPrint> clazz = registeredPrints.get(identifier);
        if (clazz != null) {
            try {
                return clazz.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Environment(EnvType.CLIENT)
    public static IPrint.Renderer getRenderer(IPrint print) {
        String id = getPrintIdentifier(print);
        return registeredRenders.get(id);
    }

    @Environment(EnvType.CLIENT)
    public static IPrint.Renderer getRenderer(String identifier) {
        return registeredRenders.get(identifier);
    }

    public static String getPrintIdentifier(IPrint print) {
        return registeredPrints.inverse().get(print.getClass());
    }
}
