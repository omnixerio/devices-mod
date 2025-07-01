package dev.ultreon.devices.object.tiles;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.ultreon.devices.api.utils.RenderUtil;
import dev.ultreon.devices.object.Game;
import net.minecraft.client.gui.GuiGraphics;

public class TileCactus extends Tile
{
	public TileCactus(int id)
	{
		super(id, 3, 2);
	}

	@Override
	public void render(GuiGraphics graphics, Game game, int x, int y, Game.Layer layer)
	{
		if(game.getTile(layer.up(), x, y - 1) != this || layer == Game.Layer.FOREGROUND)
		{
			RenderUtil.drawRectWithTexture(null, graphics,game.xPosition + x * WIDTH, game.yPosition + y * HEIGHT - 5.5, layer.zLevel, this.x * 16, this.y * 16, WIDTH, HEIGHT, 16, 16);
			RenderUtil.drawRectWithTexture(null, graphics, game.xPosition + x * WIDTH + 0.5, game.yPosition + y * HEIGHT - 5.5, layer.zLevel, (this.x + 1) * 16 + 1, this.y * 16 + 1, WIDTH - 1, HEIGHT - 1, 14, 14);
		}

		RenderSystem.setShaderColor(0.6f, 0.6f, 0.6f, 1f);
		RenderUtil.drawRectWithTexture(null, graphics, game.xPosition + x * WIDTH, game.yPosition + y * HEIGHT - 0.5, layer.zLevel, this.x * 16, this.y * 16, WIDTH, HEIGHT, 16, 16);
		RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
	}

	@Override
	public boolean isFullTile()
	{
		return false;
	}
}
