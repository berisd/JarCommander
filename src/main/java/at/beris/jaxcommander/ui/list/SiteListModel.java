/*
 * This file is part of JaxCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jaxcommander.ui.list;

import at.beris.jaxcommander.model.SiteModel;

import javax.swing.AbstractListModel;
import java.util.List;

public class SiteListModel extends AbstractListModel<SiteModel> {
    private List<SiteModel> siteModelList;

    public SiteListModel(List<SiteModel> siteModelList) {
        this.siteModelList = siteModelList;
    }

    @Override
    public int getSize() {
        return siteModelList.size();
    }

    @Override
    public SiteModel getElementAt(int index) {
        return siteModelList.get(0);
    }
}
