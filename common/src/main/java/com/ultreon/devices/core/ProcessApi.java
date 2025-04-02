package com.ultreon.devices.core;

public interface ProcessApi {
    void kill(int code);

    int pid();

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
        };
    }
}
