package com.ultreon.devices.compat;

import com.ultreon.devices.Devices;
import com.ultreon.devices.block.entity.*;
import com.ultreon.mods.advanceddebug.api.extension.Extension;
import com.ultreon.mods.advanceddebug.api.extension.ExtensionInfo;
import com.ultreon.mods.advanceddebug.util.ImGuiEx;
import imgui.ImGui;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.Objects;

/**
 * This class represents an advanced debugging extension.
 */
@ExtensionInfo(Devices.MOD_ID)
public class AdvancedDebugExt implements Extension {
    @Override
    public void handleBlockEntity(BlockEntity blockEntity) {
        if (!(blockEntity instanceof DeviceBlockEntity device) || !ImGui.collapsingHeader("Network Device")) {
            return;
        }
        displayCommonDeviceDetails(device);

        if (blockEntity instanceof DeviceBlockEntity.Colored colored) {
            handleColoredDevice(colored);
        }
        if (blockEntity instanceof NetworkDeviceBlockEntity networkDevice && ImGui.collapsingHeader("Connection")) {
            handleNetworkDevice(networkDevice);
        }
        if (blockEntity instanceof PrinterBlockEntity printer && ImGui.collapsingHeader("Printer")) {
            handlePrinter(printer);
        }
        if (blockEntity instanceof ComputerBlockEntity computer && ImGui.collapsingHeader("Printer")) {
            handleComputer(computer);
        }
        if (blockEntity instanceof RouterBlockEntity router && ImGui.collapsingHeader("Printer")) {
            handleRouter(router);
        }

        ImGui.treePop();
    }

    private void displayCommonDeviceDetails(DeviceBlockEntity device) {
        ImGui.treePush();
        ImGuiEx.text("Display Name: ", () -> device.getDisplayName());
        ImGuiEx.text("Device Name: ", () -> device.getDeviceName());
        ImGuiEx.nbt("Pipeline: ", () -> device.getPipeline());
        ImGuiEx.editString("Custom Name: ", createDevicePropertyIdentifier(device, "customName"), () -> device.hasCustomName() ? device.getCustomName() : "", device::setCustomName);
    }

    private void handleColoredDevice(DeviceBlockEntity.Colored colored) {
        ImGuiEx.editEnum("Color: ", createDevicePropertyIdentifier(colored, "color"), colored::getColor, colored::setColor);
    }

    private void handleNetworkDevice(NetworkDeviceBlockEntity networkDevice) {
        ImGui.treePush();
        ImGuiEx.bool("Is Connected: ", () -> networkDevice.getConnection().isConnected());
        ImGuiEx.text("Router ID: ", () -> networkDevice.getConnection().getRouterId());
        ImGuiEx.text("Router Position: ", () -> Objects.requireNonNull(networkDevice.getConnection().getRouterPos()).toShortString());
        ImGui.treePop();
    }

    private void handlePrinter(PrinterBlockEntity printer) {
        ImGui.treePush();
        ImGuiEx.editEnum("State: ", createDevicePropertyIdentifier(printer, "state"), printer::getState, printer::setState);
        ImGuiEx.button("Add Paper: ", createDevicePropertyIdentifier(printer, "addPaper"), () -> printer.addPaper(new ItemStack(Items.PAPER), true));
        ImGui.treePop();
    }

    private void handleComputer(ComputerBlockEntity computer) {
        ImGui.treePush();
        ImGuiEx.nbt("Application Data: ", () -> computer.getApplicationData());
        ImGuiEx.nbt("System Data: ", () -> computer.getSystemData());
        ImGuiEx.editEnum("External Drive Color: ", createDevicePropertyIdentifier(computer, "externalDriveColor"), computer::getExternalDriveColor, v -> {
        });
        ImGui.treePop();
    }

    private void handleRouter(RouterBlockEntity router) {
        ImGui.treePush();
        ImGuiEx.text("UUID: ", () -> router.getRouter().getId());
        ImGuiEx.text("Pos: ", () -> router.getRouter().getPos());
        ImGui.treePop();
    }

    private String createDevicePropertyIdentifier(DeviceBlockEntity device, String propertyName) {
        return "$$BlockEntity[" + device.getId() + "]::" + propertyName;
    }
}
