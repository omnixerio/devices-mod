package dev.ultreon.devices.object.tiles;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.ultreon.devices.api.utils.RenderUtil;
import dev.ultreon.devices.object.Game;
import net.minecraft.client.gui.GuiGraphics;

public class TileBlock extends Tile
{
	public TileBlock(int id, int x, int y)
	{
		super(id, x, y);
	}

	public TileBlock(int id, int x, int y, int topX, int topY)
	{
		super(id, x, y, topX, topY);
	}

	@Override
	public void render(GuiGraphics graphics, Game game, int x, int y, Game.Layer layer)
	{
		if(layer == Game.Layer.BACKGROUND)
		{
			super.render(graphics, game, x, y, layer);
			if(!game.isFullTile(layer, x, y + 1) && this != Tile.water)
			{
				RenderSystem.setShaderColor(0.6f, 0.6f, 0.6f, 1f);
				RenderUtil.drawRectWithTexture(null, graphics, game.xPosition + x * Tile.WIDTH, game.yPosition + y * Tile.HEIGHT + 6, layer.zLevel, this.x * 16, this.y * 16, WIDTH, 2, 16, 4);
				RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
			}
			return;
		}

		if(game.getTile(layer.up(), x, y - 1) != this || layer == Game.Layer.FOREGROUND)
		{
			RenderUtil.drawRectWithTexture(null, graphics, game.xPosition + x * Tile.WIDTH, game.yPosition + y * Tile.HEIGHT - 6, layer.zLevel, this.topX * 16, this.topY * 16, WIDTH, HEIGHT, 16, 16);
		}

		RenderSystem.setShaderColor(0.6f, 0.6f, 0.6f, 1f);
		RenderUtil.drawRectWithTexture(null, graphics, game.xPosition + x * Tile.WIDTH, game.yPosition + y * Tile.HEIGHT, layer.zLevel, this.x * 16, this.y * 16, WIDTH, 6, 16, 16);
		RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
	}

	@Override
	public void renderForeground(GuiGraphics graphics, Game game, int x, int y, Game.Layer layer)
	{
		if(layer != Game.Layer.BACKGROUND || this == Tile.water)
			return;

		Tile tileDown = game.getTile(layer, x, y + 1);
		if(game.getTile(layer, x, y + 1) == Tile.water)
		{
			RenderSystem.setShaderColor(0.6f, 0.6f, 0.6f, 1f);
			RenderUtil.drawRectWithTexture(null, graphics, game.xPosition + x * Tile.WIDTH, game.yPosition + y * Tile.HEIGHT + 6, layer.zLevel, this.x * 16, this.y * 16, WIDTH, 1, 16, 2);
			RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
		}
	}
}
