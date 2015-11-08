/*
 * This file is part of JarCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jarcommander.exception;

import javax.swing.JOptionPane;

public abstract class ApplicationException extends RuntimeException {
    public ApplicationException(String message, Throwable cause) {
        super(message, cause);
    }

    public void show() {
        JOptionPane.showMessageDialog(null, getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}
