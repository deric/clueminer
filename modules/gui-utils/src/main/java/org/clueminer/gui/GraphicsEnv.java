package org.clueminer.gui;

import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;

/**
 *
 * @author Tomas Barton
 */
public class GraphicsEnv {

    /**
     * In headless mode (server without screen) we can not get default screen
     * device unless users connects with 'ssh -X ...'
     *
     * Headless mode does not allow creating top level components (JFrame) but
     * other graphics like panels, images (JPanel) etc. should be fine.
     *
     * (That means that we environment variable DISPLAY is set)
     *
     * @param width
     * @param height
     * @return
     */
    public static BufferedImage compatibleImage(int width, int height) {
        BufferedImage image;
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();

        if (ge.isHeadlessInstance()) {
            //without java auto-detection
            image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        } else {
            image = ge.getDefaultScreenDevice().getDefaultConfiguration().createCompatibleImage(width, height);
        }

        return image;
    }

}
