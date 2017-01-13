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
package org.clueminer.hclust;

import java.io.IOException;
import java.io.OutputStreamWriter;
import org.clueminer.clustering.api.dendrogram.DendroLeaf;
import org.clueminer.clustering.api.dendrogram.DendroNode;
import org.clueminer.dataset.api.DataVector;

/**
 *
 * @author Tomas Barton
 * @param <T>
 */
public class DLeaf<T extends DataVector> extends DTreeNode implements DendroLeaf<T>, DendroNode {

    private T data;

    public DLeaf(int id) {
        super(id);
    }

    public DLeaf(int id, DataVector data) {
        super(id);
        this.data = (T) data;
    }

    @Override
    public boolean isLeaf() {
        return true;
    }

    @Override
    protected void printNodeValue(OutputStreamWriter out) throws IOException {
        out.write("#" + getId());
        if (data != null) {
            out.write(" - " + data.getName());
        }
        out.write('\n');
    }

    @Override
    public void setData(T data) {
        this.data = data;
    }

    @Override
    public int getIndex() {
        if (data != null) {
            return data.getIndex();
        }
        return -1;
    }

    @Override
    public T getData() {
        return data;
    }

    public boolean containsCluster() {
        return false;
    }

}
