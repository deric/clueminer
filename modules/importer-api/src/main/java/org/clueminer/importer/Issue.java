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
package org.clueminer.importer;

/**
 * Issue are logged and classified by
 * <code>Report</code> to describe a problem encountered during import process.
 * Fill issues as <code>Exceptions</code>.
 *
 * @author Mathieu Bastian
 */
public final class Issue {

    public enum Level {

        INFO(100),
        WARNING(200),
        SEVERE(500),
        CRITICAL(1000);
        private final int levelInt;

        Level(int levelInt) {
            this.levelInt = levelInt;
        }

        public int toInteger() {
            return levelInt;
        }
    }
    private final Throwable throwable;
    private final String message;
    private final Level level;

    public Issue(Throwable throwable, Level level) {
        this.throwable = throwable;
        this.level = level;
        this.message = throwable.getMessage();
    }

    public Issue(String message, Level level, Throwable throwable) {
        this.throwable = throwable;
        this.level = level;
        this.message = message;
    }

    public Issue(String message, Level level) {
        this.message = message;
        this.level = level;
        this.throwable = null;
    }

    public String getMessage() {
        return message;
    }

    public Level getLevel() {
        return level;
    }

    public Throwable getThrowable() {
        return throwable;
    }
}
