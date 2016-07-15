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
package org.clueminer.explorer;

import java.awt.event.ActionEvent;
import org.clueminer.clustering.api.ClusterEvaluation;
import org.clueminer.clustering.api.ClusteringAlgorithm;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.evolution.api.Evolution;
import org.clueminer.utils.Props;

/**
 *
 * @author Tomas Barton
 * @param <E>
 */
public interface ToolbarListener<E extends Instance> {

    void evolutionAlgorithmChanged(ActionEvent evt);

    void startEvolution(ActionEvent evt, final Evolution alg);

    void evaluatorChanged(ClusterEvaluation eval);

    void runClustering(ClusteringAlgorithm alg, Dataset<E> dataset, Props props);

    /**
     *
     * @return currently active algorithm
     */
    Evolution currentEvolution();

    /**
     * Remove all found clusterings from explorer
     */
    void clearAll();

    /**
     *
     * @return current dataset
     */
    Dataset<E> getDataset();

}
