package dev.ultreon.devices.mixin.common;


import dev.architectury.injectables.targets.ArchitecturyTarget;
import dev.ultreon.devices.debug.DebugLog;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;

public class MixinPlugin implements IMixinConfigPlugin {
    @Override
    public void onLoad(String mixinPackage) {

    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    private static String getTrueName(String internalName) {
        if (internalName.startsWith("dev/ultreon/devices")) {
            return ArchitecturyTarget.getCurrentTarget() + "/" + internalName;
        }
        return internalName;
    }

    private static boolean trueEquals(String className, String className2) {
        return getTrueName(className).equals(getTrueName(className2));
    }

    private static void objectPrinter(Object o, String i) {
        try {
            if (o instanceof String || o instanceof Boolean || o instanceof Integer || o instanceof Character || o instanceof Long || o instanceof Byte || o instanceof Short || o instanceof Float || o instanceof Double) {
                DebugLog.log(i + o);
                return;
            } else if (o instanceof List list){
                if (list.isEmpty()) {
                    DebugLog.log(i + "    " + "Empty list");
                    return;
                }
                for (Object o1 : list) {
                    objectPrinter(o1, i + "    ");
                }
                return;
            }
            var fields = o.getClass().getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                var val = field.get(o);
                DebugLog.log(i + /*field.getName()); +*/ " : " + field.getType());
                objectPrinter(val, i + "    ");
            }
        } catch (Exception e) {

        }
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }
}
