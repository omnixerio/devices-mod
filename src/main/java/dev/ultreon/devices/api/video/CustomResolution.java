package dev.ultreon.devices.api.video;

import dev.ultreon.devices.programs.system.DisplayResolution;

public record CustomResolution(int width, int height) implements DisplayResolution {
}
