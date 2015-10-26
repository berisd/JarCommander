/*
 * This file is part of JarCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jarcommander.model;

import javax.swing.AbstractListModel;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "sites")
@XmlAccessorType(XmlAccessType.FIELD)
public class SiteListModel extends AbstractListModel<SiteModel> {

    @XmlElement(name = "site")
    private List<SiteModel> sites = null;

    public SiteListModel() {
        this.sites = new ArrayList<>();
    }

    public List<SiteModel> getSites() {
        return sites;
    }

    public void setSites(List<SiteModel> sites) {
        this.sites = sites;
    }

    public void addElement(SiteModel site) {
        int lastIndex = sites.size();
        sites.add(site);
        fireIntervalAdded(this, lastIndex, lastIndex);
    }

    @Override
    public int getSize() {
        return sites.size();
    }

    @Override
    public SiteModel getElementAt(int index) {
        return sites.get(index);
    }
}
