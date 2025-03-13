package dev.ultreon.devices.api.io;

import dev.ultreon.devices.programs.system.component.FileInfo;

import java.util.List;

public record DriveRoot(
        List<FileInfo> files,
        FileInfo info
) {
}
