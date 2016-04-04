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
package org.clueminer.chameleon.mo;

import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.MergeEvaluation;
import org.clueminer.dataset.api.Instance;
import org.clueminer.utils.PairValue;
import org.clueminer.utils.Props;

/**
 * A pair of objects evaluated by N objectives (N is typically between 2 and 5).
 *
 * @author deric
 */
public class MoPair<E extends Instance, C extends Cluster<E>> extends PairValue<C> implements Comparable<PairValue<C>> {

    private double[] objectives;
    private MergeEvaluation<E> eval;
    private Props props = new Props();

    public MoPair(C A, C B) {
        super(A, B);
        setNumObjectives(2);
    }

    public MoPair(C A, C B, int numObjectives, MergeEvaluation<E> eval) {
        super(A, B);
        setNumObjectives(numObjectives);
        this.eval = eval;
    }

    public MoPair(C A, C B, int numObjectives) {
        super(A, B);
        setNumObjectives(numObjectives);
    }

    private void setNumObjectives(int numObjectives) {
        objectives = new double[numObjectives];
    }

    public double getObjective(int i) {
        return objectives[i];
    }

    public void setObjective(int i, double value) {
        objectives[i] = value;
    }

    /**
     * Express multiple objectives by single value
     *
     * @return
     */
    @Override
    public double getValue() {
        double value = 0.0;
        for (int i = 0; i < objectives.length; i++) {
            value += objectives[i];
        }
        return value;
    }

    @Override
    public int compareTo(PairValue<C> o) {
        PairValue<C> e = (PairValue<C>) o;
        double sc1 = eval.score(this.A, this.B, props);
        double sc2 = eval.score(e.A, e.B, props);
        if (sc1 > sc2) {
            return eval.isMaximized() ? -1 : 1;
        } else if (sc1 < sc2) {
            return eval.isMaximized() ? 1 : -1;
        }
        return 0;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Pair{ ");
        for (int i = 0; i < objectives.length; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(i).append(":").append(objectives[i]);
        }
        //sb.append("A: ").append(A.toString()).append(", ");
        //sb.append("B: ").append(B.toString());
        sb.append("}");
        return sb.toString();
    }

}
