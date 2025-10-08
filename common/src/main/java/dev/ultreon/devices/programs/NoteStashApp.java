package dev.ultreon.devices.programs;

import dev.ultreon.devices.UltreonDevices;
import dev.ultreon.devices.api.app.Application;
import dev.ultreon.devices.api.app.Dialog;
import dev.ultreon.devices.api.app.Layout;
import dev.ultreon.devices.api.app.component.*;
import dev.ultreon.devices.api.io.Drive;
import dev.ultreon.devices.api.task.Callback;
import dev.ultreon.devices.core.io.FileSystem;
import dev.ultreon.devices.core.io.Path;
import dev.ultreon.devices.programs.system.component.FileInfo;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Unit;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.nio.charset.StandardCharsets;
import java.util.List;

@SuppressWarnings("FieldCanBeLocal")
public class NoteStashApp extends Application {
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
            notes.setLoading(true);
            UltreonDevices.LOGGER.debug(MARKER, "Loading notes...");
            FileSystem.getApplicationFolder(this, (response) -> {
                response.data().child("notes.json", (response2) -> {
                    if (response2.success()) {
                        FileInfo fileInfo = response2.data();
                        fileInfo.read((response3) -> {
                            if (response3.success()) {
                                byte[] data = response3.data();
                                List<String> lines = new String(data, StandardCharsets.UTF_8).lines().toList();
                                for (String line : lines) {
                                    String[] split = line.split(",", 2);
                                    if (split.length == 2) {
                                        notes.addItem(new Note(split[0], null, Path.of(split[1]), null));
                                    }
                                }

                                notes.setLoading(false);
                            }
                        });
                    }
                });
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
                note.getContent((content, success) -> {
                    if (success) {
                        noteContent.setText(content);
                    } else {
                        Dialog.Message message = new Dialog.Message("Failed to load note!");
                        message.setTitle("Error");
                        openDialog(message);
                    }
                });
                setCurrentLayout(layoutViewNote);
            } else {
                btnView.setEnabled(false);
            }
        });
        layoutMain.addComponent(btnView);

        btnDelete = new Button(124, 55, "Delete");
        btnDelete.setSize(50, 20);
        btnDelete.setEnabled(false);
        btnDelete.setClickListener((mouseX, mouseY, mouseButton) -> {
            if (notes.getSelectedIndex() != -1) {
                Note note = notes.getSelectedItem();
                assert note != null;
                Path file = note.getSource();
                Drive drive = note.drive;
                if (file != null) {
                    drive.delete(file, (response) -> {
                        if (response.success()) {
                            notes.removeItem(notes.getSelectedIndex());
                            btnView.setEnabled(false);
                            btnDelete.setEnabled(false);
                        } else {
                            Dialog.Message message = new Dialog.Message(response.message());
                            message.setTitle("I/O Error");
                            openDialog(message);
                        }
                    });
                } else {
                    //TODO error dialog
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

            FileSystem.getApplicationFolder(this, (response) -> {
                if (!response.success()) {
                    UltreonDevices.LOGGER.error(MARKER, "Failed to get application folder: {}", response.message());
                    Dialog.Message message = new Dialog.Message("Failed to get app directory, report the log to the developers.");
                    openDialog(message);
                }
            });

            Dialog.SaveFile dialog;
            dialog = new Dialog.SaveFile(NoteStashApp.this);
            dialog.setFolder(getApplicationFolderPath());
            dialog.setResponseHandler((success, file) -> {
                FileInfo info = file.withExtension(".note");
                String text = title.getText() + "\0" + textArea.getText();
                info.write(text.getBytes(StandardCharsets.UTF_8), (response) -> {
                    if (response.success()) {
                        Note note = new Note(title.getText(), info, textArea.getText());
                        notes.addItem(note);
                        title.clear();
                        textArea.clear();
                        setCurrentLayout(layoutMain);
                    } else {
                        Dialog.Message message = new Dialog.Message("Failed to save note:\n" + response.message());
                        openDialog(message);
                    }
                });
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

    public boolean handleFile(FileInfo file, Callback<Unit> callback) {
        if (!file.getName().endsWith(".note")) return false;

        Note note = Note.fromFile(file);
        note.getContent((content, success) -> {
            if (!success) {
                callback.execute(null, false);
                return;
            }
            noteTitle.setText(note.getTitle());
            noteContent.setText(content);
            setCurrentLayout(layoutViewNote);
            callback.execute(Unit.INSTANCE, true);
        });

        return true;
    }

    private static class Note {
        private final String title;
        private FileInfo file;
        private Drive drive;
        private Path source;
        private final @Nullable String content;

        public Note(String title, Drive drive, Path source, @Nullable String content) {
            this.title = title;
            this.drive = drive;
            this.source = source;
            this.content = content;
        }

        public Note(String title, FileInfo file, String content) {
            this.title = title;
            this.file = file;
            this.content = content;
        }

        public static Note fromFile(FileInfo file) {
            return new Note(file.getName(), file, null);
        }

        public Path getSource() {
            return source;
        }

        public String getTitle() {
            return title;
        }

        public void getContent(Callback<String> callback) {
            if (content == null) {
                loadContent(callback);
                return;
            }

            callback.execute(content, true);
        }

        private void loadContent(Callback<String> callback) {
            drive.read(source, (response) -> {
                if (!response.success()) {
                    callback.execute("", false);
                    return;
                }
                callback.execute(new String(response.data(), StandardCharsets.UTF_8), true);
            });
        }

        @Override
        public String toString() {
            return title;
        }
    }
}
