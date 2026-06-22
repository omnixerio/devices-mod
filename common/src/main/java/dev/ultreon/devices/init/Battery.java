package dev.ultreon.devices.init;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class Battery {
    public static final Codec<Battery> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("capacity").forGetter(Battery::getCapacity),
            Codec.INT.fieldOf("charge").forGetter(Battery::getCharge)
    ).apply(instance, Battery::new));

    private final int capacity;
    private int charge = 0;

    public Battery(int capacity) {
        this.capacity = capacity;
    }

    public Battery(int capacity, int charge) {
        this.capacity = capacity;
        this.charge = charge;
    }

    public void charge(int amount) {
        charge = Math.min(capacity, charge + amount);
    }

    public void discharge(int amount) {
        charge = Math.max(0, charge - amount);
    }

    public boolean isFull() {
        return charge == capacity;
    }

    public boolean isEmpty() {
        return charge == 0;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getCharge() {
        return charge;
    }

    public float getChargeRatio() {
        return (float) charge / capacity;
    }
}
