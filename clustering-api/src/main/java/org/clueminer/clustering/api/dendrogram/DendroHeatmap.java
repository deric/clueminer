package org.clueminer.clustering.api.dendrogram;

import java.awt.Dimension;
import java.awt.image.BufferedImage;

/**
 *
 * @author Tomas Barton
 */
public interface DendroHeatmap {

    /**
     * Draws heatmap into an image
     *
     * @param size dimension of resulting image
     * @return
     */
    public BufferedImage drawData(Dimension size);

    /**
     * Data necessary for rendering a heatmap
     *
     * @param dendroData
     */
    public void setData(DendrogramMapping dendroData);

    /**
     * Should create new buffered image from scratch
     */
    public void resetCache();

}
