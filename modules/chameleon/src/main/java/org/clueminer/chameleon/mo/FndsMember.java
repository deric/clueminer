/*
 * Copyright (C) 2011-2015 clueminer.org
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
package org.clueminer.chameleon.mo;

import java.util.HashSet;
import java.util.Iterator;

/**
 * Member of Fast non-dominated sort queue
 *
 * @author deric
 */
public class FndsMember<T> {

    private final T value;
    private final HashSet<FndsMember<T>> iDominate;
    private final HashSet<FndsMember<T>> dominatesMe;
    private int domCnt;

    public FndsMember(T value) {
        this.value = value;
        iDominate = new HashSet<>();
        dominatesMe = new HashSet<>();
        domCnt = 0;
    }

    public T getValue() {
        return value;
    }

    public void addIDominate(FndsMember<T> other) {
        iDominate.add(other);
        other.dominatesMe.add(this);
    }

    public void addDominatesMe(FndsMember<T> other) {
        dominatesMe.add(other);
        other.iDominate.add(this);
    }

    /**
     * Rank determines membership to a front
     *
     * @return
     */
    public int rank() {
        return dominatesMe.size();
    }

    public int domDiff() {
        return dominatesMe.size() - domCnt;
    }

    public void incDomCnt() {
        domCnt++;
    }

    /**
     * Remove this member from all other lists, thus lowering/increasing rank of
     * others
     */
    public void delete() {
        for (FndsMember<T> mem : iDominate) {
            mem.dominatesMe.remove(this);
            //TODO: move mem to higher front
        }

        for (FndsMember<T> mem : dominatesMe) {
            mem.iDominate.remove(this);
            //TODO: move mem to lower front
        }
    }

    public Iterator<FndsMember<T>> iterIDominate() {
        return iDominate.iterator();
    }

    public Iterator<FndsMember<T>> iterDominatesMe() {
        return dominatesMe.iterator();
    }

}
