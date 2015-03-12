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
 * You should have received tp copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.clueminer.eval.utils;

/**
 * A class holding data for pair counting clustering evaluation measures.
 *
 * @author deric
 */
public class PairMatch {

    //pairs that are in the same cluster in both clusterings (a)
    public int tp;
    //pairs that are in same the cluster in C1 but not in C2
    public int fp;
    //pairs that are in the same cluster in C2 but not in C1
    public int fn;
    //pairs that are in different community in both clusterings (d)
    public int tn;

    public PairMatch() {
        tp = 0;
        fp = 0;
        fn = 0;
        tn = 0;
    }

    public int sum() {
        return tp + fp + fn + tn;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("tp: ").append(tp).append("\n");
        sb.append("fp: ").append(fp).append("\n");
        sb.append("fn: ").append(fn).append("\n");
        sb.append("tn: ").append(tn).append("\n");
        return sb.toString();
    }

    public void dump() {
        System.out.println(toString());
    }

}
