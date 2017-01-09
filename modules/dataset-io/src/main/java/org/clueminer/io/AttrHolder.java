/*
 * Copyright (C) 2011-2017 clueminer.org
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
package org.clueminer.io;

import java.util.regex.Pattern;

/**
 *
 * @author Tomas Barton
 */
public class AttrHolder {

    private String name;
    private String type;
    private String range;
    private String allowed;

    public static final Pattern integerSet = Pattern.compile("([\\d\\s,]+)", Pattern.CASE_INSENSITIVE);

    public AttrHolder() {

    }

    public AttrHolder(String name, String type, String range, String allowed) {
        this.name = name;
        setType(type);
        setRange(range);
        setAllowed(allowed);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        if (type == null) {
            if (allowed != null) {
                if (integerSet.matcher(allowed).matches()) {
                    setType("INTEGER");
                }
            } else if (range != null) {
                if (integerSet.matcher(range).matches()) {
                    setType("INTEGER");
                }
            } else {
                setType("REAL");
            }
        }
        return type;
    }

    public final void setType(String type) {
        if (type != null) {
            this.type = type.toUpperCase();
        }
    }

    public String getRange() {
        return range;
    }

    public final void setRange(String range) {
        this.range = range;
    }

    public String getAllowed() {
        return allowed;
    }

    public final void setAllowed(String allowed) {
        this.allowed = allowed;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("AttrHolder[");
        sb.append("name: ").append(getName()).append(", ")
                .append("type: ").append(getType()).append(", ");
        if (range != null && !range.isEmpty()) {
            sb.append("range: ").append(getRange());
        }
        if (allowed != null && !allowed.isEmpty()) {
            sb.append("allowed: ").append(getAllowed());
        }
        sb.append("]");
        return sb.toString();
    }

}
