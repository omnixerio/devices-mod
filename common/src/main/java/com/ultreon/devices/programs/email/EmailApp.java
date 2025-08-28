package com.ultreon.devices.programs.email;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.ultreon.devices.Resources;
import com.ultreon.devices.api.ApplicationManager;
import com.ultreon.devices.api.app.Component;
import com.ultreon.devices.api.app.Dialog;
import com.ultreon.devices.api.app.*;
import com.ultreon.devices.api.app.component.Button;
import com.ultreon.devices.api.app.component.Image;
import com.ultreon.devices.api.app.component.Label;
import com.ultreon.devices.api.app.component.TextArea;
import com.ultreon.devices.api.app.component.TextField;
import com.ultreon.devices.api.app.component.*;
import com.ultreon.devices.api.app.renderer.ListItemRenderer;
import com.ultreon.devices.api.io.File;
import com.ultreon.devices.api.task.TaskManager;
import com.ultreon.devices.api.utils.RenderUtil;
import com.ultreon.devices.core.Laptop;
import com.ultreon.devices.object.AppInfo;
import com.ultreon.devices.programs.email.object.Contact;
import com.ultreon.devices.programs.email.object.Email;
import com.ultreon.devices.programs.email.task.*;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;

import org.jetbrains.annotations.Nullable;
import java.awt.*;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings({"FieldCanBeLocal", "unused"})
public class EmailApp extends Application {
    private static final ResourceLocation ENDER_MAIL_ICONS = Resources.ENDER_MAIL_ICONS;
    private static final ResourceLocation ENDER_MAIL_BACKGROUND = Resources.ENDER_MAIL_BACKGROUND;

    private static final Pattern EMAIL = Pattern.compile("^([a-zA-Z\\d]{1,10})@endermail\\.official$");
    private final Color COLOR_EMAIL_CONTENT_BACKGROUND = new Color(160, 160, 160);

    /* Loading Layout */
    private Layout layoutInit;
    private Spinner spinnerInit;
    private Label labelLoading;

    /* Main Menu Layout */
    private Layout layoutMainMenu;
    private Image.AppImage logo;
    private Label labelLogo;
    private Button btnRegisterAccount;

    /* Register Account Layout */
    private Layout layoutRegisterAccount;
    private Label labelEmail;
    private TextField fieldEmail;
    private Label labelDomain;
    private Button btnRegister;

    /* Inbox Layout */
    private Layout layoutInbox;
    private ItemList<Email> listEmails;
    private Button btnViewEmail;
    private Button btnNewEmail;
    private Button btnReplyEmail;
    private Button btnDeleteEmail;
    private Button btnRefresh;

    /* New Email Layout */
    private Layout layoutNewEmail;
    private TextField fieldRecipient;
    private TextField fieldSubject;
    private TextArea textAreaMessage;
    private Button btnSendEmail;
    private Button btnCancelEmail;
    private Button btnAttachedFile;
    private Button btnRemoveAttachedFile;
    private Label labelAttachedFile;

    /* View Email Layout */
    private Layout layoutViewEmail;
    private Label labelViewSubject;
    private Label labelSender; // TODO unused
    private Label labelFrom;
    private Label labelViewSubjectContent; // TODO unused
    private Label labelViewMessage; // TODO unused
    private Text textMessage;
    private Button btnCancelViewEmail;
    private Button btnSaveAttachment;
    private Label labelAttachmentName;

    /* Contacts Layout */
    private Layout layoutContacts; // TODO unused
    private ItemList<Contact> listContacts; // TODO unused
    private Button btnAddContact; // TODO unused
    private Button btnDeleteContact; // TODO unused
    private Button btnCancelContact; // TODO unused

    /* Add Contact Layout */
    private Layout layoutAddContact; // TODO unused
    private Label labelContactNickname; // TODO unused
    private TextField fieldContactNickname; // TODO unused
    private Label labelContactEmail; // TODO unused
    private TextField fieldContactEmail; // TODO unused
    private Button btnSaveContact; // TODO unused
    private Button btnCancelAddContact; // TODO unused

