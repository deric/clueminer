/*
 * Copyright (C) 2011-2016 clueminer.org
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
package org.clueminer.scatter;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.clueminer.attributes.BasicAttrType;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.plugin.ArrayDataset;

/**
 *
 * @author deric
 */
public class Plot2DTest extends JPanel {

    private Plot2D plot;
    private static Dataset<Instance> kumar;

    public Plot2DTest() {
        setPreferredSize(new Dimension(800, 600));
        initComponents();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                new Plot2DTest().showInFrame();
            }
        });
    }

    public String getTitle() {
        return "Scatter test";
    }

    protected JFrame showInFrame() {
        JFrame frame = new JFrame(getTitle());
        frame.getContentPane().add(this, BorderLayout.CENTER);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(new Dimension(500, 500));
        //frame.setSize(getPreferredSize());
        frame.setVisible(true);
        return frame;
    }

    private void initComponents() {
        setSize(500, 500);
        setLayout(new BorderLayout());
        plot = new Plot2D();
        Dataset<? extends Instance> dataset = kumarData();
        for (Instance inst : dataset) {
            plot.addInstance(inst);
        }
        add(plot);
    }

    /**
     * Testing dataset from Kumar (chapter 8, page 519)
     *
     * @return
     */
    public static Dataset<? extends Instance> kumarData() {
        if (kumar == null) {
            kumar = new ArrayDataset<>(4, 2);
            kumar.attributeBuilder().create("x", BasicAttrType.NUMERIC);
            kumar.attributeBuilder().create("y", BasicAttrType.NUMERIC);
            kumar.builder().create(new double[]{0.40, 0.53}, "1");
            kumar.builder().create(new double[]{0.22, 0.38}, "2");
            kumar.builder().create(new double[]{0.35, 0.32}, "3");
            kumar.builder().create(new double[]{0.26, 0.19}, "4");
            kumar.builder().create(new double[]{0.08, 0.41}, "5");
            kumar.builder().create(new double[]{0.45, 0.30}, "6");
        }
        return kumar;
    }

}
