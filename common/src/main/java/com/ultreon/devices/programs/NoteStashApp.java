package com.ultreon.devices.programs;

import com.ultreon.devices.Devices;
import com.ultreon.devices.api.app.Application;
import com.ultreon.devices.api.app.Dialog;
import com.ultreon.devices.api.app.Layout;
import com.ultreon.devices.api.app.component.*;
import com.ultreon.devices.api.io.File;
import com.ultreon.devices.core.io.FileSystem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import org.jetbrains.annotations.Nullable;
import java.util.Objects;
import java.util.function.Predicate;

@SuppressWarnings("FieldCanBeLocal")
public class NoteStashApp extends Application {
    @SuppressWarnings("ConstantConditions")
    private static final Predicate<File> PREDICATE_FILE_NOTE = file -> !file.isFolder()
            && file.getData().contains("title", Tag.TAG_STRING)
            && file.getData().contains("content", Tag.TAG_STRING);
    private static final Marker MARKER = MarkerFactory.getMarker("Note Stash App");

    /* Main */
    private Layout layoutMain;
    private ItemList<Note> notes;
    private Button btnNew;
    private Button btnView;
    private Button btnDelete;

    /* Add Note */
    private Layout layoutAddNote;
    private TextField title;
    private TextArea textArea;
    private Button btnSave;
    private Button btnCancel;

    /* View Note */
    private Layout layoutViewNote;
    private Label noteTitle;
    private Text noteContent;
    private Button btnBack;

    public NoteStashApp() {
        //super("note_stash", "Note Stash");
    }

    @Override
    public void init(@Nullable CompoundTag intent) {
        /* Main */

        layoutMain = new Layout(180, 80);
        layoutMain.setInitListener(() -> {
            notes.getItems().clear();
            Devices.LOGGER.debug(MARKER, "Loading notes...");
            FileSystem.getApplicationFolder(this, (folder, success) -> {
                if (success) {
                    assert folder != null;
                    folder.search(file -> file.isForApplication(this)).forEach(file -> notes.addItem(Note.fromFile(file)));
                } else {
                    Devices.LOGGER.error(MARKER, "Failed to get application folder");
                    //TODO error dialog
                }
            });
        });

        notes = new ItemList<>(5, 5, 100, 5);
        notes.setItemClickListener((e, index, mouseButton) -> {
            btnView.setEnabled(true);
            btnDelete.setEnabled(true);
        });
        layoutMain.addComponent(notes);

        btnNew = new Button(124, 5, "New");
        btnNew.setSize(50, 20);
        btnNew.setClickListener((mouseX, mouseY, mouseButton) -> setCurrentLayout(layoutAddNote));
        layoutMain.addComponent(btnNew);

        btnView = new Button(124, 30, "View");
        btnView.setSize(50, 20);
        btnView.setEnabled(false);
        btnView.setClickListener((mouseX, mouseY, mouseButton) -> {
            if (notes.getSelectedIndex() != -1) {
                Note note = notes.getSelectedItem();
                assert note != null;
                noteTitle.setText(note.getTitle());
                noteContent.setText(note.getContent());
                setCurrentLayout(layoutViewNote);
            }
        });
        layoutMain.addComponent(btnView);

        btnDelete = new Button(124, 55, "Delete");
        btnDelete.setSize(50, 20);
        btnDelete.setEnabled(false);
        btnDelete.setClickListener((mouseX, mouseY, mouseButton) -> {
            if (notes.getSelectedIndex() != -1) {
                if (notes.getSelectedIndex() != -1) {
                    Note note = notes.getSelectedItem();
                    assert note != null;
                    File file = note.getSource();
                    if (file != null) {
                        file.delete((o, success) -> {
                            if (success) {
                                notes.removeItem(notes.getSelectedIndex());
                                btnView.setEnabled(false);
                                btnDelete.setEnabled(false);
                            } else {
                                //TODO error dialog
                            }
                        });
                    } else {
                        //TODO error dialog
                    }
                }
            }
        });
        layoutMain.addComponent(btnDelete);


        /* Add Note */

        layoutAddNote = new Layout(180, 80);

        title = new TextField(5, 5, 114);
        layoutAddNote.addComponent(title);

        textArea = new TextArea(5, 25, 114, 50);
        textArea.setFocused(true);
        textArea.setPadding(2);
        layoutAddNote.addComponent(textArea);

        btnSave = new Button(124, 5, "Save");
        btnSave.setSize(50, 20);
        btnSave.setClickListener((mouseX, mouseY, mouseButton) -> {
            CompoundTag data = new CompoundTag();
            data.putString("title", title.getText());
            data.putString("content", textArea.getText());

            FileSystem.getApplicationFolder(this, (folder, success) -> {
                if (success) {
                    assert folder != null;
                    folder.search(file -> file.isForApplication(this)).forEach(file -> notes.addItem(Note.fromFile(file)));
                } else {
                    Devices.LOGGER.error(MARKER, "Failed to get application folder");
                    //TODO error dialog
                }
            });

            Dialog.SaveFile dialog = new Dialog.SaveFile(NoteStashApp.this, data);
            dialog.setFolder(getApplicationFolderPath());
            dialog.setResponseHandler((success, file) -> {
                title.clear();
                textArea.clear();
                setCurrentLayout(layoutMain);
                return true;
            });
            openDialog(dialog);
        });
        layoutAddNote.addComponent(btnSave);

        btnCancel = new Button(124, 30, "Cancel");
        btnCancel.setSize(50, 20);
        btnCancel.setClickListener((mouseX, mouseY, mouseButton) -> {
            title.clear();
            textArea.clear();
            setCurrentLayout(layoutMain);
        });
        layoutAddNote.addComponent(btnCancel);


        /* View Note */

        layoutViewNote = new Layout(180, 80);

        noteTitle = new Label("", 5, 5);
        layoutViewNote.addComponent(noteTitle);

        noteContent = new Text("", 5, 18, 110);
        layoutViewNote.addComponent(noteContent);

        btnBack = new Button(124, 5, "Back");
        btnBack.setSize(50, 20);
        btnBack.setClickListener((mouseX, mouseY, mouseButton) -> setCurrentLayout(layoutMain));
        layoutViewNote.addComponent(btnBack);

        setCurrentLayout(layoutMain);
    }

    @Override
    public void load(CompoundTag tagCompound) {
    }

    @Override
    public void save(CompoundTag tagCompound) {
    }

    @Override
    public void onClose() {
        super.onClose();
        notes.removeAll();
    }

    @Override
    public boolean handleFile(File file) {
        if (!PREDICATE_FILE_NOTE.test(file)) return false;

        CompoundTag data = file.getData();
        assert data != null;
        noteTitle.setText(data.getString("title"));
        noteContent.setText(data.getString("content"));
        setCurrentLayout(layoutViewNote);
        return true;
    }

    private static class Note {
        private File source;
        private final String title;
        private final String content;

        public Note(String title, String content) {
            this.title = title;
            this.content = content;
        }

        public static Note fromFile(File file) {
            Note note = new Note(Objects.requireNonNull(file.getData(), "File data doesn't exist.").getString("title"), file.getData().getString("content"));
            note.source = file;
            return note;
        }

        public File getSource() {
            return source;
        }

        public String getTitle() {
            return title;
        }

        public String getContent() {
            return content;
        }

        @Override
        public String toString() {
            return title;
        }
    }
}
