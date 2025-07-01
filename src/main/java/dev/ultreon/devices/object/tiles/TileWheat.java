package dev.ultreon.devices.object.tiles;


import com.mojang.blaze3d.vertex.PoseStack;
import dev.ultreon.devices.api.utils.RenderUtil;
import dev.ultreon.devices.object.Game;
import net.minecraft.client.gui.GuiGraphics;

public class TileWheat extends Tile
{
	public TileWheat(int id, int x, int y)
	{
		super(id, x, y);
	}

	@Override
	public void render(GuiGraphics graphics, Game game, int x, int y, Game.Layer layer)
	{
		RenderUtil.drawRectWithTexture(null, graphics, game.xPosition + x * Tile.WIDTH, game.yPosition + y * Tile.HEIGHT - 6, this.x * 16, this.y * 16, WIDTH, HEIGHT + 1, 16, 16);
		RenderUtil.drawRectWithTexture(null, graphics, game.xPosition + x * Tile.WIDTH, game.yPosition + y * Tile.HEIGHT - 2, this.x * 16, this.y * 16, WIDTH, HEIGHT + 1, 16, 16);
	}

	@Override
	public boolean isFullTile()
	{
		return false;
	}
}
