package com.ultreon.devices;

import dev.architectury.injectables.annotations.ExpectPlatform;
import dev.architectury.platform.Platform;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraftforge.common.ForgeConfigSpec;

public class DeviceConfig {
    private static final String CATEGORY_LAPTOP = "laptopSettings";
    public static final ForgeConfigSpec.IntValue PING_RATE;

    private static final String CATEGORY_ROUTER = "routerSettings";
    public static final ForgeConfigSpec.IntValue SIGNAL_RANGE;
    public static final ForgeConfigSpec.IntValue BEACON_INTERVAL;
    public static final ForgeConfigSpec.IntValue MAX_DEVICES;

    private static final String CATEGORY_PRINTING = "printerSettings";
    public static final ForgeConfigSpec.BooleanValue OVERRIDE_PRINT_SPEED;
    public static final ForgeConfigSpec.IntValue CUSTOM_PRINT_SPEED;
    public static final ForgeConfigSpec.IntValue MAX_PAPER_COUNT;

    private static final String CATEGORY_PIXEL_PAINTER = "pixelPainter";
    public static final ForgeConfigSpec.BooleanValue PIXEL_PAINTER_ENABLE;
    public static final ForgeConfigSpec.BooleanValue RENDER_PRINTED_3D;

    public static final String CATEGORY_DEBUG = "debug";
    public static final ForgeConfigSpec.BooleanValue DEBUG_BUTTON;

    public static final ForgeConfigSpec CONFIG;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        PING_RATE = builder.comment("The amount of ticks the laptop waits until sending another ping to it's connected router.")
                .defineInRange(CATEGORY_LAPTOP + ".pingRate", 20, 1, 200);

        SIGNAL_RANGE = builder.comment("The range that routers can produce a signal to devices. This is the radius in blocks. Be careful when increasing this value, the performance is O(n^3) and larger numbers will have a bigger impact on the server")
                .defineInRange(CATEGORY_ROUTER + ".signalRange", 20, 10, 100);
        BEACON_INTERVAL = builder.comment("The amount of ticks the router waits before sending out a beacon signal. Higher number will increase performance but devices won't know as quick if they lost connection.")
                .defineInRange(CATEGORY_ROUTER + ".beaconInterval", 20, 1, 200);
        MAX_DEVICES = builder.comment("The maximum amount of devices that can be connected to the router.")
                .defineInRange(CATEGORY_ROUTER + ".maxDevices", 16, 1, 64);

        OVERRIDE_PRINT_SPEED = builder.comment("If enable, overrides all printing times with customPrintSpeed property")
                .define(CATEGORY_PRINTING + ".overridePrintSpeed", false);
        CUSTOM_PRINT_SPEED = builder.comment("The amount of seconds it takes to print a page. This is overridden if overridePrintSpeed is enabled.")
                .defineInRange(CATEGORY_PRINTING + ".customPrintSpeed", 20, 1, 600);
        MAX_PAPER_COUNT = builder.comment("The maximum amount of paper that can be used in the printer.")
                .defineInRange(CATEGORY_PRINTING + ".maxPaperCount", 64, 0, 99);

        PIXEL_PAINTER_ENABLE = builder.comment("Enable or disable the Pixel Painter app.")
                .define(CATEGORY_PIXEL_PAINTER + ".enabled", true);
        RENDER_PRINTED_3D = builder.comment("Should the pixels on printed pictures be render in 3D? Warning, this will decrease the performance of the game. You shouldn't enable it if you have a slow computer.")
                .define(CATEGORY_PIXEL_PAINTER + ".renderPrintedIn3d", false);

        DEBUG_BUTTON = builder.comment("Display a button to access a worldless laptop")
                .define(CATEGORY_DEBUG + ".debugButton", Platform.isDevelopmentEnvironment());

        CONFIG = builder.build();
    }

    // TODO *** Add read/write of synchronization tags of the config file if needed ***

    public static void readSyncTag(CompoundTag tag) {
        if (tag.contains("pingRate", Tag.TAG_INT)) PING_RATE.set(tag.getInt("pingRate"));
        if (tag.contains("signalRange", Tag.TAG_INT)) SIGNAL_RANGE.set(tag.getInt("signalRange"));
    }

    public static CompoundTag writeSyncTag() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("pingRate", PING_RATE.get());
        tag.putInt("signalRange", SIGNAL_RANGE.get());
        return tag;
    }

    public static void init() {
        // NO-OP
    }

//    @ExpectPlatform
//    @PlatformOnly("fabric")
//    public static void.json register(ModLoadingContext context) {
//        throw new AssertionError();
//        //context.registerConfig(ModConfig.Type.CLIENT, CONFIG);
//    }

    public static void restore() {
        // NO-OP
    }

    public void save() {
        CONFIG.save();
    }

//    @SubscribeEvent
//    public static void.json onConfigChanged(ModConfigEvent.Reloading event) {
//        // TODO // Implement config reloading if needed.
//    }
}
