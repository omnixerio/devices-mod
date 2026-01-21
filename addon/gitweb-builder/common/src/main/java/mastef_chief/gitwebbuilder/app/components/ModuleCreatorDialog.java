package mastef_chief.gitwebbuilder.app.components;

import com.ultreon.devices.api.app.Component;
import com.ultreon.devices.api.app.Dialog;
import com.ultreon.devices.api.app.Layout;
import com.ultreon.devices.api.app.ScrollableLayout;
import com.ultreon.devices.api.app.component.Button;
import com.ultreon.devices.api.app.component.Label;
import com.ultreon.devices.api.app.component.*;
import com.ultreon.devices.api.app.component.TextArea;
import com.ultreon.devices.api.app.component.TextField;
import com.ultreon.devices.api.app.listener.SlideListener;
import com.ultreon.devices.api.utils.RenderUtil;
import com.ultreon.devices.core.Laptop;
import com.ultreon.devices.object.ColorGrid;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

public class ModuleCreatorDialog extends Dialog {

    private static final int DIVIDE_WIDTH = 15;

    private String selectedModule = null;
    private String inputText = "";
    private String positiveText = "Create";
    private String negativeText = "Cancel";

    private Color setColor = Color.darkGray;

    private ResponseHandler<String> responseListener;

    private Button buttonPositive;
    private Button buttonNegative;

    private Component colorDisplay;

    private ColorGrid colorGrid;

    private Slider redSlider;
    private Slider greenSlider;
    private Slider blueSlider;

    private TextArea selectedTextArea;

    public static final int LAYOUT_WIDTH = 175;
    public static final int LAYOUT_HEIGHT = 150;

    public ModuleCreatorDialog(String module, TextArea textArea) {

        this.selectedModule = module;
        this.selectedTextArea = textArea;
    }


