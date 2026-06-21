package dev.ultreon.devices.programs.snake;

import dev.ultreon.devices.api.app.Application;
import dev.ultreon.devices.api.app.Layout;
import dev.ultreon.devices.api.app.component.Button;
import dev.ultreon.devices.api.app.component.Label;
import dev.ultreon.devices.core.Laptop;
import dev.ultreon.devices.programs.snake.layout.SnakeLayout;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;

public class SnakeApp extends Application {
    public Layout titleScreen;
    public SnakeLayout gameLayout;
    @Override
    public void init(@Nullable CompoundTag intent) {
        this.titleScreen = new Layout(200, 100);
        var startButton = new Button(30, 70, "Start");
        startButton.setClickListener(((mouseX, mouseY, mouseButton) -> {
            this.gameLayout = new SnakeLayout(this);
            this.setCurrentLayout(this.gameLayout);
        }));
        var titleText = new Label("Snake", 10, 10);
        titleText.setScale(2);

        titleScreen.addComponent(titleText);
        titleScreen.addComponent(startButton);
        setCurrentLayout(titleScreen);
    }

    @Override
    public void render(GuiGraphics graphics, Laptop laptop, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean active, float partialTicks) {
        super.render(graphics, laptop, mc, x, y, mouseX, mouseY, active, partialTicks);
//        for (Component component : this.titleScreen.components) {
//            DebugLog.log(component + "lt: " + component.left + ", " + component.top);
//        }
    }

    @Override
    public void load(CompoundTag tag) {

    }

    @Override
    public void save(CompoundTag tag) {

    }
}
