/*
 * Copyright (C) 2011-2018 clueminer.org
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
package org.clueminer.r.api;

/**
 *
 * @author deric
 */
public class RException extends Exception {

    protected RBackend engine;

    public RException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public RException(final RBackend rEngine, Throwable cause) {
        super(cause);
        this.engine = rEngine;
    }

    /**
     *
     * @param rEngine
     * @param message
     */
    public RException(final RBackend rEngine, final String message) {
        super(message);
        this.engine = rEngine;
    }

    public RException(RBackend engine, String msg, Throwable cause) {
        super(msg, cause);
        this.engine = engine;
    }

    public RBackend getEngine() {
        return engine;
    }

}
