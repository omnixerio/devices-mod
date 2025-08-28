import dev.ultreon.devices.core.Ext2FS;
import dev.ultreon.devices.core.FS;
import org.jnode.fs.ext2.Ext2Constants;
import org.jnode.fs.ext2.Ext2FileSystem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;

public class FileManager extends JFrame {
    private Ext2FS fs;
    private final JTable fileTable;
    private final DefaultTableModel tableModel;
    private final JTextField pathField;
    private Path currentDirectory;

    public FileManager(Path rootDirectory) {
        this.currentDirectory = rootDirectory;

        // UI Setup
        setTitle("Graphical File Manager");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Path Field
        JPanel pathPanel = new JPanel(new BorderLayout());
        pathField = new JTextField(rootDirectory.toString());
        JButton goButton = new JButton("Go");
        pathPanel.add(pathField, BorderLayout.CENTER);
        pathPanel.add(goButton, BorderLayout.EAST);

        // File Table
        String[] columnNames = {"Name", "Type", "Size", "Last Modified"};
        tableModel = new DefaultTableModel(columnNames, 0);
        fileTable = new JTable(tableModel);
        JScrollPane tableScroll = new JScrollPane(fileTable);

        // Buttons
        JPanel buttonPanel = new JPanel();
        JButton createFileButton = new JButton("Create File");
        JButton createFolderButton = new JButton("Create Folder");
        JButton deleteButton = new JButton("Delete");
        JButton renameButton = new JButton("Rename");
        JButton openButton = new JButton("Open");
        JButton newButton = new JButton("New");

        buttonPanel.add(createFileButton);
        buttonPanel.add(createFolderButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(renameButton);
        buttonPanel.add(openButton);
        buttonPanel.add(newButton);

        // Add components
        add(pathPanel, BorderLayout.NORTH);
        add(tableScroll, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Event Listeners
        goButton.addActionListener(e -> navigateTo(Path.of(pathField.getText())));
        createFileButton.addActionListener(e -> createFile());
        createFolderButton.addActionListener(e -> createFolder());
        deleteButton.addActionListener(e -> deleteSelected());
        renameButton.addActionListener(e -> renameSelected());
        openButton.addActionListener(e -> openFS());
        newButton.addActionListener(e -> newFS());
        fileTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {
                    int selectedRow = fileTable.getSelectedRow();
                    if (selectedRow != -1) {
                        String fileName = (String) tableModel.getValueAt(selectedRow, 0);
                        navigateTo(currentDirectory.resolve(fileName));
                    }
                } else if (e.getClickCount() == 1 && e.getButton() == MouseEvent.BUTTON3) {
                    int selectedRow = fileTable.getSelectedRow();
                    if (selectedRow != -1) {
                        String fileName = (String) tableModel.getValueAt(selectedRow, 0);
                        showContextMenu(e, fileName);
                    }
                }
            }

            private void showContextMenu(MouseEvent e, String fileName) {
                JPopupMenu menu = new JPopupMenu();
                JMenuItem renameItem = new JMenuItem("Rename");
                JMenuItem deleteItem = new JMenuItem("Delete");
                JMenuItem detailsItem = new JMenuItem("Details");
                menu.add(renameItem);
                menu.add(deleteItem);
                menu.addSeparator();
                menu.add(detailsItem);
                menu.show(fileTable, e.getX(), e.getY());
                renameItem.addActionListener(e1 -> renameFile(fileName));
                deleteItem.addActionListener(e1 -> deleteFile(fileName));
                detailsItem.addActionListener(e1 -> showFileDetails(fileName));
            }

            private void showFileDetails(String fileName) {
                Path filePath = currentDirectory.resolve(fileName);
                try {
                    boolean isFile = fs.isFile(filePath);
                    boolean isFolder = fs.isFolder(filePath);
                    long size = isFile ? fs.size(filePath) : 0;
                    long lastModified = fs.lastModified(filePath);

                    JOptionPane.showMessageDialog(FileManager.this,
                            "Name: " + fileName +
                            "\nType: " + (isFile ? "File" : isFolder ? "Folder" : "Unknown") +
                            "\nSize: " + size +
                            "\nLast Modified: " + lastModified,
                            "Details", JOptionPane.INFORMATION_MESSAGE);
                } catch (IOException ex) {
                    showError(ex);
                }
            }

            private void deleteFile(String fileName) {
                Path filePath = currentDirectory.resolve(fileName);
                try {
                    Files.delete(filePath);
                    updateFileList();
                } catch (IOException ex) {
                    showError(ex);
                }
            }

            private void renameFile(String fileName) {
                String newName = JOptionPane.showInputDialog(FileManager.this, "Enter new name:", fileName);
                if (newName != null && !newName.trim().isEmpty()) {
                    Path oldPath = currentDirectory.resolve(fileName);
                    Path newPath = currentDirectory.resolve(newName);
                    try {
                        Files.move(oldPath, newPath);
                        updateFileList();
                    } catch (IOException ex) {
                        showError(ex);
                    }
                }
            }
        });

