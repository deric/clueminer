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
package org.clueminer.clustering.api.dendrogram;

import java.awt.Color;

public class TreeCluster {

    public int root; // root index
    public int firstElem;
    public int lastElem;
    private Color color; // color of the cluster.
    private String text; // cluster description.
    public int size;

    /**
     * Constructs a cluster with specified root,
     * firstRow and lastRow indices.
     *
     * @param root
     * @param firstElem
     * @param lastElem
     * @param color
     */
    public TreeCluster(int root, int firstElem, int lastElem, Color color) {
        this.root = root;
        this.firstElem = firstElem;
        this.lastElem = lastElem;
        this.size = (lastElem - firstElem) + 1;
        this.color = color;
    }

    public void setFinalSize() {
        this.size = (this.lastElem - this.firstElem) + 1;
    }

    public void setRoot(int r) {
        this.root = r;
    }

    public void setFirstElem(int fe) {
        this.firstElem = fe;
        try {
            this.size = (lastElem = firstElem) + 1;
        } catch (NullPointerException npe) {
        }
    }

    public void setLastElem(int le) {
        this.lastElem = le;
        try {
            this.size = (lastElem = firstElem) + 1;
        } catch (NullPointerException npe) {
        }
    }

    public int getRoot() {
        return this.root;
    }

    public int getFirstElem() {
        return this.firstElem;
    }

    public int getLastElem() {
        return this.lastElem;
    }
    //End beanifying methods

    /**
     * @return the color
     */
    public Color getColor() {
        return color;
    }

    /**
     * @param color the color to set
     */
    public void setColor(Color color) {
        this.color = color;
    }

    /**
     * @return the text
     */
    public String getText() {
        return text;
    }

    /**
     * @param text the text to set
     */
    public void setText(String text) {
        this.text = text;
    }
}
