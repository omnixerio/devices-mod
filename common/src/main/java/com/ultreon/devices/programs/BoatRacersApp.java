package com.ultreon.devices.programs;


import com.ultreon.devices.api.app.Application;
import com.ultreon.devices.api.app.Dialog;
import com.ultreon.devices.api.app.Icons;
import com.ultreon.devices.api.app.Layout;
import com.ultreon.devices.api.app.component.Button;
import com.ultreon.devices.api.app.component.CheckBox;
import com.ultreon.devices.api.app.component.Label;
import com.ultreon.devices.core.Laptop;
import com.ultreon.devices.exception.WorldLessException;
import com.ultreon.devices.object.Game;
import com.ultreon.devices.object.TileGrid;
import com.ultreon.devices.object.tiles.Tile;
import net.minecraft.nbt.CompoundTag;

import org.jetbrains.annotations.Nullable;

public class BoatRacersApp extends Application {
    private Layout layoutLevelEditor;
    private Game game;
    private TileGrid tileGrid;
    private Label labelLayer;
    private Button btnNextLayer;
    private Button btnPrevLayer;
    private CheckBox checkBoxForeground;
    private CheckBox checkBoxBackground;
    private CheckBox checkBoxPlayer;

    public BoatRacersApp() {
        //super("boat_racer", "Boat Racers");
        this.setDefaultWidth(320);
        this.setDefaultHeight(160);
    }

    @Override
    public void init(@Nullable CompoundTag intent) {
        layoutLevelEditor = new Layout(364, 178);

        try {
            game = new Game(4, 4, 256, 144);
            game.setEditorMode(true);
            game.setRenderPlayer(false);
            game.fill(Tile.grass);
            layoutLevelEditor.addComponent(game);
        } catch (WorldLessException e) {
            setCurrentLayout(new Layout(320, 160));
            Dialog.Message message = new Dialog.Message(e.getMessage()) {
                @Override
                public void onClose() {
                    super.onClose();
                    BoatRacersApp.this.getWindow().close();
                }
            };
            message.setTitle("Error");
            openDialog(message);
            return;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        tileGrid = new TileGrid(266, 3, game);
        layoutLevelEditor.addComponent(tileGrid);

        labelLayer = new Label("1", 280, 108);
        layoutLevelEditor.addComponent(labelLayer);

        btnNextLayer = new Button(266, 106, Icons.CHEVRON_RIGHT);
        btnNextLayer.setClickListener((event) ->
        {
            game.nextLayer();
            labelLayer.setText(Integer.toString(game.getCurrentLayer().layer + 1));
        });
        layoutLevelEditor.addComponent(btnNextLayer);

        btnPrevLayer = new Button(314, 106, Icons.CHEVRON_LEFT);
        btnPrevLayer.setClickListener((event) ->
        {
            game.prevLayer();
            labelLayer.setText(Integer.toString(game.getCurrentLayer().layer + 1));
        });
        layoutLevelEditor.addComponent(btnPrevLayer);

        checkBoxBackground = new CheckBox("Background", 3, 151);
        checkBoxBackground.setClickListener((event) -> game.setRenderBackground(checkBoxBackground.isSelected()));
        checkBoxBackground.setSelected(true);
        layoutLevelEditor.addComponent(checkBoxBackground);

        checkBoxForeground = new CheckBox("Foreground", 80, 151);
        checkBoxForeground.setClickListener((event) -> game.setRenderForeground(checkBoxForeground.isSelected()));
        checkBoxForeground.setSelected(true);
        layoutLevelEditor.addComponent(checkBoxForeground);

        checkBoxPlayer = new CheckBox("Player", 160, 151);
        checkBoxPlayer.setClickListener((event) -> game.setRenderPlayer(checkBoxPlayer.isSelected()));
        layoutLevelEditor.addComponent(checkBoxPlayer);

        setCurrentLayout(layoutLevelEditor);
    }

    @Override
    public void onTick() {
        if (Laptop.isWorldLess()) {
            return;
        }
        super.onTick();
    }

    @Override
    public void load(CompoundTag tagCompound) {
        // TODO Auto-generated method stub

    }

    @Override
    public void save(CompoundTag tagCompound) {
        // TODO Auto-generated method stub

    }


}
