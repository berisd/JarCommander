/*
 * This file is part of JaxCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jaxcommander;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;

public class Application extends JFrame implements Runnable {

    public Application() {
        setSize(1024, 768);
        setLocationRelativeTo(null);
        setTitle("JaxCommander");

        setFocusable(true);
        setPreferredSize(new Dimension(1024, 768));
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new CustomWindowAdapter());
        addKeyListener(new CustomKeyListener());

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
        add(createContentPanel(), c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.SOUTHWEST;
        c.weightx = 0;
        c.weighty = 0;
        c.gridy++;
        add(createFooterPanel(), c);

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

    private JPanel createContentPanel() {
        JPanel panel = new JPanel();
        GridLayout layout = new GridLayout(1, 2);

        panel.setLayout(layout);
        panel.add(new JaxTable());
        panel.add(new JaxTable());

        return panel;
    }

    private JPanel createFooterPanel() {
        JPanel panel = new JPanel();
        GridLayout footerLayout = new GridLayout(2, 1);
        panel.setLayout(footerLayout);

        panel.add(createCommandBar());
        panel.add(createStatusLine());
        return panel;
    }

    private JLabel createStatusLine() {
        JLabel label = new JLabel("Hier ist der Statusbar");

        return label;
    }

    private JPanel createCommandBar() {
        GridLayout gridLayout = new GridLayout(1, 1);

        JPanel toolBar = new JPanel();

        toolBar.setMaximumSize(new Dimension(10000, 40));
        toolBar.setPreferredSize(new Dimension(600, 40));
        toolBar.setMinimumSize(new Dimension(1, 40));

        toolBar.setLayout(gridLayout);
        toolBar.add(createButtonCopy());
        toolBar.add(createButtonMove());

        return toolBar;
    }

    private JButton createButtonCopy() {
        JButton button = new JButton("F5 Copy");

        button.setToolTipText("Copy");
        button.setMnemonic(KeyEvent.VK_F5);

        InputMap inputMap = button.getInputMap();
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0), "copy");
        ActionMap actionMap = button.getActionMap();

        AbstractAction actionCopy = new AbstractAction() {
            public void actionPerformed(ActionEvent ae) {
                new JOptionPane("Copy :)");
            }
        };

        actionMap.put("copy", actionCopy);
        button.addActionListener(actionCopy);

        return button;
    }

    private JButton createButtonMove() {
        JButton button = new JButton("F6 Move");

        button.setToolTipText("Move");
        button.setMnemonic(KeyEvent.VK_F6);

        InputMap inputMap = button.getInputMap();
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0), "move");
        ActionMap actionMap = button.getActionMap();

        AbstractAction actionCopy = new AbstractAction() {
            public void actionPerformed(ActionEvent ae) {
                new JOptionPane("Move :)");
            }
        };

        actionMap.put("move", actionCopy);
        button.addActionListener(actionCopy);

        return button;
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

        return fileMenu;
    }

    private JMenuItem createMenuItemShow() {
        JMenuItem menuItem = new JMenuItem("Show", KeyEvent.VK_S);
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

        JMenuItem menuItem = new JMenuItem("About",
                KeyEvent.VK_T);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_A, ActionEvent.ALT_MASK));
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
                "Are you sure to quit?", "Quit JXCommander",
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

    class CustomKeyListener extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            super.keyPressed(e);

            if (e.getKeyCode() == KeyEvent.VK_F5) {
//                JOptionPane.showMessageDialog(this., "Copy :)");
                System.out.println("F5");
            }


        }
    }

}
