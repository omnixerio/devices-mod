package dev.ultreon.devices;

import java.util.HashSet;
import java.util.Set;

public class TestManager {
    private Set<String> enabled = new HashSet<>();

    public void load(Set<String> tests) {
        this.enabled = tests;
    }

    public boolean isEnabled(String name) {
        return enabled.contains(name);
    }
}
