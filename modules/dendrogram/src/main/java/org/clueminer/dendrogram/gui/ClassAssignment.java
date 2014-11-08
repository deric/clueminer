package org.clueminer.dendrogram.gui;

import com.google.common.collect.BiMap;
import com.google.common.collect.Table;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.clustering.api.dendrogram.DendroPane;
import org.clueminer.colors.ColorBrewer;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.eval.utils.CountingPairs;

/**
 *
 * @author Tomas Barton
 */
public class ClassAssignment extends ClusterAssignment {

    private static final Logger logger = Logger.getLogger(ClassAssignment.class.getName());
    private ColorBrewer colorGenerator = new ColorBrewer();

    public ClassAssignment(DendroPane panel) {
        super(panel);
    }

    @Override
    protected void drawData(Graphics2D g) {
        if (flatClust != null) {
            HierarchicalResult res = flatClust.getLookup().lookup(HierarchicalResult.class);
            if (res != null) {
                hieraRes = res;
            }
        }
        if (flatClust != null && hieraRes != null) {
            int i = 0;
            Dataset<? extends Instance> dataset = hieraRes.getDataset();
            if (dataset.getClasses().size() == 0) {
                logger.log(Level.INFO, "no class information in data");
                return;
            }
            BiMap<String, String> matching = getMatching(flatClust);
            System.out.println("matching " + matching.toString());
            Object2ObjectMap<Object, Color> map = new Object2ObjectOpenHashMap(i);

            g.setFont(font);
            FontMetrics fm = g.getFontMetrics();
            lineHeight = fm.getHeight();
            //int currCluster = clusters[i];
            int mapped = hieraRes.getMappedIndex(i);
            Object currClass = dataset.get(mapped).classValue();
            int start = 0;
            int x = insets.left;

            while (i < dataset.size()) {
                mapped = hieraRes.getMappedIndex(i);
//                System.out.println("curr: " + currClass + " ith: " + dataset.get(mapped).classValue());
                if (!dataset.get(mapped).classValue().equals(currClass)) {
                    //System.out.println("rectangle from " + start + " to " + i);
                    drawClass(g, x, start, i, colorForClass(map, currClass, matching));
                    start = i; //index if new cluster start
                    currClass = dataset.get(mapped).classValue();
                }
                i++;
            }
            //close unfinished cluster
            drawClass(g, x, start, i, colorForClass(map, currClass, matching));
        }
        g.dispose(); //finished drawing
    }

    private Color colorForClass(Object2ObjectMap<Object, Color> map, Object klass, BiMap<String, String> matching) {
        if (!map.containsKey(klass)) {
            String cls = String.valueOf(klass);
            if (matching.containsKey(cls)) {
                String cluster = matching.get(cls);
                Cluster c = flatClust.get(cluster);
                if (c != null) {
                    map.put(klass, c.getColor());
                } else {
                    //FIXME: right now we suppose that the generator is the same as for clusters
                    colorGenerator.seek(map.size());
                    map.put(klass, colorGenerator.next());
                }
            } else {
                //System.out.println("matching miss " + cls);
                //System.out.println(matching.toString());
                colorGenerator.seek(map.size());
                map.put(klass, colorGenerator.next());
            }
        }
        return map.get(klass);
    }

    private void drawClass(Graphics2D g, int x, int start, int end, Color color) {
        int y = start * elemHeight();
        int y2 = (end - start) * elemHeight();
        //FontRenderContext frc = g.getFontRenderContext();
        if (y == 0) {
            g.setColor(color);
            g.fillRect(x + 1, y + 1, stripeWidth - 2, y2 - 2);
            g.setColor(Color.black);
            if (this.drawBorders) {
                g.drawRect(x, y, stripeWidth - 1, y2 - 1);
            }
        } else {
            g.setColor(color);
            g.fillRect(x + 1, y - 1, stripeWidth - 2, y2);
            g.setColor(Color.black);
            if (this.drawBorders) {
                g.drawRect(x, y - 1, stripeWidth - 1, y2);
            }
        }
    }

    private BiMap<String, String> getMatching(Clustering<? extends Cluster> ref) {
        BiMap<String, String> matching = ref.getLookup().lookup(BiMap.class);
        if (matching == null) {
            Table<String, String, Integer> table = CountingPairs.contingencyTable(ref);
            System.out.println("table: " + table);
            matching = CountingPairs.findMatching(table);
            ref.lookupAdd(matching);
        }
        return matching;
    }

}
