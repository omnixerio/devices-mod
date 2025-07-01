package dev.ultreon.devices.datagen;

import dev.ultreon.devices.init.DeviceBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;

public class DevicesBlockLootTableGenerator extends FabricBlockLootTableProvider {
    public DevicesBlockLootTableGenerator(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generate() {
        DeviceBlocks.getAllLaptops().forEach(this::dropSelf);
        DeviceBlocks.getAllOfficeChairs().forEach(this::dropSelf);
        DeviceBlocks.getAllPrinters().forEach(this::dropSelf);
        DeviceBlocks.getAllRouters().forEach(this::dropSelf);
    }
}
