package com.ultreon.devices.programs;

import com.ultreon.devices.api.app.Application;
import com.ultreon.devices.api.app.Dialog;
import com.ultreon.devices.api.app.Icons;
import com.ultreon.devices.api.app.component.Button;
import net.minecraft.nbt.CompoundTag;

import org.jetbrains.annotations.Nullable;

/**
 * @author MrCrayfish
 */
public class TestApp extends Application {
    @Override
    public void init(@Nullable CompoundTag intent) {
        Button button = new Button(5, 5, Icons.PRINTER);
        button.setClickListener((mouseX, mouseY, mouseButton) -> {
            if (mouseButton == 0) {
                Dialog.Confirmation dialog = new Dialog.Confirmation("Test");
                dialog.setPositiveText("Override");
                openDialog(dialog);
            }
        });
        super.addComponent(button);

        Button button1 = new Button(30, 5, Icons.PRINTER);
        button1.setClickListener((mouseX, mouseY, mouseButton) -> {
            if (mouseButton == 0) {
                openDialog(new Dialog.Message("Test"));
            }
        });
        super.addComponent(button1);

        Button button2 = new Button(55, 5, Icons.PRINTER);
        button2.setClickListener((mouseX, mouseY, mouseButton) -> {
            if (mouseButton == 0) {
                openDialog(new Dialog.Input("Test"));
            }
        });
        super.addComponent(button2);

        Button button3 = new Button(80, 5, Icons.PRINTER);
        button3.setClickListener((mouseX, mouseY, mouseButton) -> {
            if (mouseButton == 0) {
                openDialog(new Dialog.OpenFile(this));
            }
        });
        super.addComponent(button3);

        Button button4 = new Button(105, 5, Icons.PRINTER);
        button4.setClickListener((mouseX, mouseY, mouseButton) -> {
            if (mouseButton == 0) {
                openDialog(new Dialog.SaveFile(this, new CompoundTag()));
            }
        });
        super.addComponent(button4);
    }

    @Override
    public void load(CompoundTag tagCompound) {

    }

    @Override
    public void save(CompoundTag tagCompound) {

    }
}
