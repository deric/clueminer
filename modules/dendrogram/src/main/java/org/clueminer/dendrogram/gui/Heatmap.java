package org.clueminer.dendrogram.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.clustering.api.dendrogram.ColorScheme;
import org.clueminer.clustering.api.dendrogram.DendroHeatmap;
import org.clueminer.clustering.api.dendrogram.DendroPane;
import org.clueminer.clustering.api.dendrogram.DendrogramDataEvent;
import org.clueminer.clustering.api.dendrogram.DendrogramDataListener;
import org.clueminer.clustering.api.dendrogram.DendrogramMapping;
import org.clueminer.clustering.api.dendrogram.DendrogramTree;
import org.clueminer.clustering.api.dendrogram.TreeCluster;
import org.clueminer.clustering.api.dendrogram.TreeListener;
import org.clueminer.clustering.gui.colors.ColorSchemeImpl;
import org.clueminer.dendrogram.DistributionCollector;
import org.clueminer.dendrogram.tree.VerticalTree;
import org.imgscalr.Scalr;

/**
 * This class is used to render a heatmap of given dendroData.
 *
 *
 * @author Tomas Barton
 */
public class Heatmap extends JPanel implements DendrogramDataListener, TreeListener, DendroHeatmap {

    private static final long serialVersionUID = -676917065082387341L;
    protected Dimension elementSize;
    private boolean isDrawBorders = false;
    private boolean isCompact = true;
    private boolean isShowRects = false;
    private boolean clickedCell = true;
    private int clickedColumn = 0;
    private int clickedRow = 0;
    private int firstSelectedRow = -1;
    private int lastSelectedRow = -1;
    private int firstSelectedColumn = -1;
    private int lastSelectedColumn = -1;
    private Insets insets = new Insets(0, 10, 0, 0);
    private boolean showClusters = true;
    private boolean haveColorBar = false;
    private int colorWidth = 0;
    private int maxColorWidth = 0;
    private boolean mouseOnMap = true;
    private int mouseRow = 0;
    private int mouseColumn = 0;
    private boolean inColorbarDrag = false;
    private int dragRow = 0;
    private int dragColumn = 0;
    private DendrogramMapping dendroData;
    private BufferedImage bufferedImage;
    private Graphics2D bufferedGraphics;
    private Rectangle bounds;
    private DendroPane panel;
    private Dimension size = new Dimension(10, 10);
    private ColorScheme colorScheme;
    private final DistributionCollector distribution;
    private boolean collectData = false;

    public Heatmap() {
        setBackground(Color.GRAY);
        setDoubleBuffered(true); //offscree painting
        setOpaque(true);  //not transparent
        elementSize = new Dimension(10, 10);
        colorScheme = new ColorSchemeImpl();
        distribution = new DistributionCollector(50);
        updateSize();
    }

    public Heatmap(DendroPane p) {
        this.panel = p;
        setBackground(panel.getBackground());
        colorScheme = panel.getScheme();
        Listener listener = new Listener();
        addMouseListener(listener);
        addMouseMotionListener(listener);
        this.setDoubleBuffered(false);
        this.elementSize = p.getElementSize();
        distribution = new DistributionCollector(100);
        updateSize();
    }

    public void setInsets(Insets i) {
        this.insets = i;
    }

    public void setOffset(int offset) {
        this.insets.left = offset;
    }

    /**
     * Dataset to display
     *
     * @param dendroData
     */
    @Override
    public void setData(DendrogramMapping dendroData) {
        this.dendroData = dendroData;
    }

    /**
     * Sets the left margin for the viewer
     *
     * @param leftMargin
     */
    public void setLeftInset(int leftMargin) {
        insets.left = leftMargin;
    }

    /**
     * Selects rows from start to end.
     *
     * @param start
     * @param end
     */
    public void selectRows(int start, int end) {
        firstSelectedRow = start;
        lastSelectedRow = end;
        repaint();
    }

    /**
     * Selects columns from start to end.
     *
     * @param start
     * @param end
     */
    public void selectColumns(int start, int end) {
        firstSelectedColumn = start;
        lastSelectedColumn = end;
        repaint();
    }

