package org.clueminer.dgram;

import java.awt.Graphics2D;
import org.clueminer.clustering.api.dendrogram.DendroNode;
import org.clueminer.clustering.api.dendrogram.DendroPane;

/**
 *
 * @author Tomas Barton
 */
public class DgLeftTree extends DgTree {

    public DgLeftTree(DendroPane panel) {
        super(panel);
    }

    @Override
    protected void drawSubTree(Graphics2D g2, DendroNode node) {
        int nx = (int) scaleHeight(node.getHeight());
        int ny = (int) (node.getPosition() * elementHeight + halfElem);

        drawNode(g2, node, nx, ny);

        int lx = (int) scaleHeight(node.getLeft().getHeight());
        int ly = (int) (node.getLeft().getPosition() * elementHeight + halfElem);

        int rx = (int) scaleHeight(node.getRight().getHeight());
        int ry = (int) (node.getRight().getPosition() * elementHeight + halfElem);
        //we're drawing a U shape
        //straight line
        g2.drawLine(nx, ly, nx, ry);

        //left node
        g2.drawLine(nx, ly, lx, ly);

        //right node
        g2.drawLine(nx, ry, rx, ry);
    }

}
