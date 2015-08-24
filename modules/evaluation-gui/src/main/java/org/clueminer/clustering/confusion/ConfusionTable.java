package org.clueminer.clustering.confusion;

import com.google.common.base.Supplier;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import com.google.common.collect.Tables;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.image.BufferedImage;
import java.util.Map;
import java.util.Set;
import javax.swing.JPanel;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.colors.ColorScheme;
import org.clueminer.dataset.api.Instance;
import org.clueminer.gui.ColorPalette;

/**
 *
 * @author Tomas Barton
 */
public class ConfusionTable<E extends Instance, C extends Cluster<E>> extends JPanel {

    private static final long serialVersionUID = -7558362062012338814L;
    private Dimension elemSize = new Dimension(10, 10);
    protected int fontSize = 10;
    protected Font defaultFont;
    protected Dimension size = new Dimension(0, 0);
    protected BufferedImage bufferedImage;
    protected Graphics2D g;
    private int[][] confmat;
    private int min;
    private int max;
    protected ColorPalette colorScheme;
    private boolean maxInRows = true;
    private boolean displayClustSizes = true;
    protected int maxWidth;
    protected boolean changedMax = false;
    private String[] colLabels;
    private String[] rowLabels;
    private static final String unknownLabel = "unknown";
    private int[] sumRows;
    private int[] sumCols;
    //color zeros with out of scale color
    private boolean zeroColoring = true;
    private static final Color zeroColor = Color.LIGHT_GRAY;

    public ConfusionTable() {
        initComponents();
    }

    private void initComponents() {
        defaultFont = new Font("verdana", Font.PLAIN, fontSize);
        colorScheme = new ColorScheme(Color.RED, Color.YELLOW, Color.GREEN);
    }

    public void setClusterings(Clustering<E, C> c1, Clustering<E, C> c2) {
        rowLabels = clusterNames(c1);
        colLabels = clusterNames(c2);

        confmat = countMutual(c1, c2);
        if (!maxInRows) {
            findMinMaxMatrix(confmat);
            colorScheme.setRange(min, max);
        }
        resetCache();
    }

    private String[] clusterNames(Clustering<E, C> clust) {
        String[] labels = new String[clust.size()];
        for (int i = 0; i < clust.size(); i++) {
            labels[i] = clust.get(i).getName();
        }
        return labels;
    }

    public void setClustering(Clustering<E, C> clust) {
        colLabels = clusterNames(clust);

        confmat = countMutual(clust);
        if (!maxInRows) {
            findMinMaxMatrix(confmat);
            colorScheme.setRange(min, max);
        }
        resetCache();
    }

    /**
     *
     * @param c1 - clustering displayed in rows
     * @param c2 - clustering displayed in columns
     * @return matrix with numbers of instances in same clusters
     */
    public int[][] countMutual(Clustering<E, C> c1, Clustering<E, C> c2) {
        int[][] conf = new int[c1.size()][c2.size()];
        Cluster<E> curr;
        sumRows = new int[c1.size()];
        sumCols = new int[c2.size()];

        for (int i = 0; i < c1.size(); i++) {
            curr = c1.get(i);
            for (Instance inst : curr) {
                for (int j = 0; j < c2.size(); j++) {
                    if (c2.get(j).contains(inst.getIndex())) {
                        conf[i][j]++;
                    }
                }
            }
            sumRows[i] = curr.size();
        }

        //update sum of columns
        for (int j = 0; j < c2.size(); j++) {
            sumCols[j] = c2.get(j).size();
        }

        //Dump.matrix(conf, "conf mat", 0);
        return conf;
    }

    /**
     * Count number of classes in each cluster when we don't know how many
     * classes we have.
     *
     *
     * @param clust
     * @return
     */
    public int[][] countMutual(Clustering<E, C> clust) {
        //SortedSet klasses = dataset.getClasses();
        //Table<String, String, Integer> table = counting.contingencyTable(clust);
        Table<String, String, Integer> table = contingencyTable(clust);
        //String[] klassLabels = (String[]) klasses.toArray(new String[klasses.size()]);
        Set<String> rows = table.rowKeySet();
        rowLabels = rows.toArray(new String[rows.size()]);
        int[][] conf = new int[rowLabels.length][clust.size()];
        sumCols = new int[clust.size()];
        sumRows = new int[rowLabels.length];

        int k = 0;
        //Dump.array(rowLabels, "classes");
        for (Cluster<E> c : clust) {
            Map<String, Integer> col = table.column(c.getName());
            for (int i = 0; i < rowLabels.length; i++) {
                if (col.containsKey(rowLabels[i])) {
                    conf[i][k] = col.get(rowLabels[i]);
                    sumRows[i] += conf[i][k];
                }
                sumCols[k] += conf[i][k];
            }
            k++;
        }
        //Dump.matrix(conf, "conf mat", 0);
        return conf;
    }