    /**
     * Sets dendroData for this viewer and its header.
     *
     */
    public void onDataChanged() {
        if (showClusters) {
            haveColorBar = false;
        }
        updateSize();
    }

    /**
     * Returns the row index in the experiment's <code>FloatMatrix<\code>
     * corresponding to the passed index to the clusters array
     */
    private int rowIndex(int row) {
        //return this.clusters[this.clusterIndex][row];
        if (dendroData.hasRowsClustering()) {
            return dendroData.getRowsResult().getMappedIndex(row);
        }
        //no ordering
        return row;
    }

    private int colIndex(int column) {
        if (dendroData.hasColumnsClustering()) {
            return dendroData.getColsResult().getMappedIndex(column);
        }
        //no columns ordering
        return column;
    }

    public Dimension getElementSize() {
        return elementSize;
    }

    /**
     * Sets draw borders attribute.
     */
    public void setDrawBorders(boolean value) {
        this.isDrawBorders = value;
    }

    /**
     * Updates dimensions of this component We want to set preferred size,
     * because the actual size depends on layout manager
     */
    public final void updateSize() {
        if (dendroData == null) {
            return;
        }
        size.width = countComponentWidth(elementSize.width);

        if (maxColorWidth < colorWidth) {
            maxColorWidth = colorWidth;
        }
        if (haveColorBar) {
            size.width += this.elementSize.width * colorWidth + 10;
        }

        size.height = countComponentHeight(elementSize.height);
        setSize(size);
        setMinimumSize(size);
    }

    private int countComponentWidth(int elementWidth) {
        return elementWidth * dendroData.getNumberOfColumns() + 1 + insets.left;
    }

    private int countComponentHeight(int elementHeight) {
        return elementHeight * dendroData.getNumberOfRows() + 1 + insets.top;
    }

    @Override
    public Dimension getPreferredSize() {
        return size;
    }

    /**
     * Returns content width
     *
     * @return
     */
    public int getContentWidth() {
        return size.width;
    }

    /**
     * Creates a BufferedImage of the actual dendroData plot.
     *
     * After doing some profiling, it was discovered that 90% of the drawing
     * time was spend drawing the actual dendroData (not on the axes or tick
     * marks). Since the Graphics2D has a drawImage method that can do scaling,
     * we are using that instead of scaling it ourselves. We only need to draw
     * the dendroData into the bufferedImage on startup, or if the dendroData or
     * gradient changes. This saves us an enormous amount of time. Thanks to
     * Josh Hayes-Sheen (grey@grevian.org) for the suggestion and initial code
     * to use the BufferedImage technique.
     *
     * Since the scaling of the dendroData plot will be handled by the drawImage
     * in paintComponent, we take the easy way out and draw our bufferedImage
     * with 1 pixel per dendroData point. Too bad there isn't a setPixel method
     * in the Graphics2D class, it seems a bit silly to fill a rectangle just to
     * set a single pixel...
     *
     * This function should be called whenever the dendroData or the gradient
     * changes.
     *
     * The method is synchronized because we don't want to perform multiple
     * paintings at the same time.
     *
     * @param size
     */
    @Override
    public synchronized BufferedImage drawData(Dimension size) {
        this.setOpaque(true);
        //if we don't have any dendroData, ends here
        if (dendroData == null) {
            return null;
        }

        if (this.elementSize.getHeight() < 1) {
            return null;
        }

        bufferedImage = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);
        // System.out.println("buffered image size w="+size.width+" x "+size.height);
        bufferedGraphics = bufferedImage.createGraphics();

        /*
         * this way we would repaint just a visible part of screen TODO consider
         * using it for partial update of buffered image final int top =
         * getTopIndex(bounds.y); final int bottom = getBottomIndex(bounds.y +
         * bounds.height, getCluster().length); final int left =
         * getLeftIndex(bounds.x); final int right = getRightIndex(bounds.x +
         * bounds.width, samples); System.out.println("top= "+top+", bottom=
         * "+bottom+", left= "+left+", right= "+right);
         *
         * // draw rectangles for (int column = left; column < right; column++)
         * { for (int row = top; row < bottom; row++) {
         * fillRectAt(bufferedGraphics, row, column); } }
         */
        for (int column = 0; column < dendroData.getNumberOfColumns(); column++) {
            for (int row = 0; row < dendroData.getNumberOfRows(); row++) {
                fillRectAt(bufferedGraphics, row, column);
            }
        }
        //until next dataset change
        collectData = false;

