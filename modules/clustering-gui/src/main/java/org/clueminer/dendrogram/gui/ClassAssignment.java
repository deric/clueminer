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
package org.clueminer.dendrogram.gui;

import com.google.common.collect.Table;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.util.logging.Logger;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.clustering.api.dendrogram.DendroPane;
import org.clueminer.colors.ColorBrewer;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.eval.utils.CountingPairs;
import org.clueminer.eval.utils.Matching;

/**
 *
 * @author Tomas Barton
 * @param <E>
 * @param <C>
 */
public class ClassAssignment<E extends Instance, C extends Cluster<E>> extends ClusterAssignment<E, C> {

    private static final Logger logger = Logger.getLogger(ClassAssignment.class.getName());
    private static final long serialVersionUID = 5843490511014364712L;
    private ColorBrewer colorGenerator = new ColorBrewer();

    public ClassAssignment(DendroPane panel) {
        super(panel);
        insets.left = 0;
        insets.right = 0;
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
            //TODO: still class size == 0 does not mean that there's no class information
            /*if (dataset.getClasses().size() == 0) {
             //logger.log(Level.WARNING, "no class information in data");
             return;
             }*/
            Matching matching = getMatching(flatClust);
            Object2ObjectMap<Object, Color> map = new Object2ObjectOpenHashMap(i);

            g.setFont(font);
            FontMetrics fm = g.getFontMetrics();
            lineHeight = fm.getHeight();
            //int currCluster = clusters[i];
            int mapped = hieraRes.getMappedIndex(i);
            Object currClass = dataset.get(mapped).classValue();
            //create first class -> color mapping
            colorForClass(map, currClass, matching);
            int start = 0;
            int x = insets.left;

            while (i < dataset.size()) {
                mapped = hieraRes.getMappedIndex(i);
                Object klass = dataset.get(mapped).classValue();
                if (klass != null && !klass.equals(currClass)) {
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

    private Color colorForClass(Object2ObjectMap<Object, Color> map, Object klass, Matching matching) {
        if (!map.containsKey(klass)) {
            if (klass == null) {
                return Color.GRAY;
            }
            Color color;
            String cls = klass.toString();
            if (matching.containsKey(cls)) {
                String cluster = matching.get(cls);
                Cluster c = flatClust.get(cluster);
                if (c != null) {
                    color = c.getColor();
                    //that color is already mapped to different class
                    if (map.containsValue(color)) {
                        colorGenerator.seek(map.size());
                        color = colorGenerator.next();
                    }
                    map.put(klass, color);

                } else {
                    //FIXME: right now we suppose that the generator is the same as for clusters
                    colorGenerator.seek(map.size() - 1);
                    color = colorGenerator.next();
                    map.put(klass, color);
                }
            } else {
                colorGenerator.seek(matching.size() + map.size());
                color = colorGenerator.next();
                map.put(klass, color);
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

    private Matching getMatching(Clustering<E, Cluster<E>> ref) {
        Matching matching = ref.getLookup().lookup(Matching.class);
        if (matching == null) {
            Table<String, String, Integer> table = CountingPairs.contingencyTable(ref);
            matching = CountingPairs.findMatching(table);
            ref.lookupAdd(matching);
        }
        return matching;
    }

}
