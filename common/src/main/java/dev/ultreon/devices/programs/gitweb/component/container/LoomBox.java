package dev.ultreon.devices.programs.gitweb.component.container;

import com.mojang.blaze3d.platform.Lighting;
import dev.ultreon.devices.core.ComputerScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BannerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BannerPatternLayers;

import java.util.ArrayList;

public class LoomBox extends ContainerBox {
    public static final int HEIGHT = 84;
    private final ItemStack result;
    private final BannerPatternLayers resultBannerPatterns;
    private final ModelPart flag;

    public LoomBox(ItemStack banner, ItemStack dye, ItemStack pattern, ItemStack result) {
        super(0, 0, 128, 72, HEIGHT, new ItemStack(Blocks.LOOM), "Loom");
        this.result = result;
        slots.add(new Slot(13, 26, banner));
        slots.add(new Slot(33, 26, dye));
        slots.add(new Slot(23, 45, pattern));
        slots.add(new Slot(94, 58, this.result));

        if (!result.isEmpty()) {
            this.resultBannerPatterns = result.get(DataComponents.BANNER_PATTERNS);
        } else
            this.resultBannerPatterns = new BannerPatternLayers(new ArrayList<>());
        this.flag = Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.BANNER).getChild("flag");
    }

    @Override
    protected void render(GuiGraphics graphics, ComputerScreen computerScreen, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean windowActive, float partialTicks) {
        super.render(graphics, computerScreen, mc, x, y, mouseX, mouseY, windowActive, partialTicks);
        int j = y + 12;
        if (result.isEmpty()) return;
        Lighting.setupForFlatItems();
        MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();
        graphics.pose().pushPose();
        //pose.translate((double)(i + 139), (double)(j + 52), 0.0D);
        graphics.pose().translate(x + 90d, j + 52d, 0.0D);
        graphics.pose().scale(24.0F, -24.0F, 1.0F);
        graphics.pose().translate(0.5D, 0.5D, 0.5D);
        float f = 0.6666667F;
        graphics.pose().scale(0.6666667F, -0.6666667F, -0.6666667F);
        this.flag.xRot = 0.0F;
        this.flag.y = -32.0F;
        BannerRenderer.renderPatterns(graphics.pose(), bufferSource, 15728880, OverlayTexture.NO_OVERLAY, this.flag, ModelBakery.BANNER_BASE, true, DyeColor.WHITE, this.resultBannerPatterns);
        graphics.pose().popPose();
        bufferSource.endBatch();


    }
}
//128x84