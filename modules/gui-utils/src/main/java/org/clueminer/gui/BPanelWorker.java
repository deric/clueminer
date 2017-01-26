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
package org.clueminer.gui;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.concurrent.ExecutionException;
import javax.swing.SwingWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Background process for "heavy" work in SwingWorker-pool. Instead computing
 * everything in AWT-EventQueue, a process pool is used so that GUI won't freeze.
 *
 * @author deric
 */
public class BPanelWorker extends SwingWorker<BufferedImage, Integer> {

    private final BPanel panel;
    private static final Logger LOG = LoggerFactory.getLogger(BPanelWorker.class);

    /**
     * Swing Worker uses by default 10 background threads that are being recycled
     * for computing purposes.
     *
     * @param panel
     */
    public BPanelWorker(BPanel panel) {
        this.panel = panel;
    }

    /**
     * Swing components should not be accessed from this thread.
     *
     * @return
     * @throws Exception
     */
    @Override
    protected BufferedImage doInBackground() throws Exception {
        BufferedImage bufferedImage = null;
        if (panel.fitToSpace) {
            if (panel.realSize.width <= 0 || panel.realSize.height <= 0) {
                return bufferedImage;
            }
            //create smaller image, then upscale or downscale
            bufferedImage = new BufferedImage(panel.realSize.width, panel.realSize.height, BufferedImage.TYPE_INT_ARGB);
        } else {
            if (panel.reqSize.width <= 0 || panel.reqSize.height <= 0) {
                return bufferedImage;
            }
            bufferedImage = new BufferedImage(panel.reqSize.width, panel.reqSize.height, BufferedImage.TYPE_INT_ARGB);
        }
        Graphics2D g = bufferedImage.createGraphics();
        this.setProgress(50);
        //this.setOpaque(false);
        // clear the panel
        //g.setColor(getBackground());
        if (panel.preserveAlpha) {
            panel.setOpaque(false);
            g.setComposite(AlphaComposite.Clear);
        } else {
            g.setComposite(AlphaComposite.Src);
            if (panel.fitToSpace) {
                g.fillRect(0, 0, panel.realSize.width, panel.realSize.width);
            } else {
                g.fillRect(0, 0, panel.reqSize.width, panel.reqSize.width);
            }
        }
        panel.drawComponent(g);
        this.setProgress(100);
        return bufferedImage;
    }

    @Override
    protected void done() {
        //after finishing we can safely retrieved result
        try {
            panel.bufferedImage = get();
            //should be called only from EDT thread
            panel.repaint();
        } catch (InterruptedException | ExecutionException ex) {
            //Exceptions.printStackTrace(ex);
            LOG.debug("failed to render BPanel: ", ex);
        }
    }

}
