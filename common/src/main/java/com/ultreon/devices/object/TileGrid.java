package com.ultreon.devices.object;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.ultreon.devices.api.app.Component;
import com.ultreon.devices.api.app.Icons;
import com.ultreon.devices.api.app.Layout;
import com.ultreon.devices.api.app.component.Button;
import com.ultreon.devices.api.app.component.Label;
import com.ultreon.devices.api.app.listener.ClickListener;
import com.ultreon.devices.api.utils.RenderUtil;
import com.ultreon.devices.core.Laptop;
import com.ultreon.devices.object.tiles.Tile;
import com.ultreon.devices.util.GuiHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class TileGrid extends Component
{
	private Label labelCurrentCategory;
	private Button btnNextCategory;
	private Button btnPrevCategory;
	
	private int currentCategory;
	private List<Tile> tabTiles;
	private Game game;
	
	public TileGrid(int left, int top, Game game)
	{
		super(left, top);
		this.currentCategory = 0;
		this.tabTiles = new ArrayList<Tile>();
		this.game = game;
	}
	
	@Override
	public void init(Layout layout)
	{
		labelCurrentCategory = new Label("", left + 14, top + 2);
		layout.addComponent(labelCurrentCategory);
		
		btnNextCategory = new Button(left + 81, top, Icons.CHEVRON_RIGHT);
		btnNextCategory.setPadding(1);
		btnNextCategory.setClickListener(new ClickListener()
		{
			@Override
			public void onClick(int mouseX, int mouseY, int mouseButton)
			{
				if(currentCategory < Tile.Category.values().length - 1)
				{
					currentCategory++;
					updateTiles();
				}
			}
		});
		layout.addComponent(btnNextCategory);
		
		btnPrevCategory = new Button(left, top, Icons.CHEVRON_LEFT);
		btnPrevCategory.setPadding(1);
		btnPrevCategory.setClickListener(new ClickListener()
		{
			@Override
			public void onClick(int mouseX, int mouseY, int mouseButton)
			{
				if(currentCategory > 0)
				{
					currentCategory--;
					updateTiles();
				}
			}
		});
		layout.addComponent(btnPrevCategory);
		
		updateTiles();
	}

	@Override
	public void render(GuiGraphics graphics, Laptop laptop, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean windowActive, float partialTicks)
	{
		try {
			Tesselator.getInstance().getBuilder().end();
		} catch (IllegalStateException e) {
			// ignore it
		}
		graphics.fill(xPosition, yPosition + 15, xPosition + 93, yPosition + 100, Color.DARK_GRAY.getRGB());
		graphics.fill(xPosition + 1, yPosition + 16, xPosition + 92, yPosition + 99, Color.GRAY.getRGB());


		RenderSystem.setShaderTexture(0, Game.ICONS);
		for(int i = 0; i < tabTiles.size(); i++)
		{
			Tile tile = tabTiles.get(i);
			int tileX = i % 6 * 15 + xPosition + 3;
			int tileY = i / 6 * 15 + yPosition + 18;
			if(GuiHelper.isMouseInside(mouseX, mouseY, tileX - 1, tileY - 1, tileX + 12, tileY + 12) || game.getCurrentTile() == tile)
				graphics.fill(tileX - 1, tileY - 1, tileX + 13, tileY + 13, Color.WHITE.getRGB());
			else
				graphics.fill(tileX - 1, tileY - 1, tileX + 13, tileY + 13, Color.LIGHT_GRAY.getRGB());
			graphics.pose().pushPose();
			RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
			RenderUtil.drawRectWithTexture(null, graphics, tileX, tileY, tile.x * 16, tile.y * 16, 12, 12, 16, 16);
			graphics.pose().popPose();
		}

		if(GuiHelper.isMouseInside(mouseX, mouseX, xPosition, yPosition, xPosition + 60, yPosition + 60))
		{
			for(int i = 0; i < tabTiles.size(); i++)
			{
				int tileX = i % 6 * 15 + xPosition + 2;
				int tileY = i / 6 * 15 + yPosition + 17;
				if(GuiHelper.isMouseInside(mouseX, mouseY, tileX, tileY, tileX + 14, tileY + 14))
				{
					graphics.fill(tileX - 1, tileY - 1, tileX + 13, tileY + 13, Color.WHITE.getRGB());
				}
			}
		}
	}
	
	@Override
	public void handleMouseClick(int mouseX, int mouseY, int mouseButton)
	{
		for(int i = 0; i < tabTiles.size(); i++)
		{
			int x = i % 6 * 15 + xPosition + 3;
			int y = i / 6 * 15 + yPosition + 18;
			if(GuiHelper.isMouseInside(mouseX, mouseY, x - 1, y - 1, x + 12, y + 12))
			{
				game.setCurrentTile(tabTiles.get(i));
				return;
			}
		}
	}
	
	public void updateTiles()
	{
		tabTiles.clear();
		
		Tile.Category category = Tile.Category.values()[currentCategory];
		labelCurrentCategory.setText(category.name);
		
		for(Tile tile : Game.getRegisteredtiles().values())
		{
			if(tile.getCategory() == category)
			{
				tabTiles.add(tile);
			}
		}
	}

}
