/*
 * This file is part of JaxCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jaxcommander;

import at.beris.jaxcommander.action.ActionType;
import at.beris.jaxcommander.filesystem.DriveInfo;
import at.beris.jaxcommander.helper.ActionHelper;
import at.beris.jaxcommander.ui.SessionPanel;
import at.beris.jaxcommander.ui.button.ButtonFactory;
import org.apache.log4j.Logger;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Application extends JFrame implements Runnable {
    private final static Logger LOGGER = Logger.getLogger(Application.class);

    private static List<DriveInfo> driveInfoList = new ArrayList<>();

    private SessionPanel sessionPanel;

    public Application() {
        setSize(1024, 768);
        setLocationRelativeTo(null);
        setTitle("Jax Commander");

        setFocusable(true);
        setPreferredSize(new Dimension(1024, 768));
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        addWindowListener(new CustomWindowAdapter());

        GridBagLayout gridBagLayout = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        setLayout(gridBagLayout);

        c.fill = GridBagConstraints.NONE;
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0;
        c.weighty = 0;
        c.gridwidth = 1;
        c.anchor = GridBagConstraints.NORTHWEST;

        add(createHeaderPanel(), c);

        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        c.weighty = 1;
        c.gridy++;
        sessionPanel = new SessionPanel();
        add(sessionPanel, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.SOUTHWEST;
        c.weightx = 0;
        c.weighty = 0;
        c.gridy++;
        JPanel footerPanel = createFooterPanel();
        add(footerPanel, c);

        pack();
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel();
        GridLayout layout = new GridLayout(1, 1);

        panel.setBackground(Color.BLUE);
        panel.setLayout(layout);
        panel.setMaximumSize(new Dimension(10000, 30));
        panel.setPreferredSize(new Dimension(600, 30));
        panel.setMinimumSize(new Dimension(1, 30));

        panel.add(createMenuBar());

        return panel;
    }

    private JPanel createFooterPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(2, 1));

        panel.add(createCommandBar());
        panel.add(createStatusLine());
        return panel;
    }

    private JLabel createStatusLine() {
        JLabel label = new JLabel("Ready.");

        return label;
    }

    private JPanel createCommandBar() {
        GridLayout gridLayout = new GridLayout(1, 1);

        JPanel toolBar = new JPanel();

        toolBar.setMaximumSize(new Dimension(10000, 40));
        toolBar.setPreferredSize(new Dimension(600, 40));
        toolBar.setMinimumSize(new Dimension(1, 40));

        toolBar.setLayout(gridLayout);
        toolBar.add(ButtonFactory.createButton(ActionType.COPY));
        toolBar.add(ButtonFactory.createButton(ActionType.MOVE));
        toolBar.add(ButtonFactory.createButton(ActionType.MAKE_DIR));
        toolBar.add(ButtonFactory.createButton(ActionType.DELETE));
        toolBar.add(ButtonFactory.createButton(ActionType.RENAME));

        return toolBar;
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(createFileMenu());
        menuBar.add(createHelpMenu());

        return menuBar;
    }

    private JMenu createFileMenu() {
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        fileMenu.add(createMenuItemNew());
        fileMenu.add(createMenuItemShow());
        fileMenu.add(createMenuItemQuit());

        return fileMenu;
    }

    private JMenuItem createMenuItemShow() {
        JMenuItem menuItem = new JMenuItem("Show", KeyEvent.VK_S);
        return menuItem;
    }

    private JMenuItem createMenuItemQuit() {
        JMenuItem menuItem = new JMenuItem("Quit");
        menuItem.setAction(ActionHelper.getAction(ActionType.QUIT));
        return menuItem;
    }

    private JMenuItem createMenuItemNew() {
        JMenuItem menuItem = new JMenuItem("New",
                KeyEvent.VK_N);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_N, ActionEvent.SHIFT_MASK));
        //        menuItem.getAccessibleContext().setAccessibleDescription(
//                "This doesn't really do anything");
        return menuItem;
    }

    private JMenu createHelpMenu() {
        JMenu fileMenu = new JMenu("Help");
        fileMenu.setMnemonic(KeyEvent.VK_H);
//        fileMenu.getAccessibleContext().setAccessibleDescription(
//                "The only menu in this program that has menu items");

        JMenuItem menuItem = new JMenuItem("About");
        menuItem.setAction(ActionHelper.getAction(ActionType.SHOW_ABOUT_DIALOG));
//        menuItem.getAccessibleContext().setAccessibleDescription(
//                "This doesn't really do anything");

        fileMenu.add(menuItem);

        return fileMenu;
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Application());
    }

    public void run() {
        setVisible(true);
    }

    public void quit() {
        if (JOptionPane.showConfirmDialog(null,
                "Are you sure to quit?", "Quit Jax Commander",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    class CustomWindowAdapter extends WindowAdapter {
        @Override
        public void windowClosing(java.awt.event.WindowEvent windowEvent) {
            quit();
        }
    }

    public static void logException(Throwable throwable) {
        StringBuilder sb = new StringBuilder(throwable.getClass().getName());

        for (StackTraceElement element : throwable.getStackTrace()) {
            sb.append(System.lineSeparator());
            sb.append(element.toString());
        }

        LOGGER.warn(sb.toString());
    }

    public static List<DriveInfo> getDriveInfo() {
        if (driveInfoList.size() > 0)
            return driveInfoList;

        try {
            for (FileStore fileStore : FileSystems.getDefault().getFileStores()) {
//                long total = fileStore.getTotalSpace() / 1024;
//                long used = (fileStore.getTotalSpace() - fileStore.getUnallocatedSpace()) / 1024;
//                long avail = fileStore.getUsableSpace() / 1024;
//                System.out.format("%-20s %12d %12d %12d%n", fileStore, total, used, avail);

                String[] parts = fileStore.toString().split(" ");
                Path path = new File(parts[0]).toPath();

                DriveInfo driveInfo = new DriveInfo();
                driveInfo.setPath(path);

                driveInfo.setSpaceTotal(fileStore.getTotalSpace());
                driveInfo.setSpaceLeft(fileStore.getUsableSpace());


                driveInfoList.add(driveInfo);


            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        driveInfoList.sort(new Comparator<DriveInfo>() {
            @Override
            public int compare(DriveInfo o1, DriveInfo o2) {
                return o1.getPath().compareTo(o2.getPath());
            }
        });

        return driveInfoList;
    }

    public SessionPanel getSessionPanel() {
        return sessionPanel;
    }
}