    /* Insert Contact Layout */
    private Layout layoutInsertContact; // TODO unused
    private ItemList<Contact> listContacts2; // TODO unused
    private Button btnInsertContact; // TODO unused
    private Button btnCancelInsertContact; // TODO unused

    private String currentName;
    private File attachedFile;

    private List<Contact> contacts; // TODO unused

    @Override
    public void init(@Nullable CompoundTag intent) {
        /* Loading Layout */
        layoutInit = new Layout(40, 40);

        spinnerInit = new Spinner(14, 10);
        layoutInit.addComponent(spinnerInit);

        labelLoading = new Label("Loading...", 2, 26);
        layoutInit.addComponent(labelLoading);


        /* Main Menu Layout */

        layoutMainMenu = new Layout(200, 113);

        Image image = new Image(0, 0, layoutMainMenu.width, layoutMainMenu.height, 0, 0, 640, 360, 640, 360, ENDER_MAIL_BACKGROUND);
        image.setAlpha(0.85f);
        layoutMainMenu.addComponent(image);

        logo = new Image.AppImage(86, 20, 28, 28, info);
        //logo = new Image(86, 20, 28, 28, info.getIconU(), info.getIconV(), 14, 14, 224, 224, Laptop.ICON_TEXTURES);
        layoutMainMenu.addComponent(logo);

        labelLogo = new Label("Ender Mail", 100, 46);
        labelLogo.setAlignment(Component.ALIGN_CENTER);
        layoutMainMenu.addComponent(labelLogo);

        btnRegisterAccount = new Button(70, 65, "Register");
        btnRegisterAccount.setSize(60, 16);
        btnRegisterAccount.setClickListener((mouseX, mouseY, mouseButton) -> setCurrentLayout(layoutRegisterAccount));
        layoutMainMenu.addComponent(btnRegisterAccount);

        this.setCurrentLayout(layoutMainMenu);


        /* Register Account Layout */

        layoutRegisterAccount = new Layout(200, 113);

        image = new Image(0, 0, layoutRegisterAccount.width, layoutRegisterAccount.height, 0, 0, 640, 360, 640, 360, ENDER_MAIL_BACKGROUND);
        image.setAlpha(0.85f);
        layoutRegisterAccount.addComponent(image);

        labelEmail = new Label(ChatFormatting.BOLD + "Choose your email", layoutRegisterAccount.width / 2, 30);
        labelEmail.setAlignment(Component.ALIGN_CENTER);
        layoutRegisterAccount.addComponent(labelEmail);

        fieldEmail = new TextField(20, 50, 80);
        layoutRegisterAccount.addComponent(fieldEmail);

        labelDomain = new Label("@endermail.official", 105, 54);
        layoutRegisterAccount.addComponent(labelDomain);

        btnRegister = new Button(70, 80, "Register");
        btnRegister.setSize(60, 16);
        btnRegister.setClickListener((mouseX, mouseY, mouseButton) -> {
            int length = fieldEmail.getText().length();
            if (length > 0 && length <= 10) {
                TaskRegisterEmailAccount taskRegisterAccount = new TaskRegisterEmailAccount(fieldEmail.getText());
                taskRegisterAccount.setCallback((nbt, success) -> {
                    if (success) {
                        currentName = fieldEmail.getText();
                        setCurrentLayout(layoutInbox);
                    } else {
                        fieldEmail.setTextColor(Color.RED);
                    }
                });
                TaskManager.sendTask(taskRegisterAccount);
            }
        });
        layoutRegisterAccount.addComponent(btnRegister);


        /* Inbox Layout */

        layoutInbox = new Layout(260, 146);
        layoutInbox.setInitListener(() -> {
            TaskUpdateInbox taskUpdateInbox = new TaskUpdateInbox();
            taskUpdateInbox.setCallback((nbt, success) -> {
                listEmails.removeAll();
                for (Email email : EmailManager.INSTANCE.getInbox()) {
                    listEmails.addItem(email);
                }
            });
            TaskManager.sendTask(taskUpdateInbox);
        });
        layoutInbox.setBackground((graphics, mc, x, y, width, height, mouseX, mouseY, windowActive) -> {
            RenderSystem.setShaderTexture(0, ENDER_MAIL_BACKGROUND);
            RenderUtil.drawRectWithTexture(ENDER_MAIL_BACKGROUND, graphics, x, y, 0, 0, width, height, 640, 360, 640, 360);

            Color temp = new Color(Laptop.getSystem().getSettings().getColorScheme().getBackgroundColor());
            Color color = new Color(temp.getRed(), temp.getGreen(), temp.getBlue(), 150);
            graphics.fill(x, y, x + 125, y + height, color.getRGB());
            graphics.fill(x + 125, y, x + 126, y + height, color.darker().getRGB());

            Email e = listEmails.getSelectedItem();
            if (e != null) {
                graphics.fill(x + 130, y + 5, x + width - 5, y + 34, color.getRGB());
                graphics.fill(x + 130, y + 34, x + width - 5, y + 35, color.darker().getRGB());
                graphics.fill(x + 130, y + 35, x + width - 5, y + height - 5, new Color(1f, 1f, 1f, 0.25f).getRGB());
                RenderUtil.drawStringClipped(graphics, e.getSubject(), x + 135, y + 10, 120, Color.WHITE.getRGB(), true);
                RenderUtil.drawStringClipped(graphics, e.getAuthor() + "@endermail.official", x + 135, y + 22, 120, Color.LIGHT_GRAY.getRGB(), false);
                graphics.drawWordWrap(Laptop.getFont(), FormattedText.of(e.getMessage()), x + 135, y + 40, 115, Color.WHITE.getRGB());
            }
        });

        listEmails = new ItemList<>(5, 25, 116, 4);
        listEmails.setListItemRenderer(new ListItemRenderer<>(28) {
            @Override
            public void render(GuiGraphics graphics, Email e, Minecraft mc, int x, int y, int width, int height, boolean selected) {
                graphics.fill(x, y, x + width, y + height, selected ? Color.DARK_GRAY.getRGB() : Color.GRAY.getRGB());

                if (!e.isRead()) {
                    RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
                    RenderUtil.drawApplicationIcon(graphics, info, x + width - 16, y + 2);
                }

                if (e.getAttachment() != null) {
                    RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
                    int posX = x + (!e.isRead() ? -12 : 0) + width;
                    RenderSystem.setShaderTexture(0, ENDER_MAIL_ICONS);
                    RenderUtil.drawRectWithTexture(ENDER_MAIL_ICONS, graphics, posX, y + 16, 20, 10, 7, 10, 13, 20);
                }
                RenderUtil.drawStringClipped(graphics, e.getSubject(), x + 5, y + 5, width - 20, Color.WHITE.getRGB(), false);
                RenderUtil.drawStringClipped(graphics, e.getAuthor() + "@endermail.official", x + 5, y + 17, width - 20, Color.LIGHT_GRAY.getRGB(), false);
            }
        });
        layoutInbox.addComponent(listEmails);

        btnViewEmail = new Button(5, 5, ENDER_MAIL_ICONS, 30, 0, 10, 10);
        btnViewEmail.setClickListener((mouseX, mouseY, mouseButton) -> {
            int index = listEmails.getSelectedIndex();
            if (index != -1) {
                TaskManager.sendTask(new TaskViewEmail(index));
                Email email = listEmails.getSelectedItem();
                Objects.requireNonNull(email); // Fix nullability warning

                email.setRead(true);
                textMessage.setText(email.getMessage());
                labelViewSubject.setText(email.getSubject());
                labelFrom.setText(email.getAuthor() + "@endermail.official");
                attachedFile = email.getAttachment();
                if (attachedFile != null) {
                    btnSaveAttachment.setVisible(true);
                    labelAttachmentName.setVisible(true);
                    labelAttachmentName.setText(attachedFile.getName());
                }
                setCurrentLayout(layoutViewEmail);
            }
        });
        btnViewEmail.setToolTip("View", "Opens the currently selected email");
        layoutInbox.addComponent(btnViewEmail);

        btnNewEmail = new Button(25, 5, ENDER_MAIL_ICONS, 0, 0, 10, 10);
        btnNewEmail.setClickListener((mouseX, mouseY, mouseButton) -> setCurrentLayout(layoutNewEmail));
        btnNewEmail.setToolTip("New Email", "Send an email to a player");
        layoutInbox.addComponent(btnNewEmail);

        btnReplyEmail = new Button(45, 5, ENDER_MAIL_ICONS, 60, 0, 10, 10);
        btnReplyEmail.setClickListener((mouseX, mouseY, mouseButton) -> {
            Email email = listEmails.getSelectedItem();
            if (email != null) {
                setCurrentLayout(layoutNewEmail);
                fieldRecipient.setText(email.getAuthor() + "@endermail.official");
                fieldSubject.setText("RE: " + email.getSubject());
            }
        });
        btnReplyEmail.setToolTip("Reply", "Reply to the currently selected email");
        layoutInbox.addComponent(btnReplyEmail);

        btnDeleteEmail = new Button(65, 5, ENDER_MAIL_ICONS, 10, 0, 10, 10);
        btnDeleteEmail.setClickListener((mouseX, mouseY, mouseButton) -> {
            final int index = listEmails.getSelectedIndex();
            if (index != -1) {
                TaskDeleteEmail taskDeleteEmail = new TaskDeleteEmail(index);
                taskDeleteEmail.setCallback((nbt, success) -> {
                    listEmails.removeItem(index);
                    EmailManager.INSTANCE.getInbox().remove(index);
                });
                TaskManager.sendTask(taskDeleteEmail);
            }
        });
        btnDeleteEmail.setToolTip("Trash Email", "Deletes the currently select email");
        layoutInbox.addComponent(btnDeleteEmail);

        btnRefresh = new Button(85, 5, ENDER_MAIL_ICONS, 20, 0, 10, 10);
        btnRefresh.setClickListener((mouseX, mouseY, mouseButton) -> {
            TaskUpdateInbox taskUpdateInbox = new TaskUpdateInbox();
            taskUpdateInbox.setCallback((nbt, success) -> {
                listEmails.removeAll();
                for (Email email : EmailManager.INSTANCE.getInbox()) {
                    listEmails.addItem(email);
                }
            });
            TaskManager.sendTask(taskUpdateInbox);
        });
        btnRefresh.setToolTip("Refresh Inbox", "Checks for any new emails");
        layoutInbox.addComponent(btnRefresh);

        Button btnSettings = new Button(105, 5, Icons.WRENCH);
        layoutInbox.addComponent(btnSettings);


        /* New Email Layout */

        layoutNewEmail = new Layout(231, 148);
        layoutNewEmail.setBackground((graphics, mc, x, y, width, height, mouseX, mouseY, windowActive) -> {
            if (attachedFile != null) {
                AppInfo info = ApplicationManager.getApplication(Objects.requireNonNull(ResourceLocation.tryParse(attachedFile.getOpeningApp()), "Attached file has no opening app"));
                RenderUtil.drawApplicationIcon(graphics, info, x + 46, y + 130);
            }
        });

        fieldRecipient = new TextField(26, 5, 200);
        fieldRecipient.setPlaceholder("To");
        layoutNewEmail.addComponent(fieldRecipient);

        fieldSubject = new TextField(26, 23, 200);
        fieldSubject.setPlaceholder("Subject");
        layoutNewEmail.addComponent(fieldSubject);

        textAreaMessage = new TextArea(26, 41, 200, 85);
        textAreaMessage.setPlaceholder("Message");
        layoutNewEmail.addComponent(textAreaMessage);

        btnSendEmail = new Button(5, 5, ENDER_MAIL_ICONS, 50, 0, 10, 10);
        btnSendEmail.setClickListener((mouseX, mouseY, mouseButton) -> {
            Matcher matcher = EMAIL.matcher(fieldRecipient.getText());
            if (!matcher.matches()) return;

            Email email = new Email(fieldSubject.getText(), textAreaMessage.getText(), attachedFile);
            TaskSendEmail taskSendEmail = new TaskSendEmail(email, matcher.group(1));
            taskSendEmail.setCallback((nbt, success) -> {
                if (success) {
                    setCurrentLayout(layoutInbox);
                    textAreaMessage.clear();
                    fieldSubject.clear();
                    fieldRecipient.clear();
                    resetAttachedFile();
                }
            });
            TaskManager.sendTask(taskSendEmail);
        });
        btnSendEmail.setToolTip("Send", "Send email to recipient");
        layoutNewEmail.addComponent(btnSendEmail);

        btnCancelEmail = new Button(5, 25, ENDER_MAIL_ICONS, 40, 0, 10, 10);
        btnCancelEmail.setClickListener((mouseX, mouseY, mouseButton) -> {
            setCurrentLayout(layoutInbox);
            textAreaMessage.clear();
            fieldSubject.clear();
            fieldRecipient.clear();
            resetAttachedFile();
        });
        btnCancelEmail.setToolTip("Cancel", "Go back to Inbox");
        layoutNewEmail.addComponent(btnCancelEmail);

        btnAttachedFile = new Button(26, 129, ENDER_MAIL_ICONS, 70, 0, 10, 10);
        btnAttachedFile.setToolTip("Attach File", "Select a file from computer to attach to this email");
        btnAttachedFile.setClickListener((mouseX, mouseY, mouseButton) -> {
            if (mouseButton == 0) {
                Dialog.OpenFile dialog = new Dialog.OpenFile(this);
                dialog.setResponseHandler((success, file) -> {
                    if (!file.isFolder()) {
                        attachedFile = file.copy();
                        labelAttachedFile.setText(file.getName());
                        labelAttachedFile.left += 16;
                        labelAttachedFile.xPosition += 16;
                        btnAttachedFile.setVisible(false);
                        btnRemoveAttachedFile.setVisible(true);
                        dialog.close();
                    } else {
                        openDialog(new Dialog.Message("Attachment must be a file!"));
                    }
                    return false;
                });
                openDialog(dialog);
            }
        });
        layoutNewEmail.addComponent(btnAttachedFile);

        btnRemoveAttachedFile = new Button(26, 129, ENDER_MAIL_ICONS, 40, 0, 10, 10);
        btnRemoveAttachedFile.setToolTip("Remove Attachment", "Delete the attached file from this email");
        btnRemoveAttachedFile.setVisible(false);
        btnRemoveAttachedFile.setClickListener((mouseX, mouseY, mouseButton) -> {
            if (mouseButton == 0) {
                resetAttachedFile();
            }
        });
        layoutNewEmail.addComponent(btnRemoveAttachedFile);

        labelAttachedFile = new Label("No file attached", 46, 133);
        layoutNewEmail.addComponent(labelAttachedFile);


        /* View Email Layout */

        layoutViewEmail = new Layout(240, 156);
        layoutViewEmail.setBackground((graphics, mc, x, y, width, height, mouseX, mouseY, windowActive) -> {
            graphics.fill(x, y + 22, x + layoutViewEmail.width, y + 50, Color.GRAY.getRGB());
            graphics.fill(x, y + 22, x + layoutViewEmail.width, y + 23, Color.DARK_GRAY.getRGB());
            graphics.fill(x, y + 49, x + layoutViewEmail.width, y + 50, Color.DARK_GRAY.getRGB());
            graphics.fill(x, y + 50, x + layoutViewEmail.width, y + 156, COLOR_EMAIL_CONTENT_BACKGROUND.getRGB());

            if (attachedFile != null) {
                RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
                AppInfo info = ApplicationManager.getApplication(Objects.requireNonNull(ResourceLocation.tryParse(attachedFile.getOpeningApp()), "Attached file has no opening app"));
                RenderUtil.drawApplicationIcon(graphics, info, x + 204, y + 4);
            }
        });

        labelViewSubject = new Label("Subject", 5, 26);
        labelViewSubject.setTextColor(new Color(255, 170, 0));
        layoutViewEmail.addComponent(labelViewSubject);

        labelFrom = new Label("From", 5, 38);
        layoutViewEmail.addComponent(labelFrom);

        btnCancelViewEmail = new Button(5, 3, ENDER_MAIL_ICONS, 40, 0, 10, 10);
        btnCancelViewEmail.setClickListener((mouseX, mouseY, mouseButton) -> {
            if (mouseButton == 0) {
                attachedFile = null;
                btnSaveAttachment.setVisible(false);
                labelAttachmentName.setVisible(false);
                setCurrentLayout(layoutInbox);
            }

        });
        btnCancelViewEmail.setToolTip("Cancel", "Go back to Inbox");
        layoutViewEmail.addComponent(btnCancelViewEmail);

        textMessage = new Text("Hallo", 5, 54, 230);
        textMessage.setShadow(false);
        layoutViewEmail.addComponent(textMessage);

        btnSaveAttachment = new Button(219, 3, ENDER_MAIL_ICONS, 80, 0, 10, 10);
        btnSaveAttachment.setToolTip("Save Attachment", "Save the file attached to this email");
        btnSaveAttachment.setVisible(false);
        btnSaveAttachment.setClickListener((mouseX, mouseY, mouseButton) -> {
            if (mouseButton == 0 && attachedFile != null) {
                Dialog.SaveFile dialog = new Dialog.SaveFile(this, attachedFile);
                openDialog(dialog);
            }
        });
        layoutViewEmail.addComponent(btnSaveAttachment);

        labelAttachmentName = new Label("", 200, 7);
        labelAttachmentName.setVisible(false);
        labelAttachmentName.setAlignment(Component.ALIGN_RIGHT);
        layoutViewEmail.addComponent(labelAttachmentName);

        this.setCurrentLayout(layoutInit);

        TaskCheckEmailAccount taskCheckAccount = new TaskCheckEmailAccount();
        taskCheckAccount.setCallback((nbt, success) -> {
            if (success) {
                currentName = Objects.requireNonNull(nbt, "Callback has no nbt attached").getString("Name");
                listEmails.removeAll();
                for (Email email : EmailManager.INSTANCE.getInbox()) {
                    listEmails.addItem(email);
                }
                setCurrentLayout(layoutInbox);
            } else {
                btnRegisterAccount.setVisible(true);
                setCurrentLayout(layoutMainMenu);
            }
        });
        TaskManager.sendTask(taskCheckAccount);
    }

