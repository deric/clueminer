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
package org.clueminer.projection;

import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.openide.util.lookup.ServiceProvider;

/**
 * Principal Component Analysis (PCA) proxy
 *
 * @author deric
 * @param <E>
 */
@ServiceProvider(service = Projection.class)
public class ProjectionPCA<E extends Instance> implements Projection<E> {

    private double[][] data;
    private static final String NAME = "PCA";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void initialize(Dataset<E> dataset, int targetDims) {
        PrincipalComponentAnalysis transform = new PrincipalComponentAnalysis();
        data = transform.pca(dataset.arrayCopy(), targetDims);
    }

    @Override
    public double[] transform(E instance) {
        if (hasData()) {
            return data[instance.getIndex()];
        } else {
            throw new RuntimeException("PCA data projection not available");
        }
    }

    public boolean hasData() {
        return data != null;
    }

}
