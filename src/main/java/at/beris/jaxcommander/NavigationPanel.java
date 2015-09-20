/*
 * This file is part of JaxCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jaxcommander;

import org.apache.log4j.Logger;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.border.TitledBorder;
import javax.swing.table.TableModel;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.List;

public class NavigationPanel extends JPanel {
    private final static Logger LOGGER = Logger.getLogger(NavigationPanel.class.getName());

    JComboBox<Path> driveComboBox;
    JTextField currentPathTextField;
    JTable fileTable;
    Path currentPath;

    FileTableMouseListener fileTableMouseListener;
    WatchService watchService;
    WatchKey watchKey;
    Timer timer;

    public NavigationPanel() {
        try {
            watchService = FileSystems.getDefault().newWatchService();
        } catch (IOException e) {
            Application.logException(e);
        }


        GridBagLayout gridBagLayout = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        setLayout(gridBagLayout);

        setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
                "Table Title",
                TitledBorder.CENTER,
                TitledBorder.TOP));

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.NORTHWEST;

        driveComboBox = createDriveComboBox();

        add(driveComboBox, c);

        Path currentRootDirectory = (Path) driveComboBox.getSelectedItem();
        currentPath = currentRootDirectory;
        try {
            watchKey = currentPath.register(watchService, StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);
        } catch (IOException e) {
            Application.logException(e);
        }

        c.gridy++;
        currentPathTextField = createCurrentPathTextField(currentPath);
        add(currentPathTextField, c);

        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        c.weighty = 1;
        c.gridy++;

        fileTable = createFileTable(currentPath);

        add(new JScrollPane(fileTable), c);

        timer = new Timer(1000, new PeriodicalTask());
        timer.start();
    }

    private JComboBox createDriveComboBox() {
        JComboBox<Path> comboBox = new JComboBox();

        for (Path path : FileSystems.getDefault().getRootDirectories()) {
            comboBox.addItem(path);
        }

        return comboBox;
    }

    private JTextField createCurrentPathTextField(Path currentPath) {
        JTextField textField = new JTextField();
        textField.setText(currentPath.toString());
        return textField;
    }

    private JTable createFileTable(Path currentPath) {
        fileTableMouseListener = new FileTableMouseListener();
        TableModel tableModel = new PathTableModel(currentPath);
        JTable table = new JTable(tableModel);

        table.addMouseListener(fileTableMouseListener);

        return table;
    }

    private class PeriodicalTask implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            LOGGER.info(Thread.currentThread().getName() + " PeriodicalTask");
            if (watchKey != null) {
                List<WatchEvent<?>> watchEvents = watchKey.pollEvents();
                if (watchEvents.size() > 0) {
                    refreshDirectory();
                }
                watchKey.reset();
            }

        }
    }

    private class FileTableMouseListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() == 2) {
                JTable table = (JTable) e.getSource();
                int row = table.getSelectedRow();
                LOGGER.info("Double Clicked on row with index " + row);

                String fileName = (String) table.getModel().getValueAt(table.getSelectedRow(), 0);
                changeDirectory(fileName);
            }
        }
    }

    private void changeDirectory(String fileName) {
        watchKey.cancel();
        currentPath = new File(currentPath.toString(), fileName).toPath();

        try {
            watchKey = currentPath.register(watchService, StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);
        } catch (IOException e) {
            Application.logException(e);
        }


        currentPathTextField.setText(currentPath.toString());
        refreshDirectory();
    }

    private void refreshDirectory() {
        ((PathTableModel) fileTable.getModel()).setPath(currentPath);
        repaint();
    }

    public void dispose() {
        LOGGER.info("dispose");
        timer.stop();
        timer = null;
    }
}