    @Override
    public void init(@Nullable CompoundTag nbtTagCompound) {
        super.init(nbtTagCompound);

        Layout layout = new Layout(LAYOUT_WIDTH, LAYOUT_HEIGHT);
        layout.setBackground((gui, mc, x, y, width, height, mouseX, mouseY, windowActive) ->
        {
            Color color = new Color(Laptop.getSystem().getSettings().getColorScheme().getItemBackgroundColor());
            gui.fill(x, y, x + LAYOUT_WIDTH, y + LAYOUT_HEIGHT, Color.DARK_GRAY.getRGB());
        });

        this.setTitle("Module Builder (" + selectedModule + ")");


        if (selectedModule.equals("Paragraph")) {
            ScrollableLayout scrollableLayout = new ScrollableLayout(0, 0, LAYOUT_WIDTH, 125, LAYOUT_HEIGHT - 25);
            scrollableLayout.setScrollSpeed(8);
            scrollableLayout.setBackground((gui, mc, x, y, width, height, mouseX, mouseY, windowActive) ->
            {
                Color color = new Color(Laptop.getSystem().getSettings().getColorScheme().getItemBackgroundColor());
                gui.fill(x, y, x + LAYOUT_WIDTH, y + LAYOUT_HEIGHT - 25, Color.gray.getRGB());
            });

            Label textLabel = new Label(ChatFormatting.RED + "*" + ChatFormatting.RESET + "Text:", 5, 8);
            scrollableLayout.addComponent(textLabel);
            TextField textTextField = new TextField(5, 20, 162);
            scrollableLayout.addComponent(textTextField);

            Label paddingLabel = new Label("Padding:", 5, 40);
            scrollableLayout.addComponent(paddingLabel);
            TextField paddingTextField = new TextField(5, 52, 162);
            scrollableLayout.addComponent(paddingTextField);


            Label imageLabel = new Label("Image Link:", 5, 72);
            scrollableLayout.addComponent(imageLabel);
            TextField imageTextField = new TextField(5, 84, 162);
            scrollableLayout.addComponent(imageTextField);

            layout.addComponent(scrollableLayout);

            int positiveWidth = Minecraft.getInstance().font.width(positiveText);
            buttonPositive = new Button(125, 130, positiveText){
                @Override
                public void handleCharTyped(char character, int code) {
                    super.handleCharTyped(character, code);
                    System.out.println("Handled key press");
                    if(!textTextField.getText().isEmpty()){
                        System.out.println("on");
                        buttonPositive.setEnabled(true);
                    }else {
                        System.out.println("off");
                        buttonPositive.setEnabled(false);
                    }

                }
            };
            buttonPositive.setEnabled(false);
            buttonPositive.setSize(positiveWidth + 10, 16);
            buttonPositive.setClickListener((mouseX, mouseY, mouseButton) ->
            {
                if (mouseButton == 0) {
                        selectedTextArea.performReturn();
                        selectedTextArea.writeText("#paragraph");
                        selectedTextArea.performReturn();
                        selectedTextArea.writeText("text=" + textTextField.getText());
                        if (!paddingTextField.getText().isEmpty()) {
                            selectedTextArea.performReturn();
                            selectedTextArea.writeText("padding=" + paddingTextField.getText());
                        }
                        if (!imageTextField.getText().isEmpty()) {
                            selectedTextArea.performReturn();
                            selectedTextArea.writeText("image=" + imageTextField.getText());
                        }
                        selectedTextArea.performReturn();
                        close();

                }
            });
            layout.addComponent(buttonPositive);

            int negativeWidth = Minecraft.getInstance().font.width(negativeText);
            buttonNegative = new Button(75, 130, negativeText);
            buttonNegative.setSize(negativeWidth + 10, 16);
            buttonNegative.setClickListener((mouseX, mouseY, mouseButton) -> close());
            layout.addComponent(buttonNegative);

            Label requiredLabel = new Label(ChatFormatting.RED + "*" + ChatFormatting.RESET + " Required", 7, 134);
            layout.addComponent(requiredLabel);

        }

        if (selectedModule.equals("Navigation")) {

            ScrollableLayout scrollableLayout = new ScrollableLayout(0, 0, LAYOUT_WIDTH, 1060, LAYOUT_HEIGHT - 25);
            scrollableLayout.setScrollSpeed(8);
            scrollableLayout.setBackground((gui, mc, x, y, width, height, mouseX, mouseY, windowActive) ->
            {
                Color color = new Color(Laptop.getSystem().getSettings().getColorScheme().getItemBackgroundColor());
                gui.fill(x, y, x + LAYOUT_WIDTH, y + 1060, Color.gray.getRGB());
            });

            Text colorLabel = new Text(ChatFormatting.RESET + "Menu \nColor:", 10, 15, 60);
            scrollableLayout.addComponent(colorLabel);

            Label redLabel = new Label("R ", 100, 12);
            scrollableLayout.addComponent(redLabel);
            redSlider = new Slider(110, 10, 50);
            redSlider.setSlideListener(new SlideListener() {
                @Override
                public void onSlide(float v) {
                    setColor = new Color(v, greenSlider.getPercentage(), blueSlider.getPercentage());
                }
            });
            scrollableLayout.addComponent(redSlider);

            Label greenLabel = new Label("G ", 100, 28);
            scrollableLayout.addComponent(greenLabel);
            greenSlider = new Slider(110, 26, 50);
            greenSlider.setSlideListener(new SlideListener() {
                @Override
                public void onSlide(float v) {
                    setColor = new Color(redSlider.getPercentage(), v, blueSlider.getPercentage());
                }
            });
            scrollableLayout.addComponent(greenSlider);

            Label blueLabel = new Label("B ", 100, 44);
            scrollableLayout.addComponent(blueLabel);
            blueSlider = new Slider(110, 42, 50);
            blueSlider.setSlideListener(new SlideListener() {
                @Override
                public void onSlide(float v) {
                    setColor = new Color(redSlider.getPercentage(), greenSlider.getPercentage(), v);
                }
            });
            scrollableLayout.addComponent(blueSlider);

            colorDisplay = new Component(45, 5) {
                @Override
                public void render(GuiGraphics graphics, Laptop laptop, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean windowActive, float partialTicks) {
                    graphics.fill(xPosition, yPosition, xPosition + 50, yPosition + 51, Color.DARK_GRAY.getRGB());
                    graphics.fill(xPosition + 1, yPosition + 1, xPosition + 49, yPosition + 50, setColor.getRGB());
                }
            };
            scrollableLayout.addComponent(colorDisplay);

            Label menuItemLink1Label = new Label("Menu Item Link (1):", 5, 70);
            scrollableLayout.addComponent(menuItemLink1Label);
            TextField menuItemLink1TextField = new TextField(5, 80, 162);
            scrollableLayout.addComponent(menuItemLink1TextField);

            Label menuItemLabel1Label = new Label("Menu Item Label (1):", 5, 100);
            scrollableLayout.addComponent(menuItemLabel1Label);
            TextField menuItemLabel1TextField = new TextField(5, 110, 162);
            scrollableLayout.addComponent(menuItemLabel1TextField);

            Label menuItemIcon1Label = new Label("Menu Item Icon (1):", 5, 130);
            scrollableLayout.addComponent(menuItemIcon1Label);
            TextField menuItemIcon1TextField = new TextField(5, 140, 162);
            scrollableLayout.addComponent(menuItemIcon1TextField);


            Label menuItemLink2Label = new Label("Menu Item Link (2):", 5, 170);
            scrollableLayout.addComponent(menuItemLink2Label);
            TextField menuItemLink2TextField = new TextField(5, 180, 162);
            scrollableLayout.addComponent(menuItemLink2TextField);

            Label menuItemLabel2Label = new Label("Menu Item Label (2):", 5, 200);
            scrollableLayout.addComponent(menuItemLabel2Label);
            TextField menuItemLabel2TextField = new TextField(5, 210, 162);
            scrollableLayout.addComponent(menuItemLabel2TextField);

            Label menuItemIcon2Label = new Label("Menu Item Icon (2):", 5, 230);
            scrollableLayout.addComponent(menuItemIcon2Label);
            TextField menuItemIcon2TextField = new TextField(5, 240, 162);
            scrollableLayout.addComponent(menuItemIcon2TextField);


            Label menuItemLink3Label = new Label("Menu Item Link (3):", 5, 270);
            scrollableLayout.addComponent(menuItemLink3Label);
            TextField menuItemLink3TextField = new TextField(5, 280, 162);
            scrollableLayout.addComponent(menuItemLink3TextField);

            Label menuItemLabel3Label = new Label("Menu Item Label (3):", 5, 300);
            scrollableLayout.addComponent(menuItemLabel3Label);
            TextField menuItemLabel3TextField = new TextField(5, 310, 162);
            scrollableLayout.addComponent(menuItemLabel3TextField);

            Label menuItemIcon3Label = new Label("Menu Item Icon (3):", 5, 330);
            scrollableLayout.addComponent(menuItemIcon3Label);
            TextField menuItemIcon3TextField = new TextField(5, 340, 162);
            scrollableLayout.addComponent(menuItemIcon3TextField);


            Label menuItemLink4Label = new Label("Menu Item Link (4):", 5, 370);
            scrollableLayout.addComponent(menuItemLink4Label);
            TextField menuItemLink4TextField = new TextField(5, 380, 162);
            scrollableLayout.addComponent(menuItemLink4TextField);

            Label menuItemLabel4Label = new Label("Menu Item Label (4):", 5, 400);
            scrollableLayout.addComponent(menuItemLabel4Label);
            TextField menuItemLabel4TextField = new TextField(5, 410, 162);
            scrollableLayout.addComponent(menuItemLabel4TextField);

            Label menuItemIcon4Label = new Label("Menu Item Icon (4):", 5, 430);
            scrollableLayout.addComponent(menuItemIcon4Label);
            TextField menuItemIcon4TextField = new TextField(5, 440, 162);
            scrollableLayout.addComponent(menuItemIcon4TextField);


            Label menuItemLink5Label = new Label("Menu Item Link (5):", 5, 470);
            scrollableLayout.addComponent(menuItemLink5Label);
            TextField menuItemLink5TextField = new TextField(5, 480, 162);
            scrollableLayout.addComponent(menuItemLink5TextField);

            Label menuItemLabel5Label = new Label("Menu Item Label (5):", 5, 500);
            scrollableLayout.addComponent(menuItemLabel5Label);
            TextField menuItemLabel5TextField = new TextField(5, 510, 162);
            scrollableLayout.addComponent(menuItemLabel5TextField);

            Label menuItemIcon5Label = new Label("Menu Item Icon (5):", 5, 530);
            scrollableLayout.addComponent(menuItemIcon5Label);
            TextField menuItemIcon5TextField = new TextField(5, 540, 162);
            scrollableLayout.addComponent(menuItemIcon5TextField);


            Label menuItemLink6Label = new Label("Menu Item Link (6):", 5, 570);
            scrollableLayout.addComponent(menuItemLink6Label);
            TextField menuItemLink6TextField = new TextField(5, 580, 162);
            scrollableLayout.addComponent(menuItemLink6TextField);

            Label menuItemLabel6Label = new Label("Menu Item Label (6):", 5, 600);
            scrollableLayout.addComponent(menuItemLabel6Label);
            TextField menuItemLabel6TextField = new TextField(5, 610, 162);
            scrollableLayout.addComponent(menuItemLabel6TextField);

            Label menuItemIcon6Label = new Label("Menu Item Icon (6):", 5, 630);
            scrollableLayout.addComponent(menuItemIcon6Label);
            TextField menuItemIcon6TextField = new TextField(5, 640, 162);
            scrollableLayout.addComponent(menuItemIcon6TextField);


            Label menuItemLink7Label = new Label("Menu Item Link (7):", 5, 670);
            scrollableLayout.addComponent(menuItemLink7Label);
            TextField menuItemLink7TextField = new TextField(5, 680, 162);
            scrollableLayout.addComponent(menuItemLink7TextField);

            Label menuItemLabel7Label = new Label("Menu Item Label (7):", 5, 700);
            scrollableLayout.addComponent(menuItemLabel7Label);
            TextField menuItemLabel7TextField = new TextField(5, 710, 162);
            scrollableLayout.addComponent(menuItemLabel7TextField);

            Label menuItemIcon7Label = new Label("Menu Item Icon (7):", 5, 730);
            scrollableLayout.addComponent(menuItemIcon7Label);
            TextField menuItemIcon7TextField = new TextField(5, 740, 162);
            scrollableLayout.addComponent(menuItemIcon7TextField);


            Label menuItemLink8Label = new Label("Menu Item Link (8):", 5, 770);
            scrollableLayout.addComponent(menuItemLink8Label);
            TextField menuItemLink8TextField = new TextField(5, 780, 162);
            scrollableLayout.addComponent(menuItemLink8TextField);

            Label menuItemLabel8Label = new Label("Menu Item Label (8):", 5, 800);
            scrollableLayout.addComponent(menuItemLabel8Label);
            TextField menuItemLabel8TextField = new TextField(5, 810, 162);
            scrollableLayout.addComponent(menuItemLabel8TextField);

            Label menuItemIcon8Label = new Label("Menu Item Icon (8):", 5, 830);
            scrollableLayout.addComponent(menuItemIcon8Label);
            TextField menuItemIcon8TextField = new TextField(5, 840, 162);
            scrollableLayout.addComponent(menuItemIcon8TextField);


            Label menuItemLink9Label = new Label("Menu Item Link (9):", 5, 870);
            scrollableLayout.addComponent(menuItemLink9Label);
            TextField menuItemLink9TextField = new TextField(5, 880, 162);
            scrollableLayout.addComponent(menuItemLink9TextField);

            Label menuItemLabel9Label = new Label("Menu Item Label (9):", 5, 900);
            scrollableLayout.addComponent(menuItemLabel9Label);
            TextField menuItemLabel9TextField = new TextField(5, 910, 162);
            scrollableLayout.addComponent(menuItemLabel9TextField);

            Label menuItemIcon9Label = new Label("Menu Item Icon (9):", 5, 930);
            scrollableLayout.addComponent(menuItemIcon9Label);
            TextField menuItemIcon9TextField = new TextField(5, 940, 162);
            scrollableLayout.addComponent(menuItemIcon9TextField);

            Label menuItemLink10Label = new Label("Menu Item Link (10):", 5, 970);
            scrollableLayout.addComponent(menuItemLink10Label);
            TextField menuItemLink10TextField = new TextField(5, 980, 162);
            scrollableLayout.addComponent(menuItemLink10TextField);

            Label menuItemLabel10Label = new Label("Menu Item Label (10):", 5, 1000);
            scrollableLayout.addComponent(menuItemLabel10Label);
            TextField menuItemLabel10TextField = new TextField(5, 1010, 162);
            scrollableLayout.addComponent(menuItemLabel10TextField);

            Label menuItemIcon10Label = new Label("Menu Item Icon (10):", 5, 1030);
            scrollableLayout.addComponent(menuItemIcon10Label);
            TextField menuItemIcon10TextField = new TextField(5, 1040, 162);
            scrollableLayout.addComponent(menuItemIcon10TextField);

            layout.addComponent(scrollableLayout);

            int positiveWidth = Minecraft.getInstance().font.width(positiveText);
            buttonPositive = new Button(125, 130, positiveText){
                @Override
                public void handleCharTyped(char character, int code) {
                    super.handleCharTyped(character, code);

                    if(!menuItemLink1TextField.getText().isEmpty() && !menuItemLabel1TextField.getText().isEmpty() || !menuItemIcon1TextField.getText().isEmpty()){
                        buttonPositive.setEnabled(true);
                    }else {
                        buttonPositive.setEnabled(false);
                    }

                }
            };
            buttonPositive.setEnabled(false);
            buttonPositive.setSize(positiveWidth + 10, 16);
            buttonPositive.setClickListener((mouseX, mouseY, mouseButton) ->
            {
                if (mouseButton == 0) {
                    selectedTextArea.performReturn();
                    selectedTextArea.writeText("#navigation");
                    selectedTextArea.performReturn();
                    if (setColor != Color.darkGray) {
                        selectedTextArea.writeText("color=" + setColor.getRGB());
                        selectedTextArea.performReturn();
                    }
                    if (!menuItemLink1TextField.getText().isEmpty()) {
                        selectedTextArea.writeText("item-link-1=" + menuItemLink1TextField.getText());
                        selectedTextArea.performReturn();
                        if (!menuItemLabel1TextField.getText().isEmpty()) {
                            selectedTextArea.writeText("item-label-1=" + menuItemLabel1TextField.getText());
                            selectedTextArea.performReturn();
                        }
                        if (!menuItemIcon1TextField.getText().isEmpty()) {
                            selectedTextArea.writeText("item-icon-1=" + menuItemIcon1TextField.getText().toUpperCase());
                            selectedTextArea.performReturn();
                        }
                        if (menuItemLabel1TextField.getText().isEmpty() && menuItemIcon1TextField.getText().isEmpty()) {
                            this.openDialog(new Message("Menu Item (1) requires either a label or a icon."));
                        }
                    }

                    if (!menuItemLink2TextField.getText().isEmpty()) {
                        selectedTextArea.writeText("item-link-2=" + menuItemLink2TextField.getText());
                        selectedTextArea.performReturn();
                        if (!menuItemLabel2TextField.getText().isEmpty()) {
                            selectedTextArea.writeText("item-label-2=" + menuItemLabel2TextField.getText());
                            selectedTextArea.performReturn();
                        }
                        if (!menuItemIcon2TextField.getText().isEmpty()) {
                            selectedTextArea.writeText("item-icon-2=" + menuItemIcon2TextField.getText().toUpperCase());
                            selectedTextArea.performReturn();
                        }
                        if (menuItemLabel2TextField.getText().isEmpty() && menuItemIcon2TextField.getText().isEmpty()) {
                            this.openDialog(new Message("Menu Item (2) requires either a label or a icon."));
                        }
                    }

                    if (!menuItemLink3TextField.getText().isEmpty()) {
                        selectedTextArea.writeText("item-link-3=" + menuItemLink3TextField.getText());
                        selectedTextArea.performReturn();
                        if (!menuItemLabel3TextField.getText().isEmpty()) {
                            selectedTextArea.writeText("item-label-3=" + menuItemLabel3TextField.getText());
                            selectedTextArea.performReturn();
                        }
                        if (!menuItemIcon3TextField.getText().isEmpty()) {
                            selectedTextArea.writeText("item-icon-3=" + menuItemIcon3TextField.getText().toUpperCase());
                            selectedTextArea.performReturn();
                        }
                        if (menuItemLabel3TextField.getText().isEmpty() && menuItemIcon3TextField.getText().isEmpty()) {
                            this.openDialog(new Message("Menu Item (3) requires either a label or a icon."));
                        }
                    }

                    if (!menuItemLink4TextField.getText().isEmpty()) {
                        selectedTextArea.writeText("item-link-4=" + menuItemLink4TextField.getText());
                        selectedTextArea.performReturn();
                        if (!menuItemLabel4TextField.getText().isEmpty()) {
                            selectedTextArea.writeText("item-label-4=" + menuItemLabel4TextField.getText());
                            selectedTextArea.performReturn();
                        }
                        if (!menuItemIcon4TextField.getText().isEmpty()) {
                            selectedTextArea.writeText("item-icon-4=" + menuItemIcon4TextField.getText().toUpperCase());
                            selectedTextArea.performReturn();
                        }
                        if (menuItemLabel4TextField.getText().isEmpty() && menuItemIcon4TextField.getText().isEmpty()) {
                            this.openDialog(new Message("Menu Item (4) requires either a label or a icon."));
                        }
                    }

                    if (!menuItemLink5TextField.getText().isEmpty()) {
                        selectedTextArea.writeText("item-link-5=" + menuItemLink5TextField.getText());
                        selectedTextArea.performReturn();
                        if (!menuItemLabel5TextField.getText().isEmpty()) {
                            selectedTextArea.writeText("item-label-5=" + menuItemLabel5TextField.getText());
                            selectedTextArea.performReturn();
                        }
                        if (!menuItemIcon5TextField.getText().isEmpty()) {
                            selectedTextArea.writeText("item-icon-5=" + menuItemIcon5TextField.getText().toUpperCase());
                            selectedTextArea.performReturn();
                        }
                        if (menuItemLabel5TextField.getText().isEmpty() && menuItemIcon5TextField.getText().isEmpty()) {
                            this.openDialog(new Message("Menu Item (5) requires either a label or a icon."));
                        }
                    }

                    if (!menuItemLink6TextField.getText().isEmpty()) {
                        selectedTextArea.writeText("item-link-6=" + menuItemLink6TextField.getText());
                        selectedTextArea.performReturn();
                        if (!menuItemLabel6TextField.getText().isEmpty()) {
                            selectedTextArea.writeText("item-label-6=" + menuItemLabel6TextField.getText());
                            selectedTextArea.performReturn();
                        }
                        if (!menuItemIcon6TextField.getText().isEmpty()) {
                            selectedTextArea.writeText("item-icon-6=" + menuItemIcon6TextField.getText().toUpperCase());
                            selectedTextArea.performReturn();
                        }
                        if (menuItemLabel6TextField.getText().isEmpty() && menuItemIcon6TextField.getText().isEmpty()) {
                            this.openDialog(new Message("Menu Item (6) requires either a label or a icon."));
                        }
                    }

                    if (!menuItemLink7TextField.getText().isEmpty()) {
                        selectedTextArea.writeText("item-link-7=" + menuItemLink7TextField.getText());
                        selectedTextArea.performReturn();
                        if (!menuItemLabel7TextField.getText().isEmpty()) {
                            selectedTextArea.writeText("item-label-7=" + menuItemLabel7TextField.getText());
                            selectedTextArea.performReturn();
                        }
                        if (!menuItemIcon7TextField.getText().isEmpty()) {
                            selectedTextArea.writeText("item-icon-7=" + menuItemIcon7TextField.getText().toUpperCase());
                            selectedTextArea.performReturn();
                        }
                        if (menuItemLabel7TextField.getText().isEmpty() && menuItemIcon7TextField.getText().isEmpty()) {
                            this.openDialog(new Message("Menu Item (7) requires either a label or a icon."));
                        }
                    }

                    if (!menuItemLink8TextField.getText().isEmpty()) {
                        selectedTextArea.writeText("item-link-8=" + menuItemLink8TextField.getText());
                        selectedTextArea.performReturn();
                        if (!menuItemLabel8TextField.getText().isEmpty()) {
                            selectedTextArea.writeText("item-label-8=" + menuItemLabel8TextField.getText());
                            selectedTextArea.performReturn();
                        }
                        if (!menuItemIcon8TextField.getText().isEmpty()) {
                            selectedTextArea.writeText("item-icon-8=" + menuItemIcon8TextField.getText().toUpperCase());
                            selectedTextArea.performReturn();
                        }
                        if (menuItemLabel8TextField.getText().isEmpty() && menuItemIcon8TextField.getText().isEmpty()) {
                            this.openDialog(new Message("Menu Item (8) requires either a label or a icon."));
                        }
                    }

                    if (!menuItemLink9TextField.getText().isEmpty()) {
                        selectedTextArea.writeText("item-link-9=" + menuItemLink9TextField.getText());
                        selectedTextArea.performReturn();
                        if (!menuItemLabel9TextField.getText().isEmpty()) {
                            selectedTextArea.writeText("item-label-9=" + menuItemLabel9TextField.getText());
                            selectedTextArea.performReturn();
                        }
                        if (!menuItemIcon9TextField.getText().isEmpty()) {
                            selectedTextArea.writeText("item-icon-9=" + menuItemIcon9TextField.getText().toUpperCase());
                            selectedTextArea.performReturn();
                        }
                        if (menuItemLabel9TextField.getText().isEmpty() && menuItemIcon9TextField.getText().isEmpty()) {
                            this.openDialog(new Message("Menu Item (9) requires either a label or a icon."));
                        }
                    }

                    if (!menuItemLink10TextField.getText().isEmpty()) {
                        selectedTextArea.writeText("item-link-10=" + menuItemLink10TextField.getText());
                        selectedTextArea.performReturn();
                        if (!menuItemLabel10TextField.getText().isEmpty()) {
                            selectedTextArea.writeText("item-label-10=" + menuItemLabel10TextField.getText());
                            selectedTextArea.performReturn();
                        }
                        if (!menuItemIcon10TextField.getText().isEmpty()) {
                            selectedTextArea.writeText("item-icon-10=" + menuItemIcon10TextField.getText().toUpperCase());
                            selectedTextArea.performReturn();
                        }
                    }

                    close();
                }
            });
            layout.addComponent(buttonPositive);

            int negativeWidth = Minecraft.getInstance().font.width(negativeText);
            buttonNegative = new Button(75, 130, negativeText);
            buttonNegative.setSize(negativeWidth + 10, 16);
            buttonNegative.setClickListener((mouseX, mouseY, mouseButton) -> close());
            layout.addComponent(buttonNegative);

        }

        if(selectedModule.equals("Brewing")){

            /*ScrollableLayout scrollableLayout = new ScrollableLayout(0, 0, LAYOUT_WIDTH, 220, LAYOUT_HEIGHT - 25);
            scrollableLayout.setScrollSpeed(8);
            scrollableLayout.setBackground((gui, mc, x, y, width, height, mouseX, mouseY, windowActive) ->
            {
                Color color = new Color(Laptop.getSystem().getSettings().getColorScheme().getItemBackgroundColor());
                gui.drawRect(x, y, x + LAYOUT_WIDTH, y + 220, Color.gray.getRGB());
            });

            Label titleLabel = new Label("Title:", 5, 8);
            scrollableLayout.addComponent(titleLabel);
            TextField titleTextField = new TextField(5, 18, 162);
            scrollableLayout.addComponent(titleTextField);

            Label descLabel = new Label("Description:", 5, 38);
            scrollableLayout.addComponent(descLabel);
            TextField descTextField = new TextField(5, 48, 162);
            scrollableLayout.addComponent(descTextField);

            Label fuelLabel = new Label("Fuel:", 5, 68);
            scrollableLayout.addComponent(fuelLabel);
            TextField fuelTextField = new TextField(5, 78, 162);
            fuelTextField.setPlaceholder("{id:\"minecraft:blaze_powder\",Count:1}");
            scrollableLayout.addComponent(fuelTextField);

            Label inputLabel = new Label("Input:", 5, 98);
            scrollableLayout.addComponent(inputLabel);
            TextField inputTextField = new TextField(5, 108, 162);
            scrollableLayout.addComponent(inputTextField);

            Label output1Label = new Label("Output (1):", 5, 128);
            scrollableLayout.addComponent(output1Label);
            TextField output1TextField = new TextField(5, 138, 162);
            scrollableLayout.addComponent(output1TextField);

            Label output2Label = new Label("Output (2):", 5, 158);
            scrollableLayout.addComponent(output2Label);
            TextField output2TextField = new TextField(5, 168, 162);
            scrollableLayout.addComponent(output2TextField);

            Label output3Label = new Label("Output (3):", 5, 188);
            scrollableLayout.addComponent(output3Label);
            TextField output3TextField = new TextField(5, 198, 162);
            scrollableLayout.addComponent(output3TextField);

            layout.addComponent(scrollableLayout);

            int positiveWidth = Minecraft.getMinecraft().fontRenderer.getStringWidth(positiveText);
            buttonPositive = new Button(125, 130, positiveText);
            buttonPositive.setSize(positiveWidth + 10, 16);
            buttonPositive.setClickListener((mouseX, mouseY, mouseButton) ->
            {

                if (mouseButton == 0) {
                    selectedTextArea.performReturn();
                    selectedTextArea.writeText("#brewing");
                    selectedTextArea.performReturn();
                    if(!titleTextField.getText().isEmpty()){
                        selectedTextArea.writeText("title=" + titleTextField.getText());
                        selectedTextArea.performReturn();
                    }
                    if(!descTextField.getText().isEmpty()){
                        selectedTextArea.writeText("desc=" + descTextField.getText());
                        selectedTextArea.performReturn();
                    }
                    if(!fuelTextField.getText().isEmpty()){
                        selectedTextArea.writeText("slot-fuel=" + fuelTextField.getText());
                        selectedTextArea.performReturn();
                    }
                    if(!inputTextField.getText().isEmpty()){
                        selectedTextArea.writeText("slot-input=" + inputTextField.getText());
                        selectedTextArea.performReturn();
                    }
                    if(!output1TextField.getText().isEmpty()){
                        selectedTextArea.writeText("slot-output-1=" + output1TextField.getText());
                        selectedTextArea.performReturn();
                    }
                    if(!output2TextField.getText().isEmpty()){
                        selectedTextArea.writeText("slot-output-2=" + output2TextField.getText());
                        selectedTextArea.performReturn();
                    }
                    if(!output3TextField.getText().isEmpty()){
                        selectedTextArea.writeText("slot-output-3=" + output3TextField.getText());
                        selectedTextArea.performReturn();
                    }


                    close();
                }
            });
            layout.addComponent(buttonPositive);

            int negativeWidth = Minecraft.getMinecraft().fontRenderer.getStringWidth(negativeText);
            buttonNegative = new Button(75, 130, negativeText);
            buttonNegative.setSize(negativeWidth + 10, 16);
            buttonNegative.setClickListener((mouseX, mouseY, mouseButton) -> close());
            layout.addComponent(buttonNegative);*/



        }

        if(selectedModule.equals("Download")){

            ScrollableLayout scrollableLayout = new ScrollableLayout(0, 0, LAYOUT_WIDTH, 130, LAYOUT_HEIGHT - 25);
            scrollableLayout.setScrollSpeed(8);
            scrollableLayout.setBackground((gui, mc, x, y, width, height, mouseX, mouseY, windowActive) ->
            {
                Color color = new Color(Laptop.getSystem().getSettings().getColorScheme().getItemBackgroundColor());
                gui.fill(x, y, x + LAYOUT_WIDTH, y + 130, Color.gray.getRGB());
            });

            Label fileAppLabel = new Label(ChatFormatting.RED + "*" + ChatFormatting.RESET + "File App:", 5, 8);
            scrollableLayout.addComponent(fileAppLabel);
            TextField fileAppTextField = new TextField(5, 18, 162);
            scrollableLayout.addComponent(fileAppTextField);

            Label fileDataLabel = new Label(ChatFormatting.RED + "*" + ChatFormatting.RESET + "File Data:", 5, 38);
            scrollableLayout.addComponent(fileDataLabel);
            TextField fileDataTextField = new TextField(5, 48, 162);
            scrollableLayout.addComponent(fileDataTextField);

            Label fileNameLabel = new Label("File Name:", 5, 68);
            scrollableLayout.addComponent(fileNameLabel);
            TextField fileNameTextField = new TextField(5, 78, 162);
            scrollableLayout.addComponent(fileNameTextField);

            Label textLabel = new Label("Text:", 5, 98);
            scrollableLayout.addComponent(textLabel);
            TextField textTextField = new TextField(5, 108, 162);
            scrollableLayout.addComponent(textTextField);



            layout.addComponent(scrollableLayout);

            int positiveWidth = Minecraft.getInstance().font.width(positiveText);
            buttonPositive = new Button(125, 130, positiveText){
                @Override
                public void handleCharTyped(char character, int code) {
                    super.handleCharTyped(character, code);

                    if(!fileAppTextField.getText().isEmpty() && !fileDataTextField.getText().isEmpty()){
                        buttonPositive.setEnabled(true);
                    }else {
                        buttonPositive.setEnabled(false);
                    }

                }
            };
            buttonPositive.setEnabled(false);
            buttonPositive.setSize(positiveWidth + 10, 16);
            buttonPositive.setClickListener((mouseX, mouseY, mouseButton) ->
            {

                if (mouseButton == 0) {

                    selectedTextArea.performReturn();
                    selectedTextArea.writeText("#download");
                    selectedTextArea.performReturn();
                    selectedTextArea.writeText("file-app=" + fileAppTextField.getText());
                    selectedTextArea.performReturn();
                    selectedTextArea.writeText("file-data=" + fileDataTextField.getText());
                    selectedTextArea.performReturn();
                    if(!fileNameTextField.getText().isEmpty()){
                        selectedTextArea.writeText("file-name=" + fileNameTextField.getText());
                        selectedTextArea.performReturn();
                    }
                    if(!textTextField.getText().isEmpty()){
                        selectedTextArea.writeText("text=" + textTextField.getText());
                        selectedTextArea.performReturn();
                    }
                    close();

                }
            });
            layout.addComponent(buttonPositive);

            int negativeWidth = Minecraft.getInstance().font.width(negativeText);
            buttonNegative = new Button(75, 130, negativeText);
            buttonNegative.setSize(negativeWidth + 10, 16);
            buttonNegative.setClickListener((mouseX, mouseY, mouseButton) -> close());
            layout.addComponent(buttonNegative);

            Label requiredLabel = new Label(ChatFormatting.RED + "*" + ChatFormatting.RESET + " Required", 7, 134);
            layout.addComponent(requiredLabel);

        }


        if(selectedModule.equals("Footer")){

            ScrollableLayout scrollableLayout = new ScrollableLayout(0, 0, LAYOUT_WIDTH, 160, LAYOUT_HEIGHT - 25);
            scrollableLayout.setScrollSpeed(8);
            scrollableLayout.setBackground((gui, mc, x, y, width, height, mouseX, mouseY, windowActive) ->
            {
                Color color = new Color(Laptop.getSystem().getSettings().getColorScheme().getItemBackgroundColor());
                gui.fill(x, y, x + LAYOUT_WIDTH, y + 160, Color.gray.getRGB());
            });

            Label titleLabel = new Label(ChatFormatting.RED + "*" + ChatFormatting.RESET + "Title:", 5, 8);
            scrollableLayout.addComponent(titleLabel);
            TextField titleTextField = new TextField(5, 18, 162);
            scrollableLayout.addComponent(titleTextField);

            Label subtitleLabel = new Label(ChatFormatting.RED + "*" + ChatFormatting.RESET + "Subtitle:", 5, 38);
            scrollableLayout.addComponent(subtitleLabel);
            TextField subtitleTextField = new TextField(5, 48, 162);
            scrollableLayout.addComponent(subtitleTextField);

            Label homePageLinkLabel = new Label(ChatFormatting.RED + "*" + ChatFormatting.RESET + "Home Page Link:", 5, 68);
            scrollableLayout.addComponent(homePageLinkLabel);
            TextField homePageLinkTextField = new TextField(5, 78, 162);
            scrollableLayout.addComponent(homePageLinkTextField);

            Label colorLabel = new Label("Color:", 5, 98);
            scrollableLayout.addComponent(colorLabel);

            Label redLabel = new Label("R ", 100, 107);
            scrollableLayout.addComponent(redLabel);
            redSlider = new Slider(110, 105, 50);
            redSlider.setSlideListener(new SlideListener() {
                @Override
                public void onSlide(float v) {
                    setColor = new Color(v, greenSlider.getPercentage(), blueSlider.getPercentage());
                }
            });
            scrollableLayout.addComponent(redSlider);

            Label greenLabel = new Label("G ", 100, 123);
            scrollableLayout.addComponent(greenLabel);
            greenSlider = new Slider(110, 121, 50);
            greenSlider.setSlideListener(new SlideListener() {
                @Override
                public void onSlide(float v) {
                    setColor = new Color(redSlider.getPercentage(), v, blueSlider.getPercentage());
                }
            });
            scrollableLayout.addComponent(greenSlider);

            Label blueLabel = new Label("B ", 100, 139);
            scrollableLayout.addComponent(blueLabel);
            blueSlider = new Slider(110, 137, 50);
            blueSlider.setSlideListener(new SlideListener() {
                @Override
                public void onSlide(float v) {
                    setColor = new Color(redSlider.getPercentage(), greenSlider.getPercentage(), v);
                }
            });
            scrollableLayout.addComponent(blueSlider);

            colorDisplay = new Component(45, 100) {
                @Override
                public void render(GuiGraphics graphics, Laptop laptop, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean windowActive, float partialTicks) {
                    graphics.fill(xPosition, yPosition, xPosition + 50, yPosition + 51, Color.DARK_GRAY.getRGB());
                    graphics.fill(xPosition + 1, yPosition + 1, xPosition + 49, yPosition + 50, setColor.getRGB());
                }
            };
            scrollableLayout.addComponent(colorDisplay);



            layout.addComponent(scrollableLayout);

            int positiveWidth = Minecraft.getInstance().font.width(positiveText);
            buttonPositive = new Button(125, 130, positiveText){
                @Override
                public void handleCharTyped(char character, int code) {
                    super.handleCharTyped(character, code);

                    if(!titleTextField.getText().isEmpty() && !subtitleTextField.getText().isEmpty() && !homePageLinkTextField.getText().isEmpty()){
                        buttonPositive.setEnabled(true);
                    }else {
                        buttonPositive.setEnabled(false);
                    }

                }
            };
            buttonPositive.setEnabled(false);
            buttonPositive.setSize(positiveWidth + 10, 16);
            buttonPositive.setClickListener((mouseX, mouseY, mouseButton) ->
            {

                if (mouseButton == 0) {

                    selectedTextArea.performReturn();
                    selectedTextArea.writeText("#footer");
                    selectedTextArea.performReturn();
                    selectedTextArea.writeText("title=" + titleTextField.getText());
                    selectedTextArea.performReturn();
                    selectedTextArea.writeText("sub-title=" + subtitleTextField.getText());
                    selectedTextArea.performReturn();
                    selectedTextArea.writeText("home-page=" + homePageLinkTextField.getText());
                    selectedTextArea.performReturn();
                    if (setColor != Color.darkGray) {
                        selectedTextArea.writeText("color=" + setColor.getRGB());
                        selectedTextArea.performReturn();
                    }
                    close();
                }
            });
            layout.addComponent(buttonPositive);

            int negativeWidth = Minecraft.getInstance().font.width(negativeText);
            buttonNegative = new Button(75, 130, negativeText);
            buttonNegative.setSize(negativeWidth + 10, 16);
            buttonNegative.setClickListener((mouseX, mouseY, mouseButton) -> close());
            layout.addComponent(buttonNegative);

            Label requiredLabel = new Label(ChatFormatting.RED + "*" + ChatFormatting.RESET + " Required", 7, 134);
            layout.addComponent(requiredLabel);

        }

        if(selectedModule.equals("Divider")){

            ScrollableLayout scrollableLayout = new ScrollableLayout(0, 0, LAYOUT_WIDTH, 120, LAYOUT_HEIGHT - 25);
            scrollableLayout.setScrollSpeed(8);
            scrollableLayout.setBackground((gui, mc, x, y, width, height, mouseX, mouseY, windowActive) ->
            {
                Color color = new Color(Laptop.getSystem().getSettings().getColorScheme().getItemBackgroundColor());
                gui.fill(x, y, x + LAYOUT_WIDTH, y + 120, Color.gray.getRGB());
            });

            Label sizeLabel = new Label(ChatFormatting.RED + "*" + ChatFormatting.RESET + "Size:", 5, 8);
            scrollableLayout.addComponent(sizeLabel);
            TextField sizeTextField = new TextField(5, 18, 162);
            scrollableLayout.addComponent(sizeTextField);

            Label colorLabel = new Label("Color:", 5, 38);
            scrollableLayout.addComponent(colorLabel);

            Label redLabel = new Label("R ", 100, 47);
            scrollableLayout.addComponent(redLabel);
            redSlider = new Slider(110, 45, 50);
            redSlider.setSlideListener(new SlideListener() {
                @Override
                public void onSlide(float v) {
                    setColor = new Color(v, greenSlider.getPercentage(), blueSlider.getPercentage());
                }
            });
            scrollableLayout.addComponent(redSlider);

            Label greenLabel = new Label("G ", 100, 63);
            scrollableLayout.addComponent(greenLabel);
            greenSlider = new Slider(110, 61, 50);
            greenSlider.setSlideListener(new SlideListener() {
                @Override
                public void onSlide(float v) {
                    setColor = new Color(redSlider.getPercentage(), v, blueSlider.getPercentage());
                }
            });
            scrollableLayout.addComponent(greenSlider);

            Label blueLabel = new Label("B ", 100, 79);
            scrollableLayout.addComponent(blueLabel);
            blueSlider = new Slider(110, 77, 50);
            blueSlider.setSlideListener(new SlideListener() {
                @Override
                public void onSlide(float v) {
                    setColor = new Color(redSlider.getPercentage(), greenSlider.getPercentage(), v);
                }
            });
            scrollableLayout.addComponent(blueSlider);

            colorDisplay = new Component(45, 40) {
                @Override
                public void render(GuiGraphics graphics, Laptop laptop, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean windowActive, float partialTicks) {
                    graphics.fill(xPosition, yPosition, xPosition + 50, yPosition + 51, Color.DARK_GRAY.getRGB());
                    graphics.fill(xPosition + 1, yPosition + 1, xPosition + 49, yPosition + 50, setColor.getRGB());
                }
            };
            scrollableLayout.addComponent(colorDisplay);



            layout.addComponent(scrollableLayout);

            int positiveWidth = Minecraft.getInstance().font.width(positiveText);
            buttonPositive = new Button(125, 130, positiveText){
                @Override
                public void handleCharTyped(char character, int code) {
                    super.handleCharTyped(character, code);

                    if(!sizeTextField.getText().isEmpty()){
                        buttonPositive.setEnabled(true);
                    }else {
                        buttonPositive.setEnabled(false);
                    }

                }
            };
            buttonPositive.setEnabled(false);
            buttonPositive.setSize(positiveWidth + 10, 16);
            buttonPositive.setClickListener((mouseX, mouseY, mouseButton) ->
            {

                if (mouseButton == 0) {

                    selectedTextArea.performReturn();
                    selectedTextArea.writeText("#divider");
                    selectedTextArea.performReturn();
                    selectedTextArea.writeText("size=" + sizeTextField.getText());
                    selectedTextArea.performReturn();
                    if (setColor != Color.darkGray) {
                        selectedTextArea.writeText("color=" + setColor.getRGB());
                        selectedTextArea.performReturn();
                    }
                    close();

                }
            });
            layout.addComponent(buttonPositive);

            int negativeWidth = Minecraft.getInstance().font.width(negativeText);
            buttonNegative = new Button(75, 130, negativeText);
            buttonNegative.setSize(negativeWidth + 10, 16);
            buttonNegative.setClickListener((mouseX, mouseY, mouseButton) -> close());
            layout.addComponent(buttonNegative);

            Label requiredLabel = new Label(ChatFormatting.RED + "*" + ChatFormatting.RESET + " Required", 7, 134);
            layout.addComponent(requiredLabel);

        }

        if(selectedModule.equals("Header")){

            ScrollableLayout scrollableLayout = new ScrollableLayout(0, 0, LAYOUT_WIDTH, 125, LAYOUT_HEIGHT - 25);
            scrollableLayout.setScrollSpeed(8);
            scrollableLayout.setBackground((gui, mc, x, y, width, height, mouseX, mouseY, windowActive) ->
            {
                Color color = new Color(Laptop.getSystem().getSettings().getColorScheme().getItemBackgroundColor());
                gui.fill(x, y, x + LAYOUT_WIDTH, y + LAYOUT_HEIGHT - 25, Color.gray.getRGB());
            });

            Label textLabel = new Label(ChatFormatting.RED + "*" + ChatFormatting.RESET + "Text:", 5, 8);
            scrollableLayout.addComponent(textLabel);
            TextField textTextField = new TextField(5, 18, 162);
            scrollableLayout.addComponent(textTextField);

            Label scaleLabel = new Label("Scale:", 5, 38);
            scrollableLayout.addComponent(scaleLabel);
            TextField scaleTextField = new TextField(5, 48, 162);
            scrollableLayout.addComponent(scaleTextField);

            Label paddingLabel = new Label("Padding:", 5, 68);
            scrollableLayout.addComponent(paddingLabel);
            TextField paddingTextField = new TextField(5, 78, 162);
            scrollableLayout.addComponent(paddingTextField);

            Label alignLabel = new Label("Align:", 5, 98);
            scrollableLayout.addComponent(alignLabel);
            TextField alignTextField = new TextField(5, 108, 162);
            scrollableLayout.addComponent(alignTextField);

            layout.addComponent(scrollableLayout);

            int positiveWidth = Minecraft.getInstance().font.width(positiveText);
            buttonPositive = new Button(125, 130, positiveText){
                @Override
                public void handleCharTyped(char character, int code) {
                    super.handleCharTyped(character, code);

                    if(!textTextField.getText().isEmpty()){
                        buttonPositive.setEnabled(true);
                    }else {
                        buttonPositive.setEnabled(false);
                    }

                }
            };
            buttonPositive.setEnabled(false);
            buttonPositive.setSize(positiveWidth + 10, 16);
            buttonPositive.setClickListener((mouseX, mouseY, mouseButton) ->
            {
                if (mouseButton == 0) {
                    selectedTextArea.performReturn();
                    selectedTextArea.writeText("#header");
                    selectedTextArea.performReturn();
                    selectedTextArea.writeText("text=" + textTextField.getText());
                    selectedTextArea.performReturn();
                    if(!scaleTextField.getText().isEmpty()){
                        selectedTextArea.writeText("scale=" + scaleTextField.getText());
                        selectedTextArea.performReturn();
                    }
                    if(!paddingTextField.getText().isEmpty()){
                        selectedTextArea.writeText("padding=" + paddingTextField.getText());
                        selectedTextArea.performReturn();
                    }
                    if(!alignTextField.getText().isEmpty()){
                        selectedTextArea.writeText("align=" + alignTextField.getText());
                        selectedTextArea.performReturn();
                    }

                    close();

                }
            });
            layout.addComponent(buttonPositive);

            int negativeWidth = Minecraft.getInstance().font.width(negativeText);
            buttonNegative = new Button(75, 130, negativeText);
            buttonNegative.setSize(negativeWidth + 10, 16);
            buttonNegative.setClickListener((mouseX, mouseY, mouseButton) -> close());
            layout.addComponent(buttonNegative);

            Label requiredLabel = new Label(ChatFormatting.RED + "*" + ChatFormatting.RESET + " Required", 7, 134);
            layout.addComponent(requiredLabel);

        }

        if(selectedModule.equals("Banner")){

            ScrollableLayout scrollableLayout = new ScrollableLayout(0, 0, LAYOUT_WIDTH, 125, LAYOUT_HEIGHT - 25);
            scrollableLayout.setScrollSpeed(8);
            scrollableLayout.setBackground((gui, mc, x, y, width, height, mouseX, mouseY, windowActive) ->
            {
                Color color = new Color(Laptop.getSystem().getSettings().getColorScheme().getItemBackgroundColor());
                gui.fill(x, y, x + LAYOUT_WIDTH, y + LAYOUT_HEIGHT - 25, Color.gray.getRGB());
            });

            Label imageLabel = new Label(ChatFormatting.RED + "*" + ChatFormatting.RESET + "Image Link:", 5, 8);
            scrollableLayout.addComponent(imageLabel);
            TextField imageTextField = new TextField(5, 18, 162);
            scrollableLayout.addComponent(imageTextField);

            Label textLabel = new Label("Text:", 5, 38);
            scrollableLayout.addComponent(textLabel);
            TextField textTextField = new TextField(5, 48, 162);
            scrollableLayout.addComponent(textTextField);


            layout.addComponent(scrollableLayout);

            int positiveWidth = Minecraft.getInstance().font.width(positiveText);
            buttonPositive = new Button(125, 130, positiveText){
                @Override
                public void handleCharTyped(char character, int code) {
                    super.handleCharTyped(character, code);

                    if(!imageTextField.getText().isEmpty()){
                        buttonPositive.setEnabled(true);
                    }else {
                        buttonPositive.setEnabled(false);
                    }

                }
            };
            buttonPositive.setEnabled(false);
            buttonPositive.setSize(positiveWidth + 10, 16);
            buttonPositive.setClickListener((mouseX, mouseY, mouseButton) ->
            {
                if (mouseButton == 0) {
                    selectedTextArea.performReturn();
                    selectedTextArea.writeText("#banner");
                    selectedTextArea.performReturn();
                    selectedTextArea.writeText("image=" + imageTextField.getText());
                    selectedTextArea.performReturn();
                    if(!textTextField.getText().isEmpty()){
                        selectedTextArea.writeText("text=" + textTextField.getText());
                    }

                    close();

                }
            });
            layout.addComponent(buttonPositive);

            int negativeWidth = Minecraft.getInstance().font.width(negativeText);
            buttonNegative = new Button(75, 130, negativeText);
            buttonNegative.setSize(negativeWidth + 10, 16);
            buttonNegative.setClickListener((mouseX, mouseY, mouseButton) -> close());
            layout.addComponent(buttonNegative);

            Label requiredLabel = new Label(ChatFormatting.RED + "*" + ChatFormatting.RESET + " Required", 7, 134);
            layout.addComponent(requiredLabel);

        }

        this.setLayout(layout);


    }


}
