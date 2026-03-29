package com.ultreon.devices.programs.gitweb.component.container;

import com.mojang.blaze3d.systems.RenderSystem;
import com.ultreon.devices.Reference;
import com.ultreon.devices.api.app.Component;
import com.ultreon.devices.api.utils.RenderUtil;
import com.ultreon.devices.core.Laptop;
import com.ultreon.devices.util.GuiHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author MrCrayfish
 */
public abstract class ContainerBox extends Component {
    public static final int WIDTH = 128;
    protected static final Identifier CONTAINER_BOXES_TEXTURE = Identifier.fromNamespaceAndPath(Reference.MOD_ID, "textures/gui/container_boxes.png");
    protected List<Slot> slots = new ArrayList<>();
    protected int boxU, boxV;
    protected int height;
    protected ItemStack icon;
    protected String title;

    public ContainerBox(int left, int top, int boxU, int boxV, int height, ItemStack icon, String title) {
        super(left, top);
        this.boxU = boxU;
        this.boxV = boxV;
        this.height = height;
        this.icon = icon;
        this.title = title;
    }

    @Override
    protected void render(GuiGraphicsExtractor graphics, Laptop laptop, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean windowActive, float partialTicks) {
        graphics.blit(RenderPipelines.GUI_TEXTURED, CONTAINER_BOXES_TEXTURE, x, y + 12, boxU, boxV, WIDTH, height, WIDTH, height, 256, 256);
        //Gui.blit(pose, x, y + 12, WIDTH, height, boxU, boxV, 256, 256, WIDTH, height);

        int contentOffset = (WIDTH - (Laptop.getFontStatic().width(title) + 8 + 4)) / 2;
        graphics.pose().pushMatrix();
        {
            graphics.pose().translate(x + contentOffset, y);
            graphics.pose().scale(0.5f, 0.5f);
            graphics.item(icon, x+contentOffset-5, y-4);
        }
        graphics.pose().popMatrix();

        RenderUtil.drawStringClipped(graphics, title, x + contentOffset + 8 + 4, y, 110, Color.WHITE.getRGB(), true);

        slots.forEach(slot -> slot.render(graphics, x, y + 12));
    }

    @Override
    protected void renderOverlay(GuiGraphicsExtractor graphics, Laptop laptop, Minecraft mc, int mouseX, int mouseY, boolean windowActive) {
        slots.forEach(slot -> slot.renderOverlay(graphics, laptop, xPosition, yPosition + 12, mouseX, mouseY));
    }

    protected static class Slot {
        private final int slotX;
        private final int slotY;
        private final ItemStack stack;

        public Slot(int slotX, int slotY, ItemStack stack) {
            this.slotX = slotX;
            this.slotY = slotY;
            this.stack = stack;
        }

        public void render(GuiGraphicsExtractor graphics, int x, int y) {
            RenderUtil.renderItem(graphics, x + slotX, y + slotY, stack, true);
        }

        public void renderOverlay(GuiGraphicsExtractor graphics, Laptop laptop, int x, int y, int mouseX, int mouseY) {
            if (GuiHelper.isMouseWithin(mouseX, mouseY, x + slotX, y + slotY, 16, 16)) {
                if (!stack.isEmpty()) {
                    graphics.setTooltipForNextFrame(Minecraft.getInstance().font, Screen.getTooltipFromItem(Minecraft.getInstance(), stack), Optional.empty(), mouseX, mouseY/*, stack*/);
                }
            }

            // Todo: fix this.
//            GlStateManager.disableRescaleNormal();
//            RenderHelper.disableStandardItemLighting();
//            GlStateManager.disableDepth();
        }

        public ItemStack getStack() {
            return stack;
        }
    }
}
