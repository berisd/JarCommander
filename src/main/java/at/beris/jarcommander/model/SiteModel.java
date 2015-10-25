/*
 * This file is part of JaxCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jarcommander.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "employee")
@XmlAccessorType(XmlAccessType.FIELD)
public class SiteModel extends AbstractModel {
    private String protocol;
    private String hostname;
    private int portNumber;
    private String username;
    private char[] password;

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        String oldHostname = this.hostname;
        this.hostname = hostname;
        firePropertyChange("setHostname", oldHostname, hostname);
    }

    public char[] getPassword() {
        return password;
    }

    public void setPassword(char[] password) {
        char[] oldPassword = this.password;
        this.password = password;
        firePropertyChange("setPassword", oldPassword, password);
    }

    public int getPortNumber() {
        return portNumber;
    }

    public void setPortNumber(int portNumber) {
        int oldPortNumber = this.portNumber;
        this.portNumber = portNumber;
        firePropertyChange("setPortNumber", oldPortNumber, portNumber);
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        String oldProtocol = this.protocol;
        this.protocol = protocol;
        firePropertyChange("setProtocol", oldProtocol, protocol);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        String oldUsername = this.username;
        this.username = username;
        firePropertyChange("setUsername", oldUsername, username);
    }
}
