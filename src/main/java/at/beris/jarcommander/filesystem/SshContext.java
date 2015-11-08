/*
 * This file is part of JarCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jarcommander.filesystem;

import at.beris.jarcommander.Application;
import at.beris.jarcommander.exception.ApplicationException;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import static at.beris.jarcommander.Application.logException;

public class SshContext {
    private JSch jsch;
    private Session session;
    private Channel channel;
    private String username;
    private String password;
    private String host;
    private int port;

    public void init() {
        java.util.Properties config = new java.util.Properties();
        config.put("StrictHostKeyChecking", "no");

        jsch = new JSch();

        try {
            session = jsch.getSession(username, host, port);
            session.setConfig(config);
            session.setPassword(password);
        } catch (JSchException e) {
            logException(e);
        }
    }

    public Session getSession() {
        return session;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Channel getChannel() {
        return channel;
    }

    public void connect() {
        try {
            session.connect();
            channel = session.openChannel("sftp");
            channel.connect();
        } catch (JSchException e) {
            Application.logException(e);
            throw new ApplicationException(e);
        }
    }
}
