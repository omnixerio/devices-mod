package dev.ultreon.devices;

import dev.ultreon.quantum.GamePlatform;
import dev.ultreon.quantum.config.api.ConfigCategory;
import dev.ultreon.quantum.config.api.Configuration;
import dev.ultreon.quantum.config.api.props.BooleanProperty;
import dev.ultreon.quantum.config.api.props.IntProperty;
import dev.ultreon.quantum.ubo.DataTypes;
import dev.ultreon.quantum.ubo.types.MapType;

public class DeviceConfig {
    private DeviceConfig() {
        throw new AssertionError("Utility class");
    }

    private static final Configuration CONFIGURATION = new Configuration("devices-mod");

    private static final ConfigCategory CATEGORY_LAPTOP = CONFIGURATION.createCategory("laptopSettings");
    public static final IntProperty PING_RATE = CATEGORY_LAPTOP.create("pingRate", 20, 1, 200);

    private static final ConfigCategory CATEGORY_ROUTER = CONFIGURATION.createCategory("routerSettings");
    public static final IntProperty SIGNAL_RANGE = CATEGORY_ROUTER.create("signalRange", 20, 10, 100);
    public static final IntProperty BEACON_INTERVAL = CATEGORY_ROUTER.create("beaconInterval", 20, 1, 200);
    public static final IntProperty MAX_DEVICES = CATEGORY_ROUTER.create("maxDevices", 16, 1, 64);

    private static final ConfigCategory CATEGORY_PRINTING = CONFIGURATION.createCategory("printerSettings");
    public static final BooleanProperty OVERRIDE_PRINT_SPEED = CATEGORY_PRINTING.create("overridePrintSpeed", false);
    public static final IntProperty CUSTOM_PRINT_SPEED = CATEGORY_PRINTING.create("customPrintSpeed", 20, 1, 600);
    public static final IntProperty MAX_PAPER_COUNT = CATEGORY_PRINTING.create("maxPaperCount", 64, 0, 99);

    private static final ConfigCategory CATEGORY_PIXEL_PAINTER = CONFIGURATION.createCategory("pixelPainter");
    public static final BooleanProperty PIXEL_PAINTER_ENABLE = CATEGORY_PIXEL_PAINTER.create("enabled", true);
    public static final BooleanProperty RENDER_PRINTED_3D = CATEGORY_PIXEL_PAINTER.create("renderPrintedIn3d", false);

    public static final ConfigCategory CATEGORY_DEBUG = CONFIGURATION.createCategory("debug");
    public static final BooleanProperty DEBUG_BUTTON = CATEGORY_DEBUG.create("debugButton", GamePlatform.get().isDevEnvironment());

    // TODO *** Add read/write of synchronization tags of the config file if needed ***

    public static void readSyncTag(MapType tag) {
        if (tag.contains("pingRate", DataTypes.INT)) PING_RATE.setValue(tag.getInt("pingRate"));
        if (tag.contains("signalRange", DataTypes.INT)) SIGNAL_RANGE.setValue(tag.getInt("signalRange"));
    }

    public static MapType writeSyncTag() {
        MapType tag = new MapType();
        tag.putInt("pingRate", PING_RATE.getValue());
        tag.putInt("signalRange", SIGNAL_RANGE.getValue());
        return tag;
    }

    public static void init() {
        // NO-OP
    }

    public static void restore() {
        // NO-OP
    }

    public static void save() {
        CONFIGURATION.save();
    }

    public static void reload() {
        CONFIGURATION.load();
    }
}
