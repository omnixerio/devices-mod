package dev.ultreon.devices.programs.system.layout;

import dev.ultreon.devices.api.app.Application;
import dev.ultreon.devices.api.app.IIcon;
import dev.ultreon.devices.api.app.Icons;
import dev.ultreon.devices.api.app.Layout;
import dev.ultreon.devices.api.app.component.Button;
import dev.ultreon.devices.core.ComputerScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

/**
 * @author MrCrayfish
 */
public class StandardLayout extends Layout {
    protected Application app;
    private final String title;
    private final Layout previous;
    private IIcon icon;

    public StandardLayout(String title, int width, int height, Application app, @Nullable Layout previous) {
        super(width, height);
        this.title = title;
        this.app = app;
        this.previous = previous;
    }

    @Override
    public void init() {
        if (previous != null) {
            Button btnBack = new Button(2, 2, Icons.ARROW_LEFT);
            btnBack.setClickListener((mouseX, mouseY, mouseButton) ->
            {
                if (mouseButton == 0) {
                    app.setCurrentLayout(previous);
                }
            });
            this.addComponent(btnBack);
        }
    }

    @Override
    public void render(GuiGraphics graphics, ComputerScreen computerScreen, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean windowActive, float partialTicks) {
        Color color = new Color(ComputerScreen.getSystem().getSettings().getColorScheme().getHeaderColor());
        graphics.fill(x, y, x + width, y + 20, color.getRGB());
        graphics.fill(x, y + 20, x + width, y + 21, color.darker().getRGB());

        if (previous == null && icon != null) {
            icon.draw(graphics, mc, x + 5, y + 5);
        }

        if (title != null) {
            graphics.drawString(mc.font, title, x + 5 + (previous != null || icon != null ? 16 : 0), y + 7, Color.WHITE.getRGB());
        }

        super.render(graphics, computerScreen, mc, x, y, mouseX, mouseY, windowActive, partialTicks);
    }

    public void setIcon(IIcon icon) {
        this.icon = icon;
    }
}
