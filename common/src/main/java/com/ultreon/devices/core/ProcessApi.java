package com.ultreon.devices.core;

import org.graalvm.polyglot.Value;

import java.util.function.IntConsumer;

public interface ProcessApi {
    void kill(int code);

    int pid();

    default void update() {

    }

    static ProcessApi of(PyProcess pyProcess) {
        return new ProcessApi() {
            @Override
            public void kill(int code) {
                pyProcess.kill(code);
            }

            @Override
            public int pid() {
                return pyProcess.getPid();
            }

            public void setOnExit(Value handler) {
                pyProcess.setOnExit(handler::executeVoid);
            }
        };
    }
}
