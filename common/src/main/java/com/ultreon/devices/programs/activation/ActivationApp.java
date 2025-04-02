package com.ultreon.devices.programs.activation;

import com.ultreon.devices.api.app.Dialog;
import com.ultreon.devices.api.app.Icons;
import com.ultreon.devices.api.app.component.Button;
import com.ultreon.devices.api.app.component.Label;
import com.ultreon.devices.api.app.component.TextField;
import com.ultreon.devices.api.task.TaskManager;
import com.ultreon.devices.core.Laptop;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * Codename: Apr1l
 */
public class ActivationApp extends Dialog {
    private TextField part1;
    private TextField part2;
    private TextField part3;
    private TextField part4;
    private TextField part5;
    private Label sep1;
    private Label sep2;
    private Label sep3;
    private Label sep4;
    private Button registerBtn;

    public ActivationApp() {
        width = 10 + length(8) + 9 + length(4) + 9 + length(4) + 9 + length(4) + 9 + length(12) + 10;
        height = 68;
    }

    @Override
    public void init(@Nullable CompoundTag intent) {
        super.init(intent);

        defaultLayout.width = 10 + length(8) + 9 + length(4) + 9 + length(4) + 9 + length(4) + 9 + length(12) + 10;
        defaultLayout.height = 68;
        defaultLayout.setTitle("Activate");

        // 00000000-0000-0000-0000-000000000000
        Label description = new Label("Enter product key to activate:", 10, 10);
        int i = 10;
        part1 = new TextField(10, 25, length(8));
        part1.setPlaceholder("00000000");
        part2 = new TextField(10 + length(8) + i, 25, length(4));
        part2.setPlaceholder("0000");
        part3 = new TextField(10 + length(8) + i + length(4) + i, 25, length(4));
        part3.setPlaceholder("0000");
        part4 = new TextField(10 + length(8) + i + length(4) + i + length(4) + i, 25, length(4));
        part4.setPlaceholder("0000");
        part5 = new TextField(10 + length(8) + i + length(4) + i + length(4) + i + length(4) + i, 25, length(12));
        part5.setPlaceholder("000000000000");
        sep1 = new Label("-", 10 + length(8) + 2, 29);
        sep2 = new Label("-", 10 + length(8) + i + length(4) + 2, 29);
        sep3 = new Label("-", 10 + length(8) + i + length(4) + i + length(4) + 2, 29);
        sep4 = new Label("-", 10 + length(8) + i + length(4) + i + length(4) + i + length(4) + 2, 29);
        registerBtn = new Button(10 + length(8) + i + length(4) + i + length(4) + i + length(4) + i + length(12) - 70, 45, 70, 18, "Activate");
        registerBtn.setIcon(Icons.KEY);
        registerBtn.setClickListener((mouseX, mouseY, mouseButton) -> {
            UUID license = getLicense();
            if (license == null) {
                openDialog(new Dialog.Message("Invalid license."));
                return;
            }
            TaskActivateMineOS activateTask = new TaskActivateMineOS();
            activateTask.setCallback((nbt, success) -> {
                if (success) {
                    getWindow().close();
                } else {
                    openDialog(new Dialog.Message("Incorrect license."));
                }
            });
            TaskManager.sendTask(activateTask);
        });

        super.addComponent(description);
        super.addComponent(part1);
        super.addComponent(part2);
        super.addComponent(part3);
        super.addComponent(part4);
        super.addComponent(part5);
        super.addComponent(sep1);
        super.addComponent(sep2);
        super.addComponent(sep3);
        super.addComponent(sep4);
        super.addComponent(registerBtn);
    }

    @Override
    public void render(GuiGraphics graphics, Laptop laptop, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean active, float partialTicks) {        setLayout(defaultLayout);
        defaultLayout.width = width;
        defaultLayout.height = height;

        super.render(graphics, laptop, mc, x, y, mouseX, mouseY, active, partialTicks);
    }

    private UUID getLicense() {
        try {
            return UUID.fromString(part1 + "-" + part2 + "-" + part3 + "-" + part4 + "-" + part5);
        } catch (Exception e) {
            return null;
        }
    }

    private int length(int i) {
        return 6 * i - 1 + 10;
    }
}
