package com.ultreon.devices.programs.gitweb.component.container;

import com.ultreon.devices.core.Laptop;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.object.banner.BannerFlagModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.block.entity.BannerPatternLayers;

import java.util.ArrayList;
import java.util.List;

public class LoomBox extends ContainerBox {
    public static final int HEIGHT = 84;
    private final ItemStack banner;
    private final ItemStack dye;
    private final ItemStack pattern;
    private final ItemStack result;
    private final BannerPatternLayers resultBannerPatterns;
    private final BannerFlagModel flag;

    public LoomBox(ItemStack banner, ItemStack dye, ItemStack pattern, ItemStack result) {
        super(0, 0, 128, 72, HEIGHT, new ItemStack(Blocks.LOOM), "Loom");
        this.banner = banner;
        this.dye = dye;
        this.pattern = pattern;
        this.result = result;
        slots.add(new Slot(13, 26, this.banner));
        slots.add(new Slot(33, 26, this.dye));
        slots.add(new Slot(23, 45, this.pattern));
        slots.add(new Slot(94, 58, this.result));

        if (banner.has(DataComponents.BANNER_PATTERNS))
            this.resultBannerPatterns = banner.get(DataComponents.BANNER_PATTERNS);
        else
            this.resultBannerPatterns = new BannerPatternLayers(new ArrayList<>());

        ModelPart modelPart = Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.STANDING_BANNER_FLAG);
        this.flag = new BannerFlagModel(modelPart);
    }

    @Override
    protected void render(GuiGraphicsExtractor graphics, Laptop laptop, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean windowActive, float partialTicks) {
        super.render(graphics, laptop, mc, x, y, mouseX, mouseY, windowActive, partialTicks);
        int i = x;//this.leftPos;
        int j = y + 12;//this.topPos;
        if (result.isEmpty()) return;
        MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();
        graphics.pose().pushMatrix();
        //pose.translate((double)(i + 139), (double)(j + 52), 0.0D);
        graphics.pose().translate(i + 90, j + 52);
        graphics.pose().scale(24.0F, -24.0F);
        graphics.pose().translate(0.5F, 0.5F);
        float f = 0.6666667F;
        graphics.pose().scale(0.6666667F, -0.6666667F);

        List<BannerPatternLayers.Layer> layers = resultBannerPatterns.layers();
        if (!layers.isEmpty()) {
            Item item = banner.getItem();
            if (item instanceof BannerItem bannerItem) {
                int xo = this.xPosition;
                int yo = this.yPosition;
                int x0 = xo + 141;
                int y0 = yo + 8;
                graphics.bannerPattern(flag, bannerItem.getColor(), resultBannerPatterns, x0, y0, x0 + 20, y0 + 40);
            } else {
                graphics.item(banner, 141, 8);
            }
        }

        graphics.pose().popMatrix();
        bufferSource.endBatch();


    }
}
//128x84