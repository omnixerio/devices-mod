package com.ultreon.devices.api;

import com.ultreon.devices.api.io.Drive;
import com.ultreon.devices.api.io.DriveRoot;
import com.ultreon.devices.api.io.FSResponse;
import com.ultreon.devices.core.DataPath;
import com.ultreon.devices.core.Laptop;
import com.ultreon.devices.object.Result;

import java.nio.file.Path;
import java.util.function.Consumer;

public class DeviceAPI {
    private final Laptop instance;

    public DeviceAPI(Laptop instance) {
        this.instance = instance;
    }

    public void openFile(String path, Consumer<Result<FileHandle>> response) {
        Drive mainDrive = Laptop.getMainDrive();
        if (mainDrive != null) {
            mainDrive.read(Path.of(path), fsResponse -> {
                boolean success = fsResponse.success();
                if (!success) {
                    response.accept(new Result<>(fsResponse.message(), null, false));
                    return;
                }

                byte[] data = fsResponse.data();
                FileHandle handle = new FileHandle(path, data);
                response.accept(new Result<>(null, handle, true));
            });
        } else {
            response.accept(new Result<>("No main drive", null, false));
        }
    }
}