        if (haveColorBar) {
            if (dendroData != null) {
                fillClusterColorPositions(bufferedGraphics);
            }
        }
        bufferedGraphics.dispose();
        return bufferedImage;
    }

    // Always required for good double-buffering.
    // This will cause the applet not to first wipe off
    // previous drawings but to immediately repaint.
    // the wiping off also causes flickering.
    // Update is called automatically when repaint() is called.
    @Override
    public void update(Graphics g) {
        paint(g);
    }

    /**
     * Paint component into specified graphics.
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        //System.out.println("paintComponent width=" + width + ", height=" + height + " sw=" + scale.width + ", sh= " + scale.height);
        // clear the panel
        //g2d.setColor(panel.bg);
        //g2d.fillRect(0, 0, width, height);
        //visible area
        bounds = g2d.getClipBounds();

        // draw the heat map
        if (bufferedImage == null) {
            // Ideally, we only to call drawData in the constructor, or if we
            // change the dendroData or gradients. We include this just to be safe.
            bufferedImage = drawData(size);
            // TODO bufferedImage might be still null
        }

        // The dendroData plot itself is drawn with 1 pixel per dendroData point, and the
        // drawImage method scales that up to fit our current window size. This
        // is very fast, and is much faster than the previous version, which
        // redrew the dendroData plot each time we had to repaint the screen.
        //draws buffered image
        g.drawImage(bufferedImage,
                0, 0,
                size.width, size.height,
                null);

        if (dendroData != null) {
            int oldWidth = colorWidth;

            if (mouseOnMap) {
                drawRectAt(g2d, mouseRow, mouseColumn, Color.white);
                if (haveColorBar && isShowRects) {
                    drawClusterRectsAt(g2d, mouseRow, mouseColumn, Color.gray);
                }
            }
            mouseOnMap = false;
            if (clickedCell) {
                g2d.setColor(Color.red);
                if (!isCompact) {
                    drawClusterRectsAt(g2d, clickedRow, clickedColumn, Color.red);
                }
            }
            if (inColorbarDrag) {
                g2d.setColor(Color.blue);
                g2d.drawRect(dragColumn * elementSize.width + insets.left + 5 - 1, -1, (elementSize.width), elementSize.height * dendroData.getNumberOfRows() + 1);
            }

            if (colorWidth != oldWidth) {
                updateSize();
            }
        }
        g.dispose();
    }

    /**
     * Fills rectangle with specified row and column.
     */
    private void fillRectAt(Graphics g, int row, int column) {
        if (column > (dendroData.getNumberOfColumns() - 1)) {
            return;
        }
        int x = column * elementSize.width + insets.left;
        int y = row * elementSize.height;
        double value;
        boolean mask = this.firstSelectedRow >= 0 && this.lastSelectedRow >= 0 && (row < this.firstSelectedRow || row > this.lastSelectedRow);
        mask = (mask || this.firstSelectedColumn >= 0 && this.lastSelectedColumn >= 0 && (column < this.firstSelectedColumn || column > this.lastSelectedColumn));
//System.out.println("orig row "+row+" -> "+rowIndex(row)+" orig col= "+column+" -> "+colIndex(column));
        value = dendroData.get(rowIndex(row), colIndex(column));
        g.setColor(colorScheme.getColor(value, dendroData));
        if (collectData) {
            distribution.sample(value);
        }
        //System.out.println("x: "+x+", y: "+y+" insets: "+insets+" element size: "+elementSize);
        g.fillRect(x, y + insets.top, elementSize.width, elementSize.height);
        if (mask) {
            g.setColor(ColorSchemeImpl.maskColor);
            g.fillRect(x, y + insets.top, elementSize.width, elementSize.height);
        }
        if (this.isDrawBorders) {
            g.setColor(Color.black);
            g.drawRect(x, y + insets.top, elementSize.width - 1, elementSize.height - 1);
        }
    }

    /**
     * fills cluster colors
     */
    private void fillClusterRectAt(Graphics g, int row, int xLoc, Color color) {
        if (color == null) {
            color = Color.white;
        }

        g.setColor(color);

        g.fillRect(xLoc + insets.left, row * elementSize.height + insets.top, elementSize.width - 1, elementSize.height);
    }

    /**
     * @TODO move to separate component
     *
     * Determines the location of the cluster colors for either compact or
     * non-compact settings and then fills the appropriate rectangles
     */
    private void fillClusterColorPositions(Graphics g) {
        final int samples = dendroData.getNumberOfColumns();
        // Rectangle bounds = g.getClipBounds();
        final int top = getTopIndex(bounds.y);
        final int bottom = getBottomIndex(bounds.y + bounds.height, dendroData.getNumberOfRows());

        int spacesOver = 0;
        /*
         * for (int row = top; row < bottom; row++) { Color[] colors =
         * dendroData.getRowsColors(getDataRow(row)); if (colors == null) { continue;
         * } for (int clusters = 0; clusters < colors.length; clusters++) { if
         * (colors[clusters] == null) { continue; } if
         * (storedRowColors.contains(colors[clusters])) { activeCluster =
         * storedRowColors.indexOf(colors[clusters]); } else {
         * storedRowColors.add(colors[clusters]); activeCluster =
         * (storedRowColors.size() - 1); ColorOverlaps[activeCluster] =
         * activeCluster; //compacts the cluster color display boolean foundit =
         * false; if (!isCompact) { foundit = true; } while (!foundit) { for
         * (int i = 0; i < storedRowColors.size(); i++) { boolean allClear =
         * true; for (int j = 0; j < storedRowColors.size(); j++) { if
         * (ColorOverlaps[j] == i) { if (dendroData.isColorOverlap(getDataRow(row),
         * colors[clusters], storedRowColors.get(j))) { allClear = false; break;
         * } allClear = true; } } if (allClear) { ColorOverlaps[activeCluster] =
         * i; foundit = true; break; } } if (foundit) { break; } } } spacesOver
         * = ColorOverlaps[activeCluster]; int expWidth = samples *
         * this.elementSize.width + 5 + this.elementSize.width * spacesOver;
         * fillClusterRectAt(g, row, expWidth, colors[clusters]); }public double getMidValue()
         }
         */
    }

    /**
     * Draws rect with specified row, column and color.
     */
    private void drawRectAt(Graphics g, int row, int column, Color color) {
        g.setColor(color);
        if (column < dendroData.getNumberOfColumns()) {
            g.drawRect(column * elementSize.width + insets.left, row * elementSize.height + insets.top, elementSize.width - 1, elementSize.height - 1);
        }
    }

    private void drawClusterRectsAt(Graphics g, int row, int column, Color color) {
        // System.out.println(color);
        g.setColor(color);
        if (column >= dendroData.getNumberOfColumns()) {
            g.drawRect((dendroData.getNumberOfColumns()) * elementSize.width + insets.left + 5 - 1, row * elementSize.height - 1 + insets.top, (elementSize.width) * (colorWidth) + 8, elementSize.height + 1);
            if (isCompact) {
                return;
            }
            g.drawRect(column * elementSize.width + insets.left + 5 - 1, -1 + insets.top, (elementSize.width), elementSize.height * dendroData.getNumberOfColumns() + 1);
            //  header.drawClusterHeaderRectsAt(column, color, true);

        } else {
            g.drawRect((dendroData.getNumberOfColumns()) * elementSize.width + insets.left + 5 - 1, row * elementSize.height - 1 + insets.top, (elementSize.width) * (colorWidth) + 8, elementSize.height + 1);
            //  header.drawClusterHeaderRectsAt(column, color, false);
        }
    }

    private int getTopIndex(int top) {
        if (top < 0) {
            return 0;
        }
        return top / elementSize.height;
    }

    private int getLeftIndex(int left) {
        if (left < insets.left) {
            return 0;
        }
        return (left - insets.left) / elementSize.width;
    }

    private int getRightIndex(int right, int limit) {
        if (right < 0) {
            return 0;
        }
        int result = right / elementSize.width + 1;
        return result > limit ? limit : result;
    }

    private int getBottomIndex(int bottom, int limit) {
        if (bottom < 0) {
            return 0;
        }
        int result = bottom / elementSize.height + 1;
        return result > limit ? limit : result;
    }

    /**
     * Finds column for specified x coordinate.
     *
     * @return -1 if column was not found.
     */
    private int findColumn(int targetx) {
        if (dendroData == null) {
            return -1;
        }
        int xSize = dendroData.getNumberOfColumns() * elementSize.width;
        if (targetx < insets.left) {
            return -1;
        }
        if (targetx >= (xSize + insets.left) && (targetx < (xSize + insets.left + this.elementSize.width * colorWidth + 10))) {
            return (targetx - insets.left - 5) / elementSize.width;
        }
        return (targetx - insets.left) / elementSize.width;
    }

    /**
     * Finds row for specified y coordinate.
     *
     * @return -1 if row was not found.
     */
    private int findRow(int targety) {
        if (dendroData == null || dendroData.isEmpty()) {
            return -1;
        }
        int ySize = dendroData.getNumberOfRows() * elementSize.height + insets.top;
        if (targety >= ySize || targety < 0) {
            return -1;
        }
        return targety / elementSize.height;
    }

    private boolean isLegalPosition(int row, int column) {
        return isLegalRow(row) && isLegalColumn(column);
    }

    private boolean isLegalColumn(int column) {
        return column >= 0 && column <= (dendroData.getNumberOfColumns() - 1 + colorWidth);
    }

    private boolean isLegalRow(int row) {
        return row >= 0 && row <= dendroData.getNumberOfRows() - 1;
    }

    public void onSelected() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void datasetChanged(DendrogramDataEvent evt, DendrogramMapping mapping) {
        this.dendroData = mapping;
        distribution.datasetChanged(mapping.getDataset());
        updateSize();
        collectData = true;
        // this is the expensive function that draws the dendroData plot into a
        // BufferedImage. The dendroData plot is then cheaply drawn to the screen when
        // needed, saving us a lot of time in the end.
        drawData(size);
        //paints whole component
        redraw();
    }

    public DistributionCollector getDistribution() {
        return distribution;
    }

    /**
     * Drawing just buffered image, much faster than repaint
     */
    public void redraw() {
        Graphics2D g = (Graphics2D) this.getGraphics();
        if (g != null && bufferedImage != null) {
            g.drawImage(bufferedImage,
                    0, 0,
                    size.width, size.height,
                    null);
        } else {
            Logger.getLogger(Heatmap.class.getName()).log(Level.SEVERE, "missing buffered image {0}", size);
        }


        /*
         * g.setComposite(AlphaComposite.Src);
         * g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
         * RenderingHints.VALUE_INTERPOLATION_BILINEAR);
         * g.setRenderingHint(RenderingHints.KEY_RENDERING,
         * RenderingHints.VALUE_RENDER_QUALITY);
         * g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
         * RenderingHints.VALUE_ANTIALIAS_ON);
         */
    }

    @Override
    public void cellWidthChanged(DendrogramDataEvent evt, int width, boolean isAdjusting) {
        this.elementSize.width = width;
        updateSize();
        if (isAdjusting) {
            redraw();
        } else {
            //drawing is expensive, so we try to call it as few times as possible
            drawData(size);
        }
    }

    @Override
    public void cellHeightChanged(DendrogramDataEvent evt, int height, boolean isAdjusting) {
        this.elementSize.height = height;
        updateSize();
        if (isAdjusting) {
            redraw();
        } else {
            //drawing is expensive, so we try to call it as few times as possible
            drawData(size);
        }
    }

    @Override
    public void clusterSelected(DendrogramTree source, TreeCluster cluster, DendrogramMapping data) {
        if (source instanceof VerticalTree) {
            this.selectRows(cluster.firstElem, cluster.lastElem);
        } else {
            //horizontal tree
            this.selectColumns(cluster.firstElem, cluster.lastElem);
        }

        drawData(size);
    }

    @Override
    public void treeUpdated(DendrogramTree source, int width, int height) {
        //nothing to do right now
    }

    /**
     * Generate image of given size
     *
     * @param width
     * @param height
     * @return
     */
    public Image generate(int width, int height) {

        double fWidth = width / (double) dendroData.getNumberOfColumns();
        double fHeight = height / (double) dendroData.getNumberOfRows();

        //element size can't be smaller than 1px
        elementSize.width = (int) Math.ceil(fWidth);
        elementSize.height = (int) Math.ceil(fHeight);

        size.width = elementSize.width * dendroData.getNumberOfColumns();
        size.height = elementSize.height * dendroData.getNumberOfRows();

        //if we have much more rows, we should use wider columns
        if (size.height > size.width) {
            elementSize.width = size.height / dendroData.getNumberOfColumns();
            size.width = elementSize.width * dendroData.getNumberOfColumns();
        }
        //@TODO do the same with high dimensional data (-> stretch rows)
        BufferedImage image = drawData(size);
        if (image.getHeight() != height || image.getWidth() != width) {
            image = Scalr.resize(image, Scalr.Method.SPEED,
                    Scalr.Mode.FIT_EXACT, width, height, Scalr.OP_ANTIALIAS);
        }
        return image;
    }

    @Override
    public void resetCache() {
        updateSize();
        bufferedImage = drawData(size);
        redraw();
    }

    @Override
    public void setColorScheme(ColorScheme scheme) {
        this.colorScheme = scheme;
    }

    @Override
    public ColorScheme getScheme() {
        return colorScheme;
    }

    @Override
    public void leafOrderUpdated(Object source, HierarchicalResult mapping) {
        resetCache();
    }

    /**
     * The class to listen to mouse events.
     */
    private class Listener extends MouseAdapter implements MouseMotionListener {

        private String oldStatusText;
        private int oldRow = -1;
        private int oldColumn = -1;
        private int startColumn = 0;
        private int startRow = 0;

        @Override
        public void mouseClicked(MouseEvent event) {
            if (SwingUtilities.isRightMouseButton(event)) {
                return;
            }
            int column = findColumn(event.getX());
            int row = findRow(event.getY());
            if (!isLegalPosition(row, column)) {
                return;
            }
            if (column > dendroData.getNumberOfColumns() - 1) {
                if (row == clickedRow && column == clickedColumn) {
                    clickedCell = !clickedCell;
                    //   header.clusterViewerClicked = clickedCell;
                    return;
                }
                clickedRow = row;
                clickedColumn = column;
                clickedCell = true;
                //   header.clusterViewerClickedColumn = column;
                //   header.clusterViewerClicked = true;
                if (isCompact) {
                    clickedCell = false;
                    //       header.clusterViewerClicked = false;
                }
                repaint();
                return;
            }
            if (!event.isShiftDown()) { // element info
               /*
                 * System.out.println("getting " +
                 * dendroData.getColumnIndex(getColumn(column)) + " " +
                 * getDataRow(row) + " value= " + dendroData.get(getColumn(column),
                 * getDataRow(row)) + " name= " +
                 * dendroData.getColumnName(getColumn(column)) + " orig= " + column);
                 */
                System.out.println("row= " + row + ", column= " + column + ", value= " + dendroData.getMappedValue(row, column));
                //  framework.displaySlideElementInfo(experiment.getSampleIndex(getColumn(column)), getMultipleArrayDataRow(row));
            }
        }

        @Override
        public void mouseMoved(MouseEvent event) {
            if (dendroData == null || dendroData.getNumberOfColumns() == 0 || event.isShiftDown()) {
                return;
            }
            int column = findColumn(event.getX());
            int row = findRow(event.getY());
            Graphics g = getGraphics();
            //mouse on same rectangle
            if (isCurrentPosition(row, column)) {
                if (isLegalPosition(row, column) && isShowRects) {
                    drawClusterRectsAt(g, oldRow, oldColumn, Color.gray);
                }
                return;
            }
            //mouse on heat map
            if (isLegalPosition(row, column) && (column < dendroData.getNumberOfColumns())) {
                drawRectAt(g, row, column, Color.white);
                if (isShowRects) {
                    drawClusterRectsAt(g, row, column, Color.gray);
                }
                /*
                 * framework.setStatusText( "Gene: "+
                 * dendroData.getUniqueId(getMultipleArrayDataRow(row)) +" Sample: "+
                 * dendroData.getSampleName(experiment.getSampleIndex(getColumn(column)))
                 * +" Value: "+ experiment.get(getExperimentRow(row),
                 * getColumn(column)));
                 */
            }
            //mouse on different rectangle, but still on the map
            if (!isCurrentPosition(row, column) && isLegalPosition(row, column)) {
                mouseOnMap = true;
                mouseRow = row;
                mouseColumn = column;
                repaint();
            } //mouse on cluster part of map
            else {
                repaint();
            }
            if (isLegalPosition(oldRow, oldColumn)) {
                g = g != null ? g : getGraphics();
                fillRectAt(g, oldRow, oldColumn);
            }
            setOldPosition(row, column);
            if (g != null) {
                g.dispose();
            }
        }

        @Override
        public void mouseEntered(MouseEvent event) {
            /*
             * try { oldStatusText = framework.getStatusText(); } catch
             * (NullPointerException npe) { npe.printStackTrace(); }
             */
        }

        @Override
        public void mouseExited(MouseEvent event) {
            mouseOnMap = false;
//            header.setDrag(false, 0, 0);
            inColorbarDrag = false;
            repaint();
            if (isLegalPosition(oldRow, oldColumn)) {
                Graphics g = getGraphics();
                fillRectAt(g, oldRow, oldColumn);
                g.dispose();
            }
            setOldPosition(-1, -1);
            System.out.println();
            //framework.setStatusText(oldStatusText);
            repaint();
        }

        @Override
        public void mouseDragged(MouseEvent event) {
            repaint();
            if (SwingUtilities.isRightMouseButton(event)) {
                return;
            }
            int column = findColumn(event.getX());
            int row = findRow(event.getY());
            if (!isLegalPosition(row, column)) {
                inColorbarDrag = false;
                //   header.setDrag(false, 0, 0);
                return;
            }
            if (!inColorbarDrag) {
                return;
            }
            dragColumn = column;
            dragRow = row;
            //   header.setDrag(true, dragColumn, dragRow);
            if (column >= dendroData.getNumberOfColumns()) {
            } else {
                inColorbarDrag = false;
                //       header.setDrag(false, 0, 0);
            }
        }

        /**
         * Called when the mouse has been pressed.
         */
        @Override
        public void mousePressed(MouseEvent event) {
            if (SwingUtilities.isRightMouseButton(event)) {
                return;
            }

            startColumn = findColumn(event.getX());
            startRow = findRow(event.getY());
            if ((!isLegalPosition(startRow, startColumn)) || event.isShiftDown() || startColumn < dendroData.getNumberOfColumns()) {
                return;
            }
            inColorbarDrag = true;

            dragColumn = startColumn;
            dragRow = startRow;
            //      header.setDrag(true, startColumn, startRow);
        }

        /**
         * Called when the mouse has been released.
         */
        @Override
        public void mouseReleased(MouseEvent event) {
            if (!inColorbarDrag) {
                return;
            }
            inColorbarDrag = false;
            //   header.setDrag(false, 0, 0);
            int endColumn = findColumn(event.getX());
            if (endColumn < dendroData.getNumberOfColumns()) {
                return;
            }
            int endRow = findRow(event.getY());
            if (!isLegalPosition(startRow, startColumn)) {
                return;
            }
            if (!isCompact) {
                /*  Color inter = storedRowColors.get(startColumn - dendroData.getNumberOfColumns());
                 storedRowColors.remove(startColumn - dendroData.getNumberOfColumns());
                 storedRowColors.add(endColumn - dendroData.getNumberOfColumns(), inter);*/
                repaint();
            } else {
                /*   for (int j = 0; j < storedRowColors.size(); j++) {
                 if (ColorOverlaps[j] == startColumn - dendroData.getNumberOfColumns()) {
                 ColorOverlaps[j] = -1;
                 }
                 if (ColorOverlaps[j] == endColumn - dendroData.getNumberOfColumns()) {
                 ColorOverlaps[j] = startColumn - dendroData.getNumberOfColumns();
                 }
                 if (ColorOverlaps[j] == -1) {
                 ColorOverlaps[j] = endColumn - dendroData.getNumberOfColumns();
                 }
                 }
                 */ repaint();
            }
        }

        private void setOldPosition(int row, int column) {
            oldColumn = column;
            oldRow = row;
        }

        private boolean isCurrentPosition(int row, int column) {
            return (row == oldRow && column == oldColumn);
        }
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}