    public Table<String, String, Integer> newTable() {
        return Tables.newCustomTable(
                Maps.<String, Map<String, Integer>>newHashMap(),
                new Supplier<Map<String, Integer>>() {
                    @Override
                    public Map<String, Integer> get() {
                        return Maps.newHashMap();
                    }
                });
    }

    /**
     * Should count number of item with same assignment to <Cluster A, Class X>
     * Instances must have included information about class assignment. This
     * table is sometimes called contingency table
     *
     * Classes are in rows, Clusters are in columns
     *
     * @param clustering
     * @return table with counts of items for each pair cluster, class
     */
    public Table<String, String, Integer> contingencyTable(Clustering<E, C> clustering) {
        // a lookup table for storing correctly / incorrectly classified items
        Table<String, String, Integer> table = newTable();

        //Cluster current;
        E inst;
        String cluster, label;
        int cnt;
        for (Cluster<E> current : clustering) {
            for (int i = 0; i < current.size(); i++) {
                inst = current.instance(i);
                cluster = current.getName();
                Object klass = inst.classValue();
                if (klass != null) {
                    label = klass.toString();
                } else {
                    label = unknownLabel;
                }

                if (table.contains(label, cluster)) {
                    cnt = table.get(label, cluster);
                } else {
                    cnt = 0;
                }

                cnt++;
                table.put(label, cluster, cnt);
            }
        }
        return table;
    }

    protected void updateSize(Dimension size) {
        elemSize = size;
        resetCache();
    }

    protected void createBufferedGraphics() {
        if (!hasData() || size.width <= 0 || size.height <= 0) {
            return;
        }
        bufferedImage = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);
        g = bufferedImage.createGraphics();
        this.setOpaque(false);
        // clear the panel
        g.setColor(getBackground());
        g.fillRect(0, 0, size.width, size.height);

