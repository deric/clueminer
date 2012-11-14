package org.clueminer.clustering.api.dendrogram;

import java.awt.Color;
import org.clueminer.gui.ColorGenerator;

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
     */
    public TreeCluster(int root, int firstElem, int lastElem) {
        this.root = root;
        this.firstElem = firstElem;
        this.lastElem = lastElem;
        this.size = (lastElem - firstElem) + 1;
        color = ColorGenerator.getRandomColor();
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
