package dev.ultreon.devices.object.tiles;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.ultreon.devices.api.utils.RenderUtil;
import dev.ultreon.devices.object.Game;
import net.minecraft.client.gui.GuiGraphics;

public class TileEnchantmentTable extends Tile
{
	public TileEnchantmentTable(int id, int x, int y)
	{
		super(id, x, y);
	}

	@Override
	public void render(GuiGraphics graphics, Game game, int x, int y, Game.Layer layer)
	{
		if(game.getTile(layer.up(), x, y - 1) != this || layer == Game.Layer.FOREGROUND)
		{
			RenderUtil.drawRectWithTexture(null, graphics, game.xPosition + x * WIDTH, game.yPosition + y * HEIGHT - 4, layer.zLevel, this.topX * 16 + 16, this.topY * 16, WIDTH, HEIGHT, 16, 16);
		}

		RenderSystem.setShaderColor(0.6f, 0.6f, 0.6f, 1f);
		RenderUtil.drawRectWithTexture(null, graphics, game.xPosition + x * WIDTH, game.yPosition + y * HEIGHT + 2, layer.zLevel, this.x * 16, this.y * 16 + 4, WIDTH, 4, 16, 12);
		RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
	}

	@Override
	public boolean isFullTile()
	{
		return false;
	}
}
