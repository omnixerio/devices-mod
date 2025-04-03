package com.ultreon.devices.core;

import org.graalvm.polyglot.Context;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;

public class ProcessManagement {
    private static final WeakHashMap<Context, List<PyProcess>> processes = new WeakHashMap<>();

    public static void add(Context current, PyProcess pyProcess) {
        List<PyProcess> pyProcesses = processes.computeIfAbsent(current, context -> new ArrayList<>());
        if (pyProcesses.size() > 64) {
            throw new IllegalStateException("Process limit reached!");
        }
        pyProcesses.add(pyProcess);
    }

    public static void remove(Context parent, PyProcess pyProcess) {
        List<PyProcess> pyProcesses = processes.get(parent);
        if (pyProcesses == null) return;

        pyProcesses.remove(pyProcess);
    }
}