        fileTable.setTransferHandler(new FileTransferHandler()); // Set custom transfer handler
        fileTable.setDragEnabled(true);

        setDropTarget(new DropTarget(fileTable, new DropTargetAdapter() {
            @Override
            public synchronized void drop(DropTargetDropEvent dtDrop) {
                try {
                    dtDrop.acceptDrop(DnDConstants.ACTION_COPY);
                    Transferable tr = dtDrop.getTransferable();
                    if (tr.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                        List<File> fileList = (List<File>) tr.getTransferData(DataFlavor.javaFileListFlavor);
                        for (File file : fileList) {
                            Path sourcePath = file.toPath();
                            fs.createFile(currentDirectory.resolve(sourcePath.getFileName()), Files.readAllBytes(sourcePath));
                        }
                        updateFileList();
                    }
                } catch (Exception ex) {
                    showError(ex);
                }
            }

            @Override
            public void dragEnter(DropTargetDragEvent dtde) {
                if (!dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                    dtde.rejectDrag();
                    return;
                }

                dtde.acceptDrag(DnDConstants.ACTION_COPY);
            }
        }));
    }

    private void openFS() {
        try {
            if (fs != null) fs.close();

            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                fs = (Ext2FS) FS.loadExt2(file.toPath());
                navigateTo(Path.of("/"));

                Ext2FileSystem fileSystem = (Ext2FileSystem)fs.getFileSystem();
                if (fileSystem.getSuperblock().getState() == Ext2Constants.EXT2_ERROR_FS) {
                    int i = JOptionPane.showConfirmDialog(null, "Filesystem has not been cleanly unmounted.\nAre you sure you want to open it?", "Warning", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
                    switch (i) {
                        case JOptionPane.YES_OPTION -> {
                            fs.close();

                            fs = (Ext2FS) FS.loadExt2Forced(file.toPath());
                            navigateTo(Path.of("/"));
                            fileSystem.getSuperblock().setState(Ext2Constants.EXT2_VALID_FS);
                            fileSystem.flush();
                        }
                        case JOptionPane.NO_OPTION -> {
                            fs.close();
                            fs = null;
                        }
                        default -> {

                        }
                    }
                }
            }
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void newFS() {
        try {
            if (fs != null) fs.close();

            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            int result = fileChooser.showSaveDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                Path path = file.toPath();
                if (Files.notExists(path)) {
                    Files.createFile(path);
                }

                long diskSize;
                while (true) {
                    String size = JOptionPane.showInputDialog(this, "Enter size in bytes:", "Size", JOptionPane.QUESTION_MESSAGE);
                    if (size == null) {
                        return;
                    }
                    try {
                        diskSize = Long.parseLong(size);
                        if (diskSize < 0) {
                            throw new NumberFormatException();
                        }
                        if (diskSize % 512 != 0) {
                            diskSize += 512 - diskSize % 512;
                        }
                        break;
                    } catch (NumberFormatException ex) {
                        JOptionPane.showConfirmDialog(this, "Invalid size! Do you want to try again?", "Error", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
                    }
                }
                fs = (Ext2FS) FS.formatExt2(path, diskSize);
                navigateTo(Path.of("/"));
            }
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void navigateTo(Path directory) {
        try {
            if (fs.isFolder(directory)) {
                currentDirectory = directory;
                pathField.setText(directory.toString());
                updateFileList();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid directory!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IOException ex) {
            showError(ex);
        }
    }

    private void updateFileList() {
        try {
            tableModel.setRowCount(0);
            Iterator<String> iterator = fs.listDirectory(currentDirectory);
            while (iterator.hasNext()) {
                String fileName = iterator.next();
                Path filePath = currentDirectory.resolve(fileName);
                String type = fs.isFolder(filePath) ? "Folder" : "File";
                long size = fs.isFile(filePath) ? fs.size(filePath) : 0;
                long lastModified = fs.lastModified(filePath);
                tableModel.addRow(new Object[]{fileName, type, size, lastModified});
            }
        } catch (IOException ex) {
            showError(ex);
        }
    }

    private void createFile() {
        String fileName = JOptionPane.showInputDialog(this, "Enter file name:");
        if (fileName != null && !fileName.trim().isEmpty()) {
            try {
                fs.createFile(currentDirectory.resolve(fileName), new byte[0]);
                updateFileList();
            } catch (IOException ex) {
                showError(ex);
            }
        }
    }

    private void createFolder() {
        String folderName = JOptionPane.showInputDialog(this, "Enter folder name:");
        if (folderName != null && !folderName.trim().isEmpty()) {
            try {
                fs.createDirectory(currentDirectory.resolve(folderName));
                updateFileList();
            } catch (IOException ex) {
                showError(ex);
            }
        }
    }

    private void deleteSelected() {
        int selectedRow = fileTable.getSelectedRow();
        if (selectedRow != -1) {
            String fileName = (String) tableModel.getValueAt(selectedRow, 0);
            Path filePath = currentDirectory.resolve(fileName);
            try {
                fs.delete(filePath);
                updateFileList();
            } catch (IOException ex) {
                showError(ex);
            }
        }
    }

    private void renameSelected() {
        int selectedRow = fileTable.getSelectedRow();
        if (selectedRow != -1) {
            String oldName = (String) tableModel.getValueAt(selectedRow, 0);
            String newName = JOptionPane.showInputDialog(this, "Enter new name:", oldName);
            if (newName != null && !newName.trim().isEmpty()) {
                Path oldPath = currentDirectory.resolve(oldName);
                try {
                    fs.rename(oldPath, newName);
                    updateFileList();
                } catch (IOException ex) {
                    showError(ex);
                }
            }
        }
    }

    private void showDetails() {
        int selectedRow = fileTable.getSelectedRow();
        if (selectedRow != -1) {
            String fileName = (String) tableModel.getValueAt(selectedRow, 0);
            Path filePath = currentDirectory.resolve(fileName);
            try {
                boolean isFile = fs.isFile(filePath);
                boolean isFolder = fs.isFolder(filePath);
                long size = isFile ? fs.size(filePath) : 0;
                long lastModified = fs.lastModified(filePath);

                JOptionPane.showMessageDialog(this,
                        "Name: " + fileName +
                        "\nType: " + (isFile ? "File" : isFolder ? "Folder" : "Unknown") +
                        "\nSize: " + size +
                        "\nLast Modified: " + lastModified,
                        "Details", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                showError(ex);
            }
        }
    }

    private void showError(Exception ex) {
        JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        ex.printStackTrace();
    }

    public static void main(String[] args) {
        try {
            SwingUtilities.invokeLater(() -> new FileManager(Path.of("/")).setVisible(true));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    private static class FileTransferable implements Transferable {
        private final File file;

        public FileTransferable(File file) {
            this.file = file;
        }

        @Override
        public DataFlavor[] getTransferDataFlavors() {
            return new DataFlavor[]{DataFlavor.javaFileListFlavor};
        }

        @Override
        public boolean isDataFlavorSupported(DataFlavor flavor) {
            return flavor.equals(DataFlavor.javaFileListFlavor);
        }

        @Override
        public Object getTransferData(DataFlavor flavor) {
            return List.of(file);
        }
    }

    private class FileTransferHandler extends TransferHandler {
        @Override
        protected Transferable createTransferable(JComponent c) {
            int selectedRow = fileTable.getSelectedRow();
            if (selectedRow != -1) {
                try {
                    String fileName = (String) tableModel.getValueAt(selectedRow, 0);
                    Path filePath = currentDirectory.resolve(fileName);
                    File tempFile = File.createTempFile(fileName, null);
                    tempFile.deleteOnExit();

                    try (InputStream in = fs.read(filePath);
                         OutputStream out = new FileOutputStream(tempFile)) {
                        byte[] buffer = new byte[8192];
                        int bytesRead;
                        while ((bytesRead = in.read(buffer)) != -1) {
                            out.write(buffer, 0, bytesRead);
                        }
                    }

                    return new FileTransferable(tempFile);
                } catch (IOException ex) {
                    showError(ex);
                }
            }
            return null;
        }

        @Override
        public int getSourceActions(JComponent c) {
            return COPY;
        }
    }
}