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
package org.clueminer.exception;

import java.io.File;

/**
 * Error thrown when data source does not follow expected syntax/format.
 *
 * @author deric
 */
public class ParserError extends Exception {

    private int lineNum = -1;
    private File file = null;

    public ParserError(String message) {
        super(message);
    }

    public ParserError(Throwable t) {
        super(t);
    }

    public ParserError(String message, Throwable t) {
        super(message, t);
    }

    public ParserError(String message, int lineNum) {
        super(message);
        this.lineNum = lineNum;
    }

    public ParserError(String message, File file, int lineNum) {
        super(message);
        this.lineNum = lineNum;
        this.file = file;
    }

    public String getMessage() {
        if (lineNum > -1) {
            return super.getMessage();
        } else {
            return "line " + lineNum + ": " + super.getMessage();
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Parsing Error: ");
        if (file != null) {
            sb.append(file.getName()).append(", ");
        }
        sb.append("line ").append(lineNum).append(getMessage());
        return sb.toString();
    }

}