    private void resetAttachedFile() {
        if (attachedFile != null) {
            labelAttachedFile.setText("No file attached");
            labelAttachedFile.left -= 16;
            labelAttachedFile.xPosition -= 16;
            btnRemoveAttachedFile.setVisible(false);
            btnAttachedFile.setVisible(true);
            attachedFile = null;
        }
    }

    @Override
    public void load(CompoundTag tagCompound) {

    }

    @Override
    public void save(CompoundTag tagCompound) {
        // Save emails
        ListTag emailsTag = new ListTag();
        for (Email email : listEmails) {
            CompoundTag emailTag = new CompoundTag();
            email.save(emailTag);
            emailsTag.add(emailTag);
        }
        tagCompound.put("emails", emailsTag);

        // Save contacts
        ListTag contactsTag = new ListTag();
        for (Contact contact : listContacts) {
            CompoundTag contactTag = new CompoundTag();
            contact.save(contactTag);
            contactsTag.add(contactTag);
        }
        tagCompound.put("contacts", contactsTag);
    }

    @Override
    public String getWindowTitle() {
        if (getCurrentLayout() == layoutInbox) {
            return "Inbox: " + currentName + "@endermail.official";
        }
        if (getCurrentLayout() == layoutContacts) {
            return "Contacts";
        }
        return info.getName();
    }

    @Override
    public void onClose() {
        super.onClose();
        attachedFile = null;
    }
}
