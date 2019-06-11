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
package org.clueminer.rank;

import java.util.HashMap;
import org.clueminer.clustering.api.Clustering;
import org.openide.util.lookup.ServiceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.clueminer.clustering.api.RankEvaluator;

/**
 * Spearman's rank correlation coefficient
 *
 * @see https://en.wikipedia.org/wiki/Spearman%27s_rank_correlation_coefficient
 *
 * @author deric
 */
@ServiceProvider(service = RankEvaluator.class)
public class Spearman implements RankEvaluator {

    private static final String NAME = "Spearman";
    private static final Logger LOG = LoggerFactory.getLogger(Spearman.class);

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public double correlation(Clustering[] ref, Clustering[] curr, HashMap<Integer, Integer> map) {
        int size = curr.length;
        double corr = 0.0;
        map.clear();
        for (int i = 0; i < size; i++) {
            if (ref[i] != null) {
                map.put(ref[i].getId(), i);
            } else {
                LOG.warn("missing clustering #{}", i);
            }
        }
        if (map.size() != ref.length) {
            LOG.warn("clustering IDs are not unique! Map contains {} elements,"
                    + " while reference solutions {}: {}",
                    map.size(), ref.length, map.keySet().toString());
            return Double.NaN;
        }

        int diff;
        for (int i = 0; i < size; i++) {
            //difference between reference and current ranking
            diff = i - map.get(curr[i].getId());
            corr += Math.pow(diff, 2);
        }

        return 1 - (6 * corr / (Math.pow(size, 3) - size));
    }

}
