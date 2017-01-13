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
import java.util.List;
import org.clueminer.dataset.api.Instance;

/**
 *
 * @author Tomas Bruna
 * @param <E>
 */
public class DClusterLeaf<E extends Instance> extends DLeaf {

    private List<E> data;

    public DClusterLeaf(int id, List<E> data) {
        super(id);
        this.data = data;
    }

    @Override
    protected void printNodeValue(OutputStreamWriter out) throws IOException {
        out.write("#" + getId());

        out.write(" - ");
        for (Instance instance : data) {
            out.write(instance.getName() + ", ");
        }

        out.write('\n');
    }

    @Override
    public int getIndex() {
        return data.get(0).getIndex();
    }

    public List<E> getInstances() {
        return data;
    }

    public void setInstances(List<E> data) {
        this.data = data;
    }

    @Override
    public boolean containsCluster() {
        return true;
    }

}
