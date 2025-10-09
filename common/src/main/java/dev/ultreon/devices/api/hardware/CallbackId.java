package dev.ultreon.devices.api.hardware;

import java.util.Objects;

public record CallbackId(
        long id,
        int argCount,
        int returnCount
) {
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CallbackId that = (CallbackId) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
