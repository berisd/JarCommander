/*
 * This file is part of JarCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jarcommander.exception;

public class AuthenticationException extends ApplicationException {
    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}
