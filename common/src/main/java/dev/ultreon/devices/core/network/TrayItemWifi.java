package dev.ultreon.devices.core.network;

import dev.ultreon.devices.DeviceConfig;
import dev.ultreon.devices.UltreonDevices;
import dev.ultreon.devices.api.app.Icons;
import dev.ultreon.devices.api.app.Layout;
import dev.ultreon.devices.api.app.OperatingSystem;
import dev.ultreon.devices.api.app.component.Button;
import dev.ultreon.devices.api.app.component.ItemList;
import dev.ultreon.devices.api.app.renderer.ListItemRenderer;
import dev.ultreon.devices.api.utils.RenderUtil;
import dev.ultreon.devices.block.entity.DeviceBlockEntity;
import dev.ultreon.devices.block.entity.RouterBlockEntity;
import dev.ultreon.devices.core.Device;
import dev.ultreon.devices.core.ComputerScreen;
import dev.ultreon.devices.object.TrayItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.apache.commons.lang3.EnumUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author MrCrayfish
 */
public class TrayItemWifi extends TrayItem {
    private final OperatingSystem system;
    private int pingTimer;
    private WifiStrength strength = WifiStrength.NONE;
    private ItemList<WiFiNetwork> wifiNetworks;
    private @Nullable CompletableFuture<List<WiFiNetwork>> scanFuture;

    public TrayItemWifi(OperatingSystem system) {
        super(Icons.WIFI_NONE, UltreonDevices.res("wifi"));
        this.system = system;
    }

    private Layout createWifiMenu(TrayItem item) {
        Layout layout = new Layout.Context(100, 100);
        layout.setBackground((graphics, mc, x, y, width, height, mouseX, mouseY, windowActive) -> graphics.fill(x, y, x + width, y + height, new Color(0.65f, 0.65f, 0.65f, 0.9f).getRGB()));

        @NotNull ItemList<WiFiNetwork> itemListRouters = createRouterList();
        layout.addComponent(itemListRouters);

        Button buttonConnect = new Button(79, 79, Icons.CHECK);
        buttonConnect.setClickListener((mouseX, mouseY, mouseButton) -> connect(item, mouseButton, itemListRouters));
        layout.addComponent(buttonConnect);

        return layout;
    }

    private void connect(TrayItem item, int mouseButton, @NotNull ItemList<WiFiNetwork> itemListRouters) {
        if (mouseButton == 0) {
            if (itemListRouters.getSelectedItem() != null) {
                NetworkManagerImpl network = system.getNetwork();
            }
        }
    }

    private @NotNull ItemList<WiFiNetwork> createRouterList() {
        @NotNull ItemList<WiFiNetwork> itemListRouters = getRouterList();
        itemListRouters.sortBy((o1, o2) -> {
            if (o1.strength() == null) return -1;
            if (o2.strength() == null) return 1;
            return o1.strength().compareTo(o2.strength());
        });
        return itemListRouters;
    }

    private @NotNull ItemList<WiFiNetwork> getRouterList() {
        wifiNetworks = new ItemList<>(5, 5, 90, 4);
        scan();
        wifiNetworks.setListItemRenderer(new ListItemRenderer<>(16) {
            @Override
            public void render(GuiGraphics graphics, WiFiNetwork device, Minecraft mc, int x, int y, int width, int height, boolean selected) {
                graphics.fill(x, y, x + width, y + height, selected ? Color.DARK_GRAY.getRGB() : Color.GRAY.getRGB());
                RenderUtil.drawStringClipped(graphics, device.ssid(), x + 16, y + 4, 70, Color.WHITE.getRGB(), false);

                if (device.strength() == null) return;

                BlockPos laptopPos = ComputerScreen.getPos();
                assert laptopPos != null;
                WifiStrength strength = device.strength();
                switch (strength) {
                    case LOW -> setIcon(Icons.WIFI_LOW);
                    case MED -> setIcon(Icons.WIFI_MED);
                    case HIGH -> setIcon(Icons.WIFI_HIGH);
                    default -> setIcon(Icons.WIFI_NONE);
                }
            }
        });
        return wifiNetworks;
    }

    private void scan() {
        if (ComputerScreen.isWorldLess()) {
            return;
        }

        if (scanFuture != null && !scanFuture.isDone()) {
            return;
        }
        scanFuture = system.getNetwork().scan().thenApply(networks -> {
            wifiNetworks.setItems(networks);
            return networks;
        });
    }

    @Override
    public void init() {
        setClickListener((mouseX, mouseY, mouseButton) -> {
            if (ComputerScreen.getSystem().hasContext()) {
                ComputerScreen.getSystem().closeContext();
            } else {
                ComputerScreen.getSystem().openContext(createWifiMenu(this), mouseX - 100, mouseY - 100);
            }
        });
    }

    @Override
    public void deserialize(OperatingSystem system, CompoundTag tag) {
        super.deserialize(system, tag);
        pingTimer = tag.getInt("pingTimer");
        strength = EnumUtils.getEnum(WifiStrength.class, tag.getString("strength"), WifiStrength.NONE);
        switch (strength) {
            case LOW -> setIcon(Icons.WIFI_LOW);
            case MED -> setIcon(Icons.WIFI_MED);
            case HIGH -> setIcon(Icons.WIFI_HIGH);
            case NONE -> setIcon(Icons.WIFI_NONE);
        }
    }

    @Override
    public CompoundTag serialize() {
        CompoundTag tag = super.serialize();
        tag.putInt("pingTimer", pingTimer);
        tag.putString("strength", strength.name());
        return tag;
    }

    @Override
    public void tick() {
        super.tick();

        WifiStrength strength1 = system.getNetwork().getStrength();
        switch (strength1) {
            case LOW -> setIcon(Icons.WIFI_LOW);
            case MED -> setIcon(Icons.WIFI_MED);
            case HIGH -> setIcon(Icons.WIFI_HIGH);
            case NONE -> setIcon(Icons.WIFI_NONE);
        }
    }
}
