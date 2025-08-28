package com.ultreon.devices.programs.gitweb.component.container;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.ultreon.devices.core.Laptop;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/**
 * @author MrCrayfish
 */
public class BrewingBox extends ContainerBox {
    public static final int HEIGHT = 73;
    //Copied from GuiBrewingStand. Why do they store the length in an array?
    private static final int[] BUBBLELENGTHS = new int[]{28, 24, 20, 16, 11, 6, 0}; // i kinda screwed the container texture file =|
    private int brewTimer;

    public BrewingBox(ItemStack fuel, ItemStack input, ItemStack[] output) {
        super(0, 0, 0, 136, 73, new ItemStack(Items.BREWING_STAND), "Brewing Stand");
        slots.add(new Slot(14, 8, fuel));
        slots.add(new Slot(75, 8, input));
        this.setOutput(output);
    }

    private void setOutput(ItemStack[] output) {
        if (output.length > 0) {
            slots.add(new Slot(52, 42, output[0]));
        }
        if (output.length > 1) {
            slots.add(new Slot(75, 49, output[1]));
        }
        if (output.length > 2) {
            slots.add(new Slot(98, 42, output[1]));
        }
    }

    @Override
    protected void handleTick() {
        if (--brewTimer < 0) {
            brewTimer = 400;
        }
    }

    @Override
    protected void render(GuiGraphics graphics, Laptop laptop, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean windowActive, float partialTicks) {
        super.render(graphics, laptop, mc, x, y, mouseX, mouseY, windowActive, partialTicks);

        RenderSystem.setShaderTexture(0, CONTAINER_BOXES_TEXTURE);

        graphics.blit(CONTAINER_BOXES_TEXTURE, x + 56, y + 47, 152, 252, 18, 4);

        if (brewTimer > 0) {
            if (slots.size() > 1 && !slots.get(1).getStack().isEmpty()) {
                int scaledPercent = (int) (28f * (1f - (float) brewTimer / 400f));

                if (scaledPercent > 0) {
                    graphics.blit(CONTAINER_BOXES_TEXTURE, x + 93, y + 19, 152, 223, 9, scaledPercent);
                }

                scaledPercent = BUBBLELENGTHS[brewTimer / 2 % 7];

                if (scaledPercent > 0) {
                    graphics.blit(CONTAINER_BOXES_TEXTURE, x + 59, y + 16 + 29 - scaledPercent, 161, 251 - scaledPercent, 12, scaledPercent);
                }
            }
        }
    }
}
