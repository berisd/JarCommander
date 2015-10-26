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
import at.beris.jarcommander.filesystem.SshContext;
import at.beris.jarcommander.filesystem.SshFileSystem;
import at.beris.jarcommander.helper.ModelViewController;
import at.beris.jarcommander.model.SiteListModel;
import at.beris.jarcommander.model.SiteModel;
import at.beris.jarcommander.ui.list.SiteCellRenderer;
import org.apache.log4j.Logger;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.nio.file.Files;

import static at.beris.jarcommander.Application.logException;
import static at.beris.jarcommander.ApplicationContext.HOME_DIRECTORY;

public class SiteManagerDialog extends JDialog {
    private final static Logger LOGGER = Logger.getLogger(SiteManagerDialog.class);

    private SiteModel currentSite;
    private ModelViewController modelViewcontroller;


    private JList<SiteModel> siteList;
    private JTextField siteProtocol;
    private JTextField siteHostname;
    private JTextField sitePortNumber;
    private JTextField siteUsername;
    private JPasswordField sitePassword;

    private SiteListModel siteListModel;

    public SiteManagerDialog(Frame owner, boolean modal) {
        super(owner, modal);

        currentSite = createSiteModel();
        loadSiteDataList();


        createComponents();
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

        siteList.setSelectedIndex(0);

        pack();
    }

    private void selectSite(int listItemIndex) {
        currentSite.setProtocol(siteList.getModel().getElementAt(listItemIndex).getProtocol());
        currentSite.setHostname(siteList.getModel().getElementAt(listItemIndex).getHostname());
        currentSite.setPortNumber(siteList.getModel().getElementAt(listItemIndex).getPortNumber());
        currentSite.setUsername(siteList.getModel().getElementAt(listItemIndex).getUsername());
        currentSite.setPassword(siteList.getModel().getElementAt(listItemIndex).getPassword());
    }

    private void updateSiteModel(int listItemIndex) {
        siteList.getModel().getElementAt(listItemIndex).setProtocol(currentSite.getProtocol());
        siteList.getModel().getElementAt(listItemIndex).setHostname(currentSite.getHostname());
        siteList.getModel().getElementAt(listItemIndex).setPortNumber(currentSite.getPortNumber());
        siteList.getModel().getElementAt(listItemIndex).setUsername(currentSite.getUsername());
        siteList.getModel().getElementAt(listItemIndex).setPassword(currentSite.getPassword());
    }


    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel();

        JButton buttonNew = new JButton("New");
        buttonNew.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addSite();
            }
        });
        footerPanel.add(buttonNew);

        JButton buttonDelete = new JButton("Delete");
        footerPanel.add(buttonDelete);

        JButton buttonSave = new JButton("Save");
        buttonSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateSiteModel(siteList.getSelectedIndex());
                saveSiteDataList();
            }
        });
        footerPanel.add(buttonSave);

        JButton buttonConnect = new JButton("Connect");
        buttonConnect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                connectToSite();
                SiteManagerDialog.this.dispose();
            }
        });
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

    private void addSite() {
        SiteModel site = createSiteModel();
        siteListModel.addElement(site);
        siteList.setSelectedIndex(siteListModel.getSize() - 1);
    }

    private SiteModel createSiteModel() {
        SiteModel site = new SiteModel();
        site.setProtocol("SSH");
        return site;
    }

    private void connectToSite() {
        SshContext context = new SshContext();
        context.setHost(currentSite.getHostname());
        context.setPort(currentSite.getPortNumber());
        context.setUsername(currentSite.getUsername());
        context.setPassword(String.valueOf(currentSite.getPassword()));

        SessionPanel sessionPanel = ApplicationContext.createSessionPanel(new SshFileSystem(context));
        JTabbedPane sessionsPanel = ApplicationContext.getSessionsPanel();
        sessionsPanel.addTab(currentSite.getHostname(), sessionPanel);
        sessionsPanel.setSelectedIndex(sessionsPanel.getTabCount() - 1);
    }

    private JPanel createContentPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(1, 2));
        siteList = createSiteList();
        JScrollPane scrollPane = new JScrollPane(siteList);
        scrollPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Site List"));

        panel.add(scrollPane);
        panel.add(createSiteDetailPanel());

        return panel;
    }

    private JList createSiteList() {
        siteList = new JList(siteListModel);
        siteList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        siteList.setCellRenderer(new SiteCellRenderer());
        siteList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                LOGGER.debug("valueChanged");
                JList list = (JList) e.getSource();
                selectSite(list.getSelectedIndex());
            }
        });
        return siteList;
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

    private void createSiteListModel() {
        siteListModel = new SiteListModel();
    }

    private String getSiteListFileName() {
        return HOME_DIRECTORY + File.separator + "sites.xml";
    }

    private void saveSiteDataList() {
        File sitesFile = new File(getSiteListFileName());
        JAXBContext jaxbContext = null;
        try {
            jaxbContext = JAXBContext.newInstance(SiteListModel.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            jaxbMarshaller.marshal(siteListModel, sitesFile);
        } catch (JAXBException e) {
            logException(e);
        }
    }

    private void loadSiteDataList() {
        File sitesFile = new File(getSiteListFileName());
        if (!Files.exists(sitesFile.toPath())) {
            createSiteListModel();
            sitesFile.getParentFile().mkdirs();
            saveSiteDataList();
        } else {
            JAXBContext jaxbContext = null;
            try {
                jaxbContext = JAXBContext.newInstance(SiteListModel.class);
                Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
                siteListModel = (SiteListModel) jaxbUnmarshaller.unmarshal(sitesFile);
            } catch (JAXBException e) {
                logException(e);
            }
        }
    }

    private void createComponents() {
        siteProtocol = new JTextField();
        siteProtocol.setEditable(false);
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
