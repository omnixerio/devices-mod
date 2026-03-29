package dev.ultreon.devices.api.print;

import com.google.common.collect.HashBiMap;
import dev.ultreon.devices.UltreonDevicesCommon;
import net.minecraft.resources.Identifier;

import org.jetbrains.annotations.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * @author MrCrayfish
 */
public class PrintingManager {
    private static final HashBiMap<String, Class<? extends IPrint>> registeredPrints = HashBiMap.create();

    private static Map<String, IPrint.Renderer> registeredRenders;

    public static Map<String, IPrint.Renderer> getRegisteredRenders() {
        return registeredRenders;
    }

    public static void setRegisteredRenders(Map<String, IPrint.Renderer> registeredRenders) {
        PrintingManager.registeredRenders = registeredRenders;
    }

    public static void registerPrint(Identifier identifier, Class<? extends IPrint> classPrint) {
        try {
            classPrint.getConstructor().newInstance();
            if (UltreonDevicesCommon.getInstance().registerPrint(identifier, classPrint)) {
                UltreonDevicesCommon.LOGGER.info("Registering print '" + classPrint.getName() + "'");
                registeredPrints.put(identifier.toString(), classPrint);
            } else {
                UltreonDevicesCommon.LOGGER.error("The print '" + classPrint.getName() + "' could not be registered due to a critical error!");
            }
        } catch (NoSuchMethodException e) {
            UltreonDevicesCommon.LOGGER.error("The print '" + classPrint.getName() + "' is missing an empty constructor and could not be registered!");
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            UltreonDevicesCommon.LOGGER.error("The print '" + classPrint.getName() + "' could not be registered due to a critical error!");
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

    public static IPrint.Renderer getRenderer(IPrint print) {
        String id = getPrintIdentifier(print);
        return registeredRenders.get(id);
    }

    public static IPrint.Renderer getRenderer(String identifier) {
        return registeredRenders.get(identifier);
    }

    public static String getPrintIdentifier(IPrint print) {
        return registeredPrints.inverse().get(print.getClass());
    }
}
