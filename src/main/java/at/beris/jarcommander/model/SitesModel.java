/*
 * This file is part of JarCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jarcommander.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "sites")
@XmlAccessorType(XmlAccessType.FIELD)
public class SitesModel {

    @XmlElement(name = "site")
    private List<SiteModel> sites = null;

    public List<SiteModel> getSites() {
        return sites;
    }

    public void setSites(List<SiteModel> sites) {
        this.sites = sites;
    }
}
