/*
 * This file is part of JaxCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jaxcommander.ui;

import at.beris.jaxcommander.Protocol;
import at.beris.jaxcommander.helper.ModelViewController;
import at.beris.jaxcommander.model.SiteModel;
import at.beris.jaxcommander.ui.list.SiteCellRenderer;
import at.beris.jaxcommander.ui.list.SiteListModel;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import static at.beris.jaxcommander.Application.logException;

public class SiteManagerDialog extends JDialog implements PropertyChangeListener {
    private final static Logger LOGGER = Logger.getLogger(SiteManagerDialog.class);

    private SiteModel currentSite;
    private ModelViewController controller;

    private JTextField siteProtocol;
    private JTextField siteHostname;
    private JTextField sitePortNumber;
    private JTextField siteUsername;
    private JTextField sitePassword;

    private List<SiteModel> siteModelList;

    public SiteManagerDialog(Frame owner, boolean modal) {
        super(owner, modal);



        setSize(640, 480);
        setLocationRelativeTo(null);
        setTitle("Site Manager");

        JPanel panel = (JPanel) this.getContentPane();
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        GridBagLayout gridBagLayout = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        setLayout(gridBagLayout);

        c.gridx = 0;
        c.gridy = 0;
        add(createContentPanel(), c);

        c.gridy++;
        add(createFooterPanel(), c);

        controller = new ModelViewController(currentSite, this);
        pack();
    }

    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel();

        JButton buttonNew = new JButton("New");
        footerPanel.add(buttonNew);

        JButton buttonDelete = new JButton("Delete");
        footerPanel.add(buttonDelete);

        JButton buttonSave = new JButton("Save");
        footerPanel.add(buttonSave);

        JButton buttonConnect = new JButton("Connect");
        footerPanel.add(buttonConnect);

        JButton buttonClose = new JButton("Close");
        buttonClose.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SiteManagerDialog.this.dispose();
            }
        });
        footerPanel.add(buttonClose);

        return footerPanel;
    }

    private JPanel createContentPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(1, 2));
        JList siteList = createSiteList();
        JScrollPane scrollPane = new JScrollPane(siteList);
        scrollPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Site List"));

        panel.add(scrollPane);
        panel.add(createSiteDetailPanel());

        siteList.setModel(new SiteListModel(createSiteDataList()));
        siteList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        siteList.setSelectedIndex(0);

        return panel;
    }

    private JList createSiteList() {
        JList list = new JList();
        list.setCellRenderer(new SiteCellRenderer());
        return list;
    }

    private JPanel createSiteDetailPanel() {
        JPanel panel = new JPanel();

        panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Details"));

        GridBagLayout gridBagLayout = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        panel.setLayout(gridBagLayout);

        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 0;
        JLabel labelFileProtocol = new JLabel("File protocol:");
        panel.add(labelFileProtocol, c);

        c.gridy++;
        siteProtocol = new JTextField();
        panel.add(siteProtocol, c);

        c.gridy++;
        JLabel labelHostname = new JLabel("Host name:");
        panel.add(labelHostname, c);

        c.gridx++;
        JLabel labelPortNumber = new JLabel("Port Number:");
        panel.add(labelPortNumber, c);

        c.gridx = 0;
        c.gridy++;
        siteHostname = new JTextField();
        panel.add(siteHostname, c);

        c.gridx++;
        sitePortNumber = new JTextField();
        panel.add(sitePortNumber, c);

        c.gridx = 0;
        c.gridy++;
        JLabel labelUsername = new JLabel("User name:");
        panel.add(labelUsername, c);

        c.gridx++;
        JLabel labelPassword = new JLabel("Password:");
        panel.add(labelPassword, c);

        c.gridx = 0;
        c.gridy++;
        siteUsername = new JTextField();
        panel.add(siteUsername, c);

        c.gridx++;
        sitePassword = new JTextField();
        panel.add(sitePassword, c);

        return panel;
    }

    private class TextFieldDocumentListener implements DocumentListener {
        @Override
        public void insertUpdate(DocumentEvent e) {
            LOGGER.info("insertUpdate");
            if (currentSite == null)
                return;
            try {
                final String newText = e.getDocument().getText(0, e.getDocument().getLength());
                String oldText = currentSite.getHostname();

                if (!StringUtils.equals(oldText, newText)) {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            currentSite.setHostname(newText);
                        }
                    });
                }
            } catch (BadLocationException ex) {
                logException(ex);
            }
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            LOGGER.info("removeUpdate");
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            LOGGER.info("changedUpdate");
        }
    }

    private List<SiteModel> createSiteDataList() {
        siteModelList = new ArrayList<>();

        SiteModel siteModel = new SiteModel();
        siteModel.setHostname("www.beris.at");
        siteModel.setPortNumber(22);
        siteModel.setProtocol(Protocol.SSH.toString());
        siteModel.setUsername("briedl");
        siteModel.setPassword("mypassword");

        currentSite = siteModel;
        siteModelList.add(siteModel);
        return siteModelList;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        LOGGER.debug("propertyChange");
        if (evt.getSource() instanceof SiteModel) {
            if (StringUtils.equalsIgnoreCase(evt.getPropertyName(), "sethostname")) {
                String newValue = (String) evt.getNewValue();
                String oldValue = (String) evt.getOldValue();
                if (!StringUtils.equals(oldValue, newValue)) {
                    siteHostname.setText(newValue);
                }
            }
        }
    }

    public JTextField getSiteHostname() {
        return siteHostname;
    }

    public JTextField getSitePassword() {
        return sitePassword;
    }

    public JTextField getSitePortNumber() {
        return sitePortNumber;
    }

    public JTextField getSiteProtocol() {
        return siteProtocol;
    }

    public JTextField getSiteUsername() {
        return siteUsername;
    }
}
