package com.ultreon.devices.programs.system.object;

import com.ultreon.devices.object.AppInfo;

import org.jetbrains.annotations.Nullable;
import java.util.Arrays;

/**
 * @author MrCrayfish
 */
public interface AppEntry {
    String id();

    String name();

    @Deprecated
    default String author() {
        StringBuilder a = new StringBuilder();
        Arrays.stream(authors()).forEach((str -> a.append(str).append(", ")));
        a.deleteCharAt(a.length()-1);
        a.deleteCharAt(a.length()-1);
        return a.toString();
    }

    String[] authors();

    String description();

    @Nullable
    String version();

    @Nullable
    AppInfo.Icon icon();

    @Nullable
    String[] screenshots();
}
