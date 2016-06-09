/*
 * Copyright (C) 2011-2016 clueminer.org
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.clueminer.clustering.api.config;

/**
 *
 * @author Tomas Barton
 */
public class ConfigException extends RuntimeException {

    /**
     * Creates a new instance of <code>ConfigurationException</code> without
     * detail message.
     */
    public ConfigException() {
    }

    /**
     * Constructs an instance of <code>ConfigurationException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public ConfigException(String msg) {
        super(msg);
    }

    /**
     * Constructs an instance of <code>ConfigurationException</code> with the
     * specified detail message and cause
     *
     * @param message the detail message
     * @param cause   the cause of this exception
     */
    public ConfigException(String message, Throwable cause) {
        super(message, cause);
    }
}
