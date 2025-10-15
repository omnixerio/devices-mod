package com.ultreon.devices.object;


import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.ultreon.devices.api.app.Component;
import com.ultreon.devices.core.Laptop;
import com.ultreon.devices.exception.WorldLessException;
import com.ultreon.devices.object.tiles.Tile;
import com.ultreon.devices.util.GuiHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class Game extends Component {
    public static final ResourceLocation ICONS = new ResourceLocation("devices:textures/gui/mine_racer.png");

    private static final Map<Integer, Tile> registeredTiles = new HashMap<Integer, Tile>();
    private final Player player;
    public int mapWidth;
    public int mapHeight;
    private Tile[][] tiles;
    private boolean editorMode = false;
    private Tile currentTile = Tile.grass;
    private Layer currentLayer = Layer.BACKGROUND;
    private boolean renderBackground = true;
    private final boolean renderMidgroundLow = true;
    private final boolean renderMidgroundHigh = true;
    private boolean renderForeground = true;
    private boolean renderPlayer = true;

    public Game(int left, int top, int mapWidth, int mapHeight) throws Exception {
        super(left, top);

        if (Laptop.isWorldLess()) throw new WorldLessException("The game can only exist if the universe exists.");

        if (mapWidth % Tile.WIDTH != 0 || mapHeight % Tile.HEIGHT != 0)
            throw new Exception("Width and height need to be a multiple of " + Tile.WIDTH);

        this.player = new Player(this);
        this.mapWidth = mapWidth / Tile.WIDTH;
        this.mapHeight = mapHeight / Tile.HEIGHT;
        this.tiles = new Tile[4][this.mapWidth * this.mapHeight];
    }

    public static void registerTile(int id, Tile tile) {
        registeredTiles.put(id, tile);
    }

    public static Map<Integer, Tile> getRegisteredTiles() {
        return registeredTiles;
    }

    public void setEditorMode(boolean editorMode) {
        this.editorMode = editorMode;
    }

    public boolean loadMap(int width, int height, int[][] data) {
        if (data.length != 4) return false;

        if (data[0].length != width * height) return false;

        this.mapWidth = width;
        this.mapHeight = height;
        this.tiles = new Tile[4][this.mapWidth * this.mapHeight];

        for (int layer = 0; layer < data.length; layer++) {
            for (int tile = 0; tile < data[0].length; tile++) {
                this.tiles[layer][tile] = registeredTiles.get(data[layer][tile]);
            }
        }
        return true;
    }

    @Override
    public void handleTick() {
        if (renderPlayer) {
            player.tick();
        }
    }

    @Override
    public void handleMouseClick(int mouseX, int mouseY, int mouseButton) {
        if (editorMode) {
            int startX = xPosition;
            int startY = yPosition;
            int endX = startX + mapWidth * Tile.WIDTH;
            int endY = startY + mapHeight * Tile.HEIGHT;
            if (GuiHelper.isMouseInside(mouseX, mouseY, startX, startY, endX, endY)) {
                int pixelX = (mouseX - startX) / Tile.WIDTH;
                int pixelY = (mouseY - startY) / Tile.HEIGHT;
                if (mouseButton == 0) placeTile(pixelX, pixelY, currentTile);
                else if (mouseButton == 1) placeTile(pixelX, pixelY, null);
            }
        }
    }

    @Override
    public void handleMouseDrag(int mouseX, int mouseY, int mouseButton) {
        if (editorMode) {
            int startX = xPosition;
            int startY = yPosition;
            int endX = startX + mapWidth * Tile.WIDTH;
            int endY = startY + mapHeight * Tile.HEIGHT;
            if (GuiHelper.isMouseInside(mouseX, mouseY, startX, startY, endX, endY)) {
                int pixelX = (mouseX - startX) / Tile.WIDTH;
                int pixelY = (mouseY - startY) / Tile.HEIGHT;
                if (mouseButton == 0) placeTile(pixelX, pixelY, currentTile);
                else if (mouseButton == 1) placeTile(pixelX, pixelY, null);
            }
        }
    }

    @Override
    public void render(GuiGraphics graphics, Laptop laptop, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean windowActive, float partialTicks) {
        //long start = System.currentTimeMillis();

        if (editorMode) {
            graphics.fill(xPosition - 1, yPosition - 1, xPosition + mapWidth * Tile.WIDTH + 1, yPosition + mapHeight * Tile.HEIGHT + 1, Color.DARK_GRAY.getRGB());
        }

        graphics.pose().pushPose();
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.setShaderTexture(0, ICONS);

        if (renderBackground) {
            for (int i = 0; i < tiles[0].length; i++) {
                Tile tile = tiles[0][i];
                if (tile != null) {
                    tile.render(graphics, this, i % mapWidth, i / mapWidth, Layer.BACKGROUND);
                }
            }

            for (int i = 0; i < tiles[0].length; i++) {
                Tile tile = tiles[0][i];
                if (tile != null) {
                    tile.renderForeground(graphics, this, i % mapWidth, i / mapWidth, Layer.BACKGROUND);
                }
            }
        }

        if (renderMidgroundLow) {
            for (int i = 0; i < tiles[1].length; i++) {
                Tile tile = tiles[1][i];
                if (tile != null) {
                    tile.render(graphics, this, i % mapWidth, i / mapWidth, Layer.MIDGROUND_LOW);
                }
            }

            for (int i = 0; i < tiles[1].length; i++) {
                Tile tile = tiles[1][i];
                if (tile != null) {
                    tile.renderForeground(graphics, this, i % mapWidth, i / mapWidth, Layer.MIDGROUND_LOW);
                }
            }
        }

        if (renderPlayer) {
            player.render(graphics, xPosition, yPosition, partialTicks);
        }

        RenderSystem.setShaderTexture(0, ICONS);
        if (renderMidgroundHigh) {
            for (int i = 0; i < tiles[2].length; i++) {
                Tile tile = tiles[2][i];
                if (tile != null) {
                    tile.render(graphics, this, i % mapWidth, i / mapWidth, Layer.MIDGROUND_HIGH);
                }
            }

            for (int i = 0; i < tiles[2].length; i++) {
                Tile tile = tiles[2][i];
                if (tile != null) {
                    tile.renderForeground(graphics, this, i % mapWidth, i / mapWidth, Layer.MIDGROUND_HIGH);
                }
            }
        }

        if (renderForeground) {
            for (int i = 0; i < tiles[3].length; i++) {
                Tile tile = tiles[3][i];
                if (tile != null) {
                    tile.render(graphics, this, i % mapWidth, i / mapWidth, Layer.FOREGROUND);
                }
            }

            for (int i = 0; i < tiles[3].length; i++) {
                Tile tile = tiles[3][i];
                if (tile != null) {
                    tile.renderForeground(graphics, this, i % mapWidth, i / mapWidth, Layer.FOREGROUND);
                }
            }
        }

        graphics.pose().popPose();

        //DebugLog.log("Rendered game in " + (System.currentTimeMillis() - start));
    }

    public boolean placeTile(int x, int y, Tile tile) {
        int index = x + y * mapWidth;
        if (index >= 0 && index < mapWidth * mapHeight) {
            tiles[currentLayer.layer][index] = tile;
            return true;
        }
        return false;
    }

    public Tile getTile(Layer layer, int x, int y) {
        if (x < 0) return null;
        if (x >= mapWidth) return null;

        int index = x + y * mapWidth;
        if (index >= 0 && index < mapWidth * mapHeight) {
            return tiles[layer.layer][index];
        }
        return null;
    }

    public boolean isFullTile(Layer layer, int x, int y) {
        if (x < 0) return true;
        if (x >= mapWidth) return true;

        int index = x + y * mapWidth;
        if (index >= 0 && index < mapWidth * mapHeight) {
            Tile tile = tiles[layer.layer][index];
            if (tile != null) return tile.isFullTile();
        }
        return true;
    }

    public Tile getCurrentTile() {
        return currentTile;
    }

    public void setCurrentTile(Tile currentTile) {
        this.currentTile = currentTile;
    }

    public Layer getCurrentLayer() {
        return currentLayer;
    }

    public void setCurrentLayer(Layer currentLayer) {
        this.currentLayer = currentLayer;
    }

    public void setRenderBackground(boolean renderBackground) {
        this.renderBackground = renderBackground;
    }

    public void setRenderForeground(boolean renderForeground) {
        this.renderForeground = renderForeground;
    }

    public void setRenderPlayer(boolean renderPlayer) {
        this.renderPlayer = renderPlayer;
    }

    public void nextLayer() {
        int next = currentLayer.layer + 1;
        if (next <= Layer.values().length - 1) {
            this.currentLayer = Layer.values()[next];
        }
    }

    public void prevLayer() {
        int prev = currentLayer.layer - 1;
        if (prev >= 0) {
            this.currentLayer = Layer.values()[prev];
        }
    }

    // Temp method
    public void fill(Tile tile) {
        for (int i = 0; i < tiles[0].length; i++) tiles[0][i] = tile;
    }

    public enum Layer {
        BACKGROUND(0, 0), MIDGROUND_LOW(1, 0), MIDGROUND_HIGH(2, 20), FOREGROUND(3, 30);

        public int layer;
        public double zLevel;

        Layer(int layer, double zLevel) {
            this.layer = layer;
            this.zLevel = zLevel;
        }

        public Layer up() {
            if (layer + 1 <= values().length - 1) {
                return values()[layer + 1];
            }
            return this;
        }

        public Layer down() {
            if (layer - 1 >= 0) {
                return values()[layer - 1];
            }
            return this;
        }
    }
}
