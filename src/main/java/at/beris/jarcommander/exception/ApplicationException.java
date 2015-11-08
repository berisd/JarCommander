/*
 * This file is part of JarCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jarcommander.exception;

import com.jcraft.jsch.JSchException;

import javax.swing.JOptionPane;

public class ApplicationException extends RuntimeException {
    private String message;

    public ApplicationException(Throwable cause) {
        super(cause);
        this.message = cause.getMessage();

        if (cause instanceof JSchException && message.equals("Auth fail")) {
            this.message = "Authentication failed!";
        }
    }

    public void show() {
        JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
