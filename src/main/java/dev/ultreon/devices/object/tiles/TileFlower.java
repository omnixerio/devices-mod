package dev.ultreon.devices.object.tiles;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.ultreon.devices.api.utils.RenderUtil;
import dev.ultreon.devices.object.Game;
import net.minecraft.client.gui.GuiGraphics;

public class TileFlower extends Tile
{
	public TileFlower(int id, int x, int y)
	{
		super(id, x, y);
	}

	@Override
	public void render(GuiGraphics graphics, Game game, int x, int y, Game.Layer layer)
	{
		RenderUtil.drawRectWithTexture(null, graphics, game.xPosition + x * WIDTH, game.yPosition + y * HEIGHT - 4, this.x * 16, this.y * 16, WIDTH, 8, 16, 16);
	}

	@Override
	public boolean isFullTile()
	{
		return false;
	}
}
