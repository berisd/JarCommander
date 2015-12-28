/*
 * This file is part of JarCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jarcommander.helper;

import at.beris.jarcommander.model.SiteListModel;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.nio.file.Files;

import static at.beris.jarcommander.Application.logException;

public class SiteManager {
    private String fileName;
    private SiteListModel siteListModel;

    public SiteManager(String fileName) {
        this.fileName = fileName;
        siteListModel = new SiteListModel();
    }

    public void load() {
        File sitesFile = new File(fileName);
        if (!Files.exists(sitesFile.toPath())) {
            sitesFile.getParentFile().mkdirs();
            save();
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

    public void save() {
        File sitesFile = new File(fileName);
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

    public SiteListModel getSiteListModel() {
        return siteListModel;
    }

    public void setSiteListModel(SiteListModel siteListModel) {
        this.siteListModel = siteListModel;
    }
}
