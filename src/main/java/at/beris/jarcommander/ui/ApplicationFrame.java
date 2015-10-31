/*
 * This file is part of JarCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jarcommander.ui;

import at.beris.jarcommander.ApplicationContext;
import at.beris.jarcommander.action.ActionType;
import at.beris.jarcommander.action.ActionFactory;
import at.beris.jarcommander.filesystem.LocalFileSystem;
import at.beris.jarcommander.ui.button.ButtonFactory;
import org.apache.log4j.Logger;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;

public class ApplicationFrame extends JFrame {
    private final static Logger LOGGER = Logger.getLogger(ApplicationFrame.class);
    private ApplicationContext context;
    private ActionFactory actionFactory;
    private ButtonFactory buttonFactory;
    private UIFactory uiFactory;

    public ApplicationFrame(ApplicationContext context) {
        this.context = context;
        this.actionFactory = context.getActionFactory();
        this.buttonFactory = context.getButtonFactory();
        this.uiFactory = context.getUiFactory();

        setSize(1024, 768);
        setLocationRelativeTo(null);
        setTitle("Jar Commander");

        setFocusable(true);
        setPreferredSize(new Dimension(1024, 768));
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        addWindowListener(new CustomWindowAdapter());

        GridBagLayout gridBagLayout = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        setLayout(gridBagLayout);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        add(createHeaderPanel(), c);

        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        c.weighty = 1;
        c.gridy++;

        add(uiFactory.createSessionsPanel(), c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0;
        c.weighty = 0;
        c.gridy++;
        JPanel footerPanel = createFooterPanel();
        add(footerPanel, c);

        pack();
        ((SessionPanel)context.getSessionsPanel().getSelectedComponent()).getSelectedNavigationPanel().getFileTablePane().getTable().requestFocusInWindow();
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel();
        GridLayout layout = new GridLayout(2, 1);
        panel.setLayout(layout);

        panel.add(createMenuBar());
        panel.add(createToolBar());
        return panel;
    }

    private JToolBar createToolBar() {
        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);

        JButton buttonRefresh = buttonFactory.createIconButton(ActionType.REFRESH, new ImageIcon(this.getClass().getClassLoader().getResource("images/arrow_refresh.png")));
        JButton buttonServerManager = buttonFactory.createIconButton(ActionType.SHOW_SITE_DIALOG, new ImageIcon(this.getClass().getClassLoader().getResource("images/server_connect.png")));

        toolbar.add(buttonRefresh);
        toolbar.add(buttonServerManager);
        return toolbar;
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

        JPanel panel = new JPanel();

        panel.setMaximumSize(new Dimension(10000, 40));
        panel.setPreferredSize(new Dimension(600, 40));
        panel.setMinimumSize(new Dimension(1, 40));

        panel.setLayout(gridLayout);
        panel.add(buttonFactory.createButton(ActionType.COPY));
        panel.add(buttonFactory.createButton(ActionType.MOVE));
        panel.add(buttonFactory.createButton(ActionType.MAKE_DIR));
        panel.add(buttonFactory.createButton(ActionType.DELETE));
        panel.add(buttonFactory.createButton(ActionType.RENAME));

        return panel;
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
        menuItem.setAction(actionFactory.getAction(ActionType.QUIT));
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
        menuItem.setAction(actionFactory.getAction(ActionType.SHOW_ABOUT_DIALOG));
//        menuItem.getAccessibleContext().setAccessibleDescription(
//                "This doesn't really do anything");

        fileMenu.add(menuItem);

        return fileMenu;
    }

    public void quit() {
        if (JOptionPane.showConfirmDialog(null,
                "Are you sure to quit?", "Quit Jar Commander",
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
        sb.append(": " + throwable.getMessage());

        for (StackTraceElement element : throwable.getStackTrace()) {
            sb.append(System.lineSeparator());
            sb.append(element.toString());
        }

        LOGGER.warn(sb.toString());
    }
}
