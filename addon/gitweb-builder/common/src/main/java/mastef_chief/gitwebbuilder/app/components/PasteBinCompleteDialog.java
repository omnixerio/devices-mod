package mastef_chief.gitwebbuilder.app.components;

import com.ultreon.devices.api.app.Dialog;
import com.ultreon.devices.api.app.Layout;
import com.ultreon.devices.api.app.component.Button;
import com.ultreon.devices.api.app.component.Text;
import com.ultreon.devices.api.app.listener.ClickListener;
import com.ultreon.devices.api.task.TaskManager;
import mastef_chief.gitwebbuilder.app.tasks.TaskNotificationCopiedLink;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.font.TextFieldHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.FormattedText;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

public class PasteBinCompleteDialog extends Dialog{

    Toolkit toolkit = Toolkit.getDefaultToolkit();

    private String messageText = "";

    private ClickListener positiveListener;
    private Button buttonPositive;
    private Button openLinkButton;
    private Button copyToClipboard;

    public PasteBinCompleteDialog(String messageText)
    {
        this.messageText = messageText;
    }

    @Override
    public void init(@Nullable CompoundTag nbtTagCompound)
    {
        super.init(nbtTagCompound);

        int lines = Minecraft.getInstance().font.split(FormattedText.of("Link: " + messageText), getWidth() - 10).size();
        defaultLayout.height += (lines) * Minecraft.getInstance().font.lineHeight;

        defaultLayout.setBackground((gui, mc, x, y, width, height, mouseX, mouseY, windowActive) ->
        {
                gui.fill(x, y, x + width, y + height, Color.LIGHT_GRAY.getRGB());
        });

        Text message = new Text("Link: " + messageText, 5, 5, getWidth() - 10);
        this.addComponent(message);

        buttonPositive = new Button(getWidth() - 41, getHeight() - 20, "Close");
        buttonPositive.setSize(36, 16);
        buttonPositive.setClickListener((mouseX, mouseY, mouseButton) ->
        {
            if(positiveListener != null)
            {
                positiveListener.onClick(mouseX, mouseY, mouseButton);
            }
            close();
        });
        this.addComponent(buttonPositive);

        /*openLinkButton = new Button(getWidth() - 145, getHeight() - 20, "Open");
        openLinkButton.setClickListener((mouseX, mouseY, mouseButton) -> {
            if(mouseButton == 0){
                GuiConfirmOpenLink gui = new GuiConfirmOpenLink((res, id) ->{



                }, messageText, 0, false);
                gui.disableSecurityWarning();
                Minecraft.getMinecraft().displayGuiScreen(gui);
            }
        });
        this.addComponent(openLinkButton);*/

        copyToClipboard = new Button(getWidth() - 104, getHeight() - 20, "Copy Link");
        copyToClipboard.setClickListener((mouseX, mouseY, mouseButton) -> {
            if(mouseButton == 0){
                TextFieldHelper.setClipboardContents(Minecraft.getInstance(), messageText);
                TaskManager.sendTask(new TaskNotificationCopiedLink());
            }
        });
        this.addComponent(copyToClipboard);

    }

}
