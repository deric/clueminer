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
package org.clueminer.visualize;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import org.clueminer.clustering.algorithm.KMeans;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.dataset.api.Attribute;
import org.clueminer.dataset.api.ContinuousInstance;
import org.clueminer.dataset.api.Timeseries;
import org.clueminer.dataset.impl.TimeseriesDataset;
import org.clueminer.fixtures.TimeseriesFixture;
import org.clueminer.io.csv.CsvLoader;
import org.clueminer.utils.Props;
import org.openide.util.Exceptions;

/**
 *
 * @author deric
 */
public class VisualizePanelTest2 {

    private final VisualizePanel subject;
    private static final TimeseriesFixture TF = new TimeseriesFixture();
    private static Timeseries<ContinuousInstance> data01;

    public VisualizePanelTest2() throws IOException {
        subject = new VisualizePanel();
        data01 = loadData01();
        KMeans km = new KMeans();
        Props params = new Props();
        params.put("k", 3);
        for (Attribute attr : data01.getAttributes().values()) {
            System.out.println(attr + ": ");
        }

        Clustering c = km.cluster(data01, params);
        subject.setClustering(c);
    }

    public static Timeseries<ContinuousInstance> loadData01() throws IOException {
        if (data01 == null) {
            File f = TF.ts01();
            data01 = new TimeseriesDataset<>(105);
            CsvLoader loader = new CsvLoader();
            loader.setHasHeader(true);
            loader.setDefaultDataType("TIME");
            ArrayList<Integer> meta = new ArrayList<>(7);
            for (int i = 0; i < 7; i++) {
                meta.add(i);
            }
            loader.setSkipIndex(meta);
            loader.load(f, data01);
        }
        return data01;
    }

    protected JFrame showInFrame() {
        JFrame frame = new JFrame("selection test");
        frame.getContentPane().add(subject, BorderLayout.CENTER);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(new Dimension(800, 600));
        //frame.setSize(getPreferredSize());
        frame.setVisible(true);
        return frame;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                try {
                    new VisualizePanelTest2().showInFrame();
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        });
    }

}
