package dev.ultreon.devices.core;

import java.util.HashSet;
import java.util.Set;

public class PermissionManager {
    private static final ThreadLocal<Set<Permission>> GRANTED_PERMISSIONS = new ThreadLocal<>();

    public static boolean hasPermission(Permission permission) {
        if (GRANTED_PERMISSIONS.get() == null) return false;
        for (Permission p : GRANTED_PERMISSIONS.get()) {
            if (p.equals(permission)) return true;
        }
        return false;
    }

    static void grant(Permission permission, Runnable func) {
        Set<Permission> strings = GRANTED_PERMISSIONS.get();
        if (strings == null) {
            strings = new HashSet<>();
            GRANTED_PERMISSIONS.set(strings);
        }
        strings.add(permission);
        func.run();
        strings.remove(permission);
    }
}
