package dev.ultreon.devices.components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameObject {
    private final Map<Class<? extends GameComponent>, GameComponent> components = new HashMap<>();

    public <T extends GameComponent> T getComponent(Class<T> componentClass) {
        return componentClass.cast(components.get(componentClass));
    }

    public <T extends GameComponent> T getComponent(Class<T> componentClass, T defaultComponent) {
        T component = getComponent(componentClass);
        return component != null ? component : defaultComponent;
    }

    public <T extends GameComponent> List<T> getComponents(Class<T> componentClass) {
        List<T> list = new ArrayList<>();
        for (GameComponent gameComponent : components.values()) {
            if (componentClass.isInstance(gameComponent)) {
                T obj = componentClass.cast(gameComponent);
                list.add(obj);
            }
        }
        return list;
    }

    public void addComponent(GameComponent component) {
        components.put(component.getClass(), component);
    }

    public void removeComponent(GameComponent component) {
        components.remove(component.getClass());
    }

    public boolean hasComponent(Class<? extends GameComponent> componentClass) {
        return components.containsKey(componentClass);
    }
}
