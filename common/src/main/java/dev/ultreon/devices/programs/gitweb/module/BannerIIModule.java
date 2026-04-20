package dev.ultreon.devices.programs.gitweb.module;

import dev.ultreon.devices.api.app.Component;
import dev.ultreon.devices.api.app.Layout;
import dev.ultreon.devices.core.Laptop;
import dev.ultreon.devices.debug.DebugLog;
import dev.ultreon.devices.programs.gitweb.component.GitWebFrame;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.object.banner.BannerFlagModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BannerPatternLayers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static dev.ultreon.devices.programs.gitweb.module.ContainerModule.getItem;

public class BannerIIModule extends Module {
    @Override
    public String[] getRequiredData() {
        return new String[]{"banner"};
    }

    @Override
    public String[] getOptionalData() {
        return new String[]{"waving"};
    }

    @Override
    public int calculateHeight(Map<String, String> data, int width) {
        return LoomBox.HEIGHT;
    }

    @Override
    public void generate(GitWebFrame frame, Layout layout, int width, Map<String, String> data) {
        layout.addComponent(createContainer(data));
    }

    public LoomBox createContainer(Map<String, String> data) {
        return new LoomBox(getItem(data, "banner"), Boolean.parseBoolean(data.get("waving")));
    }

    public static class LoomBox extends Component {
        public static final int HEIGHT = 84;
        private final ItemStack banner;
        private final BannerFlagModel flag;
        private final BannerPatternLayers resultBannerPatterns;

        public LoomBox(ItemStack banner, boolean waving) {
            super(0, 0);
            this.banner = banner;

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
            int j = y;//this.topPos;
            if (banner.isEmpty()) return;
            MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();
            graphics.pose().pushMatrix();
            //pose.translate((double)(i + 139), (double)(j + 52), 0.0D);
            graphics.pose().translate(i + 139, j + 90);
            graphics.pose().scale(48.0F, -48.0F);
            //    pose.scale(24.0F, -24.0F, 1.0F);
            graphics.pose().translate(0.5F, 0.5F);
            float f = 0.6666667F;
            graphics.pose().scale(f, -f);
            long l = System.currentTimeMillis() / 50;
            DebugLog.log(l);
            float h = ((float) Math.floorMod(l, 100L) + partialTicks) / 100.0f;

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
}
// 128, 72