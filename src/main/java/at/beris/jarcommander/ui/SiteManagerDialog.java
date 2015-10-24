/*
 * This file is part of JaxCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jarcommander.ui;

import at.beris.jarcommander.Protocol;
import at.beris.jarcommander.helper.ModelViewController;
import at.beris.jarcommander.model.SiteModel;
import at.beris.jarcommander.ui.list.SiteCellRenderer;
import at.beris.jarcommander.ui.list.SiteListModel;
import org.apache.log4j.Logger;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class SiteManagerDialog extends JDialog {
    private final static Logger LOGGER = Logger.getLogger(SiteManagerDialog.class);

    private SiteModel currentSite;
    private ModelViewController modelViewcontroller;

    private JTextField siteProtocol;
    private JTextField siteHostname;
    private JTextField sitePortNumber;
    private JTextField siteUsername;
    private JPasswordField sitePassword;

    private List<SiteModel> siteModelList;

    public SiteManagerDialog(Frame owner, boolean modal) {
        super(owner, modal);

        createComponents();
        currentSite = new SiteModel();

        modelViewcontroller = new ModelViewController(currentSite, this);
        registerMVCComponents();

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
        panel.add(siteProtocol, c);

        c.gridy++;
        JLabel labelHostname = new JLabel("Host name:");
        panel.add(labelHostname, c);

        c.gridx++;
        JLabel labelPortNumber = new JLabel("Port Number:");
        panel.add(labelPortNumber, c);

        c.gridx = 0;
        c.gridy++;
        panel.add(siteHostname, c);

        c.gridx++;
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
        panel.add(siteUsername, c);

        c.gridx++;
        panel.add(sitePassword, c);

        return panel;
    }

    private List<SiteModel> createSiteDataList() {
        siteModelList = new ArrayList<>();

        currentSite.setHostname("www.beris.at");
        currentSite.setPortNumber(22);
        currentSite.setProtocol(Protocol.SSH.toString());
        currentSite.setUsername("briedl");
        currentSite.setPassword("mypassword".toCharArray());

        siteModelList.add(currentSite);
        return siteModelList;
    }

    private void createComponents() {
        siteProtocol = new JTextField();
        siteHostname = new JTextField();
        sitePortNumber = new JTextField();
        siteUsername = new JTextField();
        sitePassword = new JPasswordField();
    }

    private void registerMVCComponents() {
        modelViewcontroller.registerObjectWithModelProperty(this.getSiteHostname(), "hostname");
        modelViewcontroller.registerObjectWithModelProperty(this.getSiteProtocol(), "protocol");
        modelViewcontroller.registerObjectWithModelProperty(this.getSitePortNumber(), "portNumber");
        modelViewcontroller.registerObjectWithModelProperty(this.getSiteUsername(), "username");
        modelViewcontroller.registerObjectWithModelProperty(this.getSitePassword(), "password");
    }

    public JTextField getSiteHostname() {
        return siteHostname;
    }

    public JPasswordField getSitePassword() {
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
