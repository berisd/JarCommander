/*
 * This file is part of JarCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jarcommander.filesystem.file.client;

import com.jcraft.jsch.*;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;

import static at.beris.jarcommander.Application.logException;

public class SftpClient implements IClient<SftpATTRS> {
    private JSch jsch;
    private Session session;
    private ChannelSftp sftpChannel;
    private String username;
    private String password;
    private String host;
    private int port;

    @Override
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

    @Override
    public String getHost() {
        return host;
    }

    @Override
    public void setHost(String host) {
        this.host = host;
    }

    @Override
    public int getPort() {
        return port;
    }

    @Override
    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public void connect() {
        try {
            session.connect();
            sftpChannel = (ChannelSftp) session.openChannel("sftp");
            sftpChannel.connect();
        } catch (JSchException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void disconnect() {
        if (sftpChannel != null)
            sftpChannel.disconnect();
        if (session != null)
            session.disconnect();
    }

    @Override
    public void delete(String path) {
        try {
            checkChannel();
            sftpChannel.rm(path);
        } catch (SftpException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void createFile(String path) {
        try {
            checkChannel();
            sftpChannel.put(new ByteArrayInputStream(new byte[]{}), path);
        } catch (SftpException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean exists(String path) {
        try {
            checkChannel();
            sftpChannel.stat(path);
            return true;
        } catch (SftpException e) {
            if (e.id == ChannelSftp.SSH_FX_NO_SUCH_FILE)
                return false;
            else
                throw new RuntimeException(e);
        }
    }

    @Override
    public void makeDirectory(String path) {
        try {
            checkChannel();
            sftpChannel.mkdir(path);
        } catch (SftpException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public InputStream getInputStream(String path) {
        try {
            checkChannel();
            return sftpChannel.get(path);
        } catch (SftpException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public OutputStream getOutputStream(String path) {
        try {
            checkChannel();
            return sftpChannel.put(path);
        } catch (SftpException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public SftpATTRS getFileInfo(String path) throws FileNotFoundException {
        try {
            checkChannel();
            return sftpChannel.stat(path);
        } catch (SftpException e) {
            if (e.id == ChannelSftp.SSH_FX_NO_SUCH_FILE)
                throw new FileNotFoundException(path);
            else
                throw new RuntimeException(e);
        }
    }

    private void checkChannel() {
        try {
            if (session == null)
                init();
            if (!session.isConnected())
                connect();
            if (sftpChannel.isClosed() || !sftpChannel.isConnected())
                sftpChannel.connect();
        } catch (JSchException e) {
            throw new RuntimeException(e);
        }
    }
}
