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
package org.clueminer.partitioning.impl;

import java.util.ArrayList;
import java.util.LinkedList;
import org.clueminer.attributes.BasicAttrType;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.impl.ArrayDataset;
import org.clueminer.graph.api.Node;
import org.clueminer.partitioning.api.Bisection;

/**
 *
 * @author Tomas Bruna
 */
public class PartitioningTest<E extends Instance> {

    protected Bisection subject;

    protected Dataset<? extends Instance> twoDistinctNeighbors() {
        Dataset<Instance> data = new ArrayDataset<>(8, 2);
        data.attributeBuilder().create("x", BasicAttrType.NUMERIC);
        data.attributeBuilder().create("y", BasicAttrType.NUMERIC);
        data.builder().create(new double[]{1, 1});
        data.builder().create(new double[]{1, 2});
        data.builder().create(new double[]{2, 1});
        data.builder().create(new double[]{2, 2});
        data.builder().create(new double[]{5, 5});
        data.builder().create(new double[]{5, 6});
        data.builder().create(new double[]{6, 5});
        data.builder().create(new double[]{6, 6});
        return data;
    }

    protected Dataset<? extends Instance> twoDistinctNeighborsMixed() {
        Dataset<Instance> data = new ArrayDataset<>(4, 2);
        data.attributeBuilder().create("x", BasicAttrType.NUMERIC);
        data.attributeBuilder().create("y", BasicAttrType.NUMERIC);
        data.builder().create(new double[]{6, 6});
        data.builder().create(new double[]{2, 2});
        data.builder().create(new double[]{5, 5});
        data.builder().create(new double[]{1, 1});
        data.builder().create(new double[]{6, 5});
        data.builder().create(new double[]{2, 1});
        data.builder().create(new double[]{5, 6});
        //data.builder().create(new double[]{1, 2});
        return data;
    }

    protected Dataset<? extends Instance> KLFail() {
        Dataset<Instance> data = new ArrayDataset<>(4, 2);
        data.attributeBuilder().create("x", BasicAttrType.NUMERIC);
        data.attributeBuilder().create("y", BasicAttrType.NUMERIC);
        data.builder().create(new double[]{0, 0});
        data.builder().create(new double[]{2, 2});
        data.builder().create(new double[]{2.5, 3});
        data.builder().create(new double[]{4, 2.5});
        data.builder().create(new double[]{4.2, 3.3});
        data.builder().create(new double[]{2, 3.5});
        data.builder().create(new double[]{3.5, 3.7});
        data.builder().create(new double[]{4.9, 5});
        data.builder().create(new double[]{5.7, 5.9});
        data.builder().create(new double[]{6.9, 5.3});
        data.builder().create(new double[]{7.2, 6.5});
        //  data.builder().create(new double[]{6.5, 5.7});
        return data;
    }

    protected Dataset<? extends Instance> threeDistinctNeighbors() {
        Dataset<Instance> data = new ArrayDataset<>(4, 2);
        data.attributeBuilder().create("x", BasicAttrType.NUMERIC);
        data.attributeBuilder().create("y", BasicAttrType.NUMERIC);
        data.builder().create(new double[]{1, 6});
        data.builder().create(new double[]{1, 5});
        data.builder().create(new double[]{2, 4});
        data.builder().create(new double[]{1, 1});
        data.builder().create(new double[]{2, 0.5});
        data.builder().create(new double[]{2.2, 2.5});
        data.builder().create(new double[]{3.5, 4});
        data.builder().create(new double[]{4, 5});
        data.builder().create(new double[]{4.3, 4.2});
        data.builder().create(new double[]{6, 7});
        return data;
    }

    protected Dataset<? extends Instance> twoDistinctNeighbors2() {
        Dataset<Instance> data = new ArrayDataset<>(4, 2);
        data.attributeBuilder().create("x", BasicAttrType.NUMERIC);
        data.attributeBuilder().create("y", BasicAttrType.NUMERIC);
        data.builder().create(new double[]{1, 1});
        data.builder().create(new double[]{6, 6});
        data.builder().create(new double[]{6, 5});
        data.builder().create(new double[]{3, 3});
        data.builder().create(new double[]{4, 4});
        data.builder().create(new double[]{5, 6});
        data.builder().create(new double[]{2, 1});
        data.builder().create(new double[]{1, 2});
        data.builder().create(new double[]{4, 5});
        data.builder().create(new double[]{5, 4});
        data.builder().create(new double[]{0, 1});
        data.builder().create(new double[]{1, 0});
        data.builder().create(new double[]{1, 1.3});
        data.builder().create(new double[]{1.3, 1});
        data.builder().create(new double[]{0.3, 0.2});
        data.builder().create(new double[]{0.1, 0.1});
        data.builder().create(new double[]{9, 0.1});

        data.builder().create(new double[]{9, 9});
        data.builder().create(new double[]{8, 9});
        return data;
    }

    protected void printResult(ArrayList<LinkedList<Node>> result) {
        for (int i = 0; i < result.size(); i++) {
            System.out.print("Cluster " + i + ": ");
            for (Node node : result.get(i)) {
                System.out.print(node.getId() + ", ");
            }
            System.out.println("");
        }
    }

}