        g.setComposite(AlphaComposite.Src);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        render(g);
        g.dispose();
    }

    public boolean hasData() {
        return (rowLabels != null && colLabels != null);
    }

    public void render(Graphics2D g) {
        int x, y;
        int value;
        String s;
        g.setColor(Color.BLACK);
        g.setFont(defaultFont);
        FontRenderContext frc = g.getFontRenderContext();
        FontMetrics fm = g.getFontMetrics();
        int fh = fm.getHeight();
        double fw;
        for (int i = 0; i < rowLabels.length; i++) {
            if (maxInRows) {
                findMinMaxRow(confmat, i);
                colorScheme.setRange(min, max);
            }
            for (int j = 0; j < colLabels.length; j++) {
                value = confmat[i][j];
                //cnt = a.get(i).countMutualElements(b.get(j));
                //System.out.println("a-" + a.get(i).getName() + "-vs" + "-b-" + b.get(j).getName() + ": " + cnt);
                x = j * elemSize.width;
                y = i * elemSize.height;
                if (zeroColoring && value == 0) {
                    g.setColor(zeroColor);
                } else {
                    g.setColor(colorScheme.getColor(value));
                }
                g.fillRect(x, y, elemSize.width, elemSize.height);
                s = String.valueOf(value);
                fw = (g.getFont().getStringBounds(s, frc).getWidth());

                g.setColor(colorScheme.complementary(g.getColor()));
                g.drawString(s, (int) (x - fw / 2 + elemSize.width / 2), y + elemSize.height / 2 + fh / 2);
                //draw rectangle around
                g.setColor(Color.BLACK);
                g.drawRect(x, y, elemSize.width - 1, elemSize.height - 1);
                //System.out.println("drawing rect: " + x + ", " + y + " w = " + elemSize.width + ", h= " + elemSize.height);
            }
        }
        if (displayClustSizes) {
            drawSums(g);
        }
    }

    /**
     * Displays sums in rows/columns next to table
     *
     * @param g
     */
    private void drawSums(Graphics2D g) {
        g.setColor(Color.GRAY);
        FontRenderContext frc = g.getFontRenderContext();
        FontMetrics fm = g.getFontMetrics();
        int fh = fm.getHeight();
        double fw;
        String str;
        int x, y;
        maxWidth = 0;

        //columns
        if (sumCols != null) {
            y = rowLabels.length * elemSize.height;
            for (int col = 0; col < sumCols.length; col++) {
                x = col * elemSize.width;
                str = String.valueOf(sumCols[col]);
                fw = (g.getFont().getStringBounds(str, frc).getWidth());
                checkMax((int) fw);
                g.drawString(str, (int) (x - fw / 2 + elemSize.width / 2), y + elemSize.height / 2 + fh / 2);
            }
        }
        //last row
        if (sumRows != null) {
            x = colLabels.length * elemSize.width;
            for (int row = 0; row < sumRows.length; row++) {
                y = row * elemSize.height;
                str = String.valueOf(sumRows[row]);
                fw = (g.getFont().getStringBounds(str, frc).getWidth());
                checkMax((int) fw);
                g.drawString(str, (int) (x - fw / 2 + elemSize.width / 2), y + elemSize.height / 2 + fh / 2);
            }
        }
    }

    public void redraw() {
        Graphics2D g2 = (Graphics2D) this.getGraphics();
        if (g2 == null) {
            return;
        }
        //buffered graphics is usually created before
        if (!hasData() && bufferedImage == null) {
            createBufferedGraphics();
        }
        if (bufferedImage != null) {
            g2.drawImage(bufferedImage,
                    0, 0,
                    size.width, size.height,
                    null);
        }
        g2.dispose();
    }

    protected void recalculate() {
        if (hasData()) {
            int rows = rowLabels.length;
            int cols = colLabels.length;
            if (displayClustSizes) {
                rows += 1;
                cols += 1;
            }
            this.size.width = elemSize.width * cols;
            this.size.height = elemSize.height * rows;
            double fsize = elemSize.height * 0.5;
            defaultFont = defaultFont.deriveFont((float) fsize);
            if (maxWidth >= elemSize.width) {
                fsize *= 0.9;
                defaultFont = defaultFont.deriveFont((float) fsize);
            }
            //System.out.println("elem width = " + elemSize.width);
            //System.out.println("|a| = " + a.size());
            //System.out.println("matrix size: " + size.toString());
            //setMinimumSize(this.size);
            setSize(this.size);
            setPreferredSize(size);
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (bufferedImage == null) {
            createBufferedGraphics();
            if (changedMax) {
                changedMax = false;
                resetCache();
            }
        }
        //cached image
        g.drawImage(bufferedImage,
                0, 0,
                size.width, size.height,
                null);
        g.dispose();
    }

    public void resetCache() {
        recalculate();
        createBufferedGraphics();
        repaint();
    }

    /**
     * Finds min and max value in confusion matrix
     *
     * @param confmat
     */
    private void findMinMaxMatrix(int[][] confmat) {
        min = Integer.MAX_VALUE;
        max = Integer.MIN_VALUE;
        for (int[] row : confmat) {
            for (int j = 0; j < row.length; j++) {
                if (row[j] < min) {
                    min = row[j];
                }
                if (row[j] > max) {
                    max = row[j];
                }
            }
        }
    }

    private void findMinMaxRow(int[][] confmat, int rowId) {
        min = Integer.MAX_VALUE;
        max = Integer.MIN_VALUE;
        int[] row = confmat[rowId];
        for (int j = 0; j < row.length; j++) {
            if (row[j] < min) {
                min = row[j];
            }
            if (row[j] > max) {
                max = row[j];
            }
        }
    }

    private void findMinMaxCol(int[][] confmat, int colId) {
        min = Integer.MAX_VALUE;
        max = Integer.MIN_VALUE;
        for (int j = 0; j < confmat[colId].length; j++) {
            if (confmat[j][colId] < min) {
                min = confmat[j][colId];
            }
            if (confmat[j][colId] > max) {
                max = confmat[j][colId];
            }
        }
    }

    public boolean isDisplayClustSizes() {
        return displayClustSizes;
    }

    public void setDisplayClustSizes(boolean displayClustSizes) {
        this.displayClustSizes = displayClustSizes;
    }

    protected void checkMax(int width) {
        if (width > maxWidth) {
            maxWidth = width;
            changedMax = true;
        }
    }

    public String[] getColLabels() {
        return colLabels;
    }

    public String[] getRowLabels() {
        return rowLabels;
    }
}
