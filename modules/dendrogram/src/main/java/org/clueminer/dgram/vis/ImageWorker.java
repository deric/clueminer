package org.clueminer.dgram.vis;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import org.clueminer.clustering.api.dendrogram.DendrogramVisualizationListener;
import org.clueminer.dendrogram.gui.Heatmap;
import org.clueminer.dgram.eval.SilhouettePlot;
import org.openide.util.Exceptions;

/**
 * Worker has instances of GUI components needed for rendering. Only one image
 * could be rendered at the same time. Creating too many workers would consume
 * too much memory.
 *
 * @author Tomas Barton
 */
public class ImageWorker implements Runnable {

    private final Heatmap heatmap;
    private final SilhouettePlot silhoulette;
    private boolean running = true;
    private final ImageFactory factory;

    public ImageWorker(ImageFactory imgFactory) {
        heatmap = new Heatmap();
        silhoulette = new SilhouettePlot(true);
        this.factory = imgFactory;
    }

    public void generateImage(ImageTask task) {
        heatmap.setData(task.getMapping());
        silhoulette.setClustering(task.getMapping().getRowsResult(), task.getClustering());

        // 1px space
        int space = 1;
        int silWidth = (int) (0.3 * task.getWidth()) - space;
        int dendroWidth = task.getWidth() - silWidth;
        Image img = heatmap.generate(dendroWidth, task.getHeight());

        Image imgSil = silhoulette.generate(silWidth, task.getHeight());
        BufferedImage combined = new BufferedImage(task.getWidth(), task.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics g = combined.getGraphics();
        g.drawImage(img, 0, 0, null);
        // 1px to separate silhoulette from heatmap
        g.drawImage(imgSil, dendroWidth + space, 0, null);

        DendrogramVisualizationListener listener = task.getListener();
        if (listener != null) {
            listener.clusteringFinished(task.getClustering());
            listener.previewUpdated(combined);
        }
    }

    @Override
    public void run() {
        while (isRunning()) {
            try {
                ImageTask task = factory.getTask();
                generateImage(task);
            } catch (InterruptedException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

    public boolean isRunning() {
        return running;
    }

    public void stop() {
        this.running = true;
    }

}
