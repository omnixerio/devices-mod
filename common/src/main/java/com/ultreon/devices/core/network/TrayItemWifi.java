package com.ultreon.devices.core.network;

import com.mojang.blaze3d.vertex.PoseStack;
import com.ultreon.devices.DeviceConfig;
import com.ultreon.devices.Devices;
import com.ultreon.devices.api.app.Icons;
import com.ultreon.devices.api.app.Layout;
import com.ultreon.devices.api.app.component.Button;
import com.ultreon.devices.api.app.component.ItemList;
import com.ultreon.devices.api.app.renderer.ListItemRenderer;
import com.ultreon.devices.api.task.Task;
import com.ultreon.devices.api.task.TaskManager;
import com.ultreon.devices.api.utils.RenderUtil;
import com.ultreon.devices.block.entity.DeviceBlockEntity;
import com.ultreon.devices.block.entity.RouterBlockEntity;
import com.ultreon.devices.core.Device;
import com.ultreon.devices.core.Laptop;
import com.ultreon.devices.core.network.task.TaskConnect;
import com.ultreon.devices.core.network.task.TaskGetRouters;
import com.ultreon.devices.core.network.task.TaskPing;
import com.ultreon.devices.object.TrayItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.apache.commons.lang3.EnumUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author MrCrayfish
 */
public class TrayItemWifi extends TrayItem {
    private int pingTimer;
    private Strength strength = Strength.NONE;

    public TrayItemWifi() {
        super(Icons.WIFI_NONE, Devices.id("wifi"));
    }

    private static Layout createWifiMenu(TrayItem item) {
        Layout layout = new Layout.Context(100, 100);
        layout.setBackground((graphics, mc, x, y, width, height, mouseX, mouseY, windowActive) -> graphics.fill(x, y, x + width, y + height, new Color(0.65f, 0.65f, 0.65f, 0.9f).getRGB()));

        ItemList<Device> itemListRouters = new ItemList<>(5, 5, 90, 4);
        itemListRouters.setItems(getRouters());
        itemListRouters.setListItemRenderer(new ListItemRenderer<>(16) {
            @Override
            public void render(GuiGraphics graphics, Device device, Minecraft mc, int x, int y, int width, int height, boolean selected) {
                graphics.fill(x, y, x + width, y + height, selected ? Color.DARK_GRAY.getRGB() : Color.GRAY.getRGB());
                RenderUtil.drawStringClipped(graphics, device.getName(), x + 16, y + 4, 70, Color.WHITE.getRGB(), false);

                if (device.getPos() == null) return;

                BlockPos laptopPos = Laptop.getPos();
                assert laptopPos != null;
                double distance = Math.sqrt(device.getPos().distToCenterSqr(laptopPos.getX() + 0.5, laptopPos.getY() + 0.5, laptopPos.getZ() + 0.5));
                if (distance > 20) {
                    Icons.WIFI_LOW.draw(graphics, mc, x + 3, y + 3);
                } else if (distance > 10) {
                    Icons.WIFI_MED.draw(graphics, mc, x + 3, y + 3);
                } else {
                    Icons.WIFI_HIGH.draw(graphics, mc, x + 3, y + 3);
                }
            }
        });
        itemListRouters.sortBy((o1, o2) -> {
            BlockPos laptopPos = Laptop.getPos();
            assert o1.getPos() != null;
            assert laptopPos != null;
            double distance1 = Math.sqrt(o1.getPos().distToCenterSqr(laptopPos.getX() + 0.5, laptopPos.getY() + 0.5, laptopPos.getZ() + 0.5));
            assert o2.getPos() != null;
            double distance2 = Math.sqrt(o2.getPos().distToCenterSqr(laptopPos.getX() + 0.5, laptopPos.getY() + 0.5, laptopPos.getZ() + 0.5));
            return Double.compare(distance1, distance2);
        });
        layout.addComponent(itemListRouters);

        Button buttonConnect = new Button(79, 79, Icons.CHECK);
        buttonConnect.setClickListener((mouseX, mouseY, mouseButton) -> {
            if (mouseButton == 0) {
                if (itemListRouters.getSelectedItem() != null) {
                    TaskConnect connect = new TaskConnect(Laptop.getPos(), itemListRouters.getSelectedItem().getPos());
                    connect.setCallback((tag, success) -> {
                        if (success) {
                            item.setIcon(Icons.WIFI_HIGH);
                            Laptop.getSystem().closeContext();
                        }
                    });
                    TaskManager.sendTask(connect);
                }
            }
        });
        layout.addComponent(buttonConnect);

        return layout;
    }

    private static List<Device> getRouters() {
        Task task = new TaskGetRouters(Laptop.getPos());
        CompoundTag[] result = new CompoundTag[1];
        boolean[] complete = new boolean[]{false};
        task.setCallback((compoundTag, success) -> {
            result[0] = compoundTag;
            complete[0] = true;
        });
        TaskManager.sendTask(task);
        while (true) {
            synchronized (complete) {
                if (complete[0]) break;
            }
        }
        ListTag routersList = result[0].getList("routers", 0);
        ArrayList<Device> routers = new ArrayList<>();
        for (Tag tag : routersList) {
            routers.add(Device.fromTag((CompoundTag) tag));
        }
        return routers;
    }

    @Override
    public void init() {
        this.setClickListener((mouseX, mouseY, mouseButton) -> {
            if (Laptop.getSystem().hasContext()) {
                Laptop.getSystem().closeContext();
            } else {
                Laptop.getSystem().openContext(createWifiMenu(this), mouseX - 100, mouseY - 100);
            }
        });

        runPingTask();
    }

    @Override
    public void tick() {
        if (++pingTimer >= DeviceConfig.PING_RATE.get()) {
            runPingTask();
            pingTimer = 0;
        }
    }

    private void runPingTask() {
        TaskPing task = new TaskPing(Laptop.getPos());
        task.setCallback((tag, success) -> {
            if (success) {
                assert tag != null;
                int strength = tag.getInt("strength");
                switch (strength) {
                    case 2 -> {
                        this.strength = Strength.LOW;
                        setIcon(Icons.WIFI_LOW);
                    }
                    case 1 -> {
                        this.strength = Strength.MED;
                        setIcon(Icons.WIFI_MED);
                    }
                    case 0 -> {
                        this.strength = Strength.HIGH;
                        setIcon(Icons.WIFI_HIGH);
                    }
                    default -> {
                        this.strength = Strength.NONE;
                        setIcon(Icons.WIFI_NONE);
                    }
                }
            } else {
                setIcon(Icons.WIFI_NONE);
            }
        });
        TaskManager.sendTask(task);
    }

    @Override
    public void deserialize(CompoundTag tag) {
        super.deserialize(tag);
        this.pingTimer = tag.getInt("pingTimer");
        this.strength = EnumUtils.getEnum(Strength.class, tag.getString("strength"), Strength.NONE);
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

    public enum Strength {
        LOW, MED, HIGH, NONE
    }
}
