package com.ultreon.devices.api.app.component;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.ultreon.devices.api.app.Component;
import com.ultreon.devices.api.app.listener.ClickListener;
import com.ultreon.devices.api.task.Task;
import com.ultreon.devices.api.utils.RenderUtil;
import com.ultreon.devices.core.Laptop;
import com.ultreon.devices.util.GuiHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.awt.*;

/**
 * A component that allows you "access" to the players inventory. Now why access
 * is in quotes is because it's client side only. If you want to process anything,
 * you'll have to sendTask the selected item slot to the server and process it there.
 * You can use a {@link Task} to perform this.
 *
 * @author MrCrayfish
 */
public class Inventory extends Component {
    protected static final ResourceLocation CHEST_GUI_TEXTURE = new ResourceLocation("textures/gui/container/generic_54.png");

    protected int selectedColor = new Color(1f, 1f, 0f, 0.15f).getRGB();
    protected int hoverColor = new Color(1f, 1f, 1f, 0.15f).getRGB();

    protected int selected = -1;

    protected ClickListener clickListener = null;

    public Inventory(int left, int top) {
        super(left, top);
    }

    @Override
    public void render(GuiGraphics graphics, Laptop laptop, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean windowActive, float partialTicks) {
        if (this.visible) {
            RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
            RenderSystem.setShaderTexture(0, CHEST_GUI_TEXTURE);
            RenderUtil.drawRectWithTexture(CHEST_GUI_TEXTURE, graphics, xPosition, yPosition, 7, 139, 162, 54, 162, 54);

            assert mc.player != null;
            net.minecraft.world.entity.player.Inventory inventory = mc.player.getInventory();
            for (int i = 9; i < inventory.getContainerSize() - 4; i++) {
                int offsetX = (i % 9) * 18;
                int offsetY = (i / 9) * 18 - 18;

                if (selected == i) {
                    graphics.fill(xPosition + offsetX, yPosition + offsetY, xPosition + offsetX + 18, yPosition + offsetY + 18, selectedColor);
                }

                if (GuiHelper.isMouseInside(mouseX, mouseY, xPosition + offsetX, yPosition + offsetY, xPosition + offsetX + 17, yPosition + offsetY + 17)) {
                    graphics.fill(xPosition + offsetX, yPosition + offsetY, xPosition + offsetX + 18, yPosition + offsetY + 18, hoverColor);
                }

                ItemStack stack = inventory.getItem(i);
                if (!stack.isEmpty()) {
                    RenderUtil.renderItem(graphics, xPosition + offsetX + 1, yPosition + offsetY + 1, stack, true);
                }
            }
        }
    }

    @Override
    public void renderOverlay(GuiGraphics graphics, Laptop laptop, Minecraft mc, int mouseX, int mouseY, boolean windowActive) {
        if (this.visible) {
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 9; j++) {
                    int x = xPosition + (j * 18) - 1;
                    int y = yPosition + (i * 18) - 1;
                    if (GuiHelper.isMouseInside(mouseX, mouseY, x, y, x + 18, y + 18)) {
                        ItemStack stack = mc.player.getInventory().getItem((i * 9) + j + 9);
                        if (!stack.isEmpty()) {
                            graphics.renderTooltip(mc.font, stack, mouseX, mouseY);
                        }
                        return;
                    }
                }
            }
        }
    }

    @Override
    public void handleMouseClick(int mouseX, int mouseY, int mouseButton) {
        if (!this.visible || !this.enabled)
            return;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                int x = xPosition + (j * 18) - 1;
                int y = yPosition + (i * 18) - 1;
                if (GuiHelper.isMouseInside(mouseX, mouseY, x, y, x + 18, y + 18)) {
                    this.selected = (i * 9) + j + 9;
                    if (clickListener != null) {
                        clickListener.onClick(mouseX, mouseY, mouseButton);
                    }
                    return;
                }
            }
        }
    }

    /**
     * Gets the selected slot index
     *
     * @return the slot index
     */
    public int getSelectedSlotIndex() {
        return selected;
    }

    /**
     * Sets the click listener for when an item is clicked
     *
     * @param clickListener the click listener
     */
    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    /**
     * Sets the color displayed when an item is selected
     *
     * @param selectedColor the selected color
     */
    public void setSelectedColor(int selectedColor) {
        this.selectedColor = selectedColor;
    }

    /**
     * Sets the color displayed when a mouse is hovering an item
     *
     * @param hoverColor the hover color
     */
    public void setHoverColor(int hoverColor) {
        this.hoverColor = hoverColor;
    }
}
