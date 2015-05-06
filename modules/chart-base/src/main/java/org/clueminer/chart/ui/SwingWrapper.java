/*
 * Copyright (C) 2011-2015 clueminer.org
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
package org.clueminer.chart.ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JPanel;
import org.clueminer.chart.api.Drawable;

/**
 * A convenience class used to display a ChartRenderer in a Swing application
 *
 * @author timmolter
 * @author deric
 */
public class SwingWrapper {

    private String windowTitle = "ClmChart";

    private List<Drawable> charts = new LinkedList<>();
    private int numRows;
    private int numColumns;

    /**
     * Constructor
     *
     * @param chart
     */
    public SwingWrapper(Drawable chart) {
        this.charts.add(chart);
    }

    /**
     * Constructor - The number of rows and columns will be calculated
     * automatically Constructor
     *
     * @param charts
     */
    public SwingWrapper(List<Drawable> charts) {

        this.charts = charts;

        this.numRows = (int) (Math.sqrt(charts.size()) + .5);
        this.numColumns = (int) ((double) charts.size() / this.numRows + 1);
    }

    /**
     * Constructor
     *
     * @param charts
     * @param numRows    - the number of rows
     * @param numColumns - the number of columns
     */
    public SwingWrapper(List<Drawable> charts, int numRows, int numColumns) {
        this.charts = charts;
        this.numRows = numRows;
        this.numColumns = numColumns;
    }

    /**
     * Display the chart in a Swing JFrame
     *
     * @param windowTitle the title of the window
     */
    public JFrame displayChart(String windowTitle) {

        this.windowTitle = windowTitle;

        return displayChart();
    }

    /**
     * Display the chart in a Swing JFrame
     */
    public JFrame displayChart() {

        // Create and set up the window.
        final JFrame frame = new JFrame(windowTitle);

        // Schedule a job for the event-dispatching thread:
        // creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {

                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                JPanel chartPanel = new ClmChartPanel(charts.get(0));
                frame.setLayout(new GridBagLayout());

                GridBagConstraints c = new GridBagConstraints();
                c.anchor = GridBagConstraints.NORTHWEST;
                c.fill = GridBagConstraints.BOTH;
                c.gridx = 0;
                c.gridy = 0;
                c.gridwidth = 1;
                c.gridheight = 1;
                c.weightx = 1.0;
                c.weighty = 1.0;
                c.insets = new Insets(0, 0, 0, 0);
                frame.add(chartPanel, c);

                // Display the window.
                frame.pack();
                frame.setVisible(true);
            }
        });

        return frame;
    }

    /**
     * Display the charts in a Swing JFrame
     *
     * @param windowTitle the title of the window
     * @return the JFrame
     */
    public JFrame displayChartMatrix(String windowTitle) {

        this.windowTitle = windowTitle;

        return displayChartMatrix();
    }

    /**
     * Display the chart in a Swing JFrame
     */
    public JFrame displayChartMatrix() {

        // Create and set up the window.
        final JFrame frame = new JFrame(windowTitle);

        // Schedule a job for the event-dispatching thread:
        // creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {

                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.getContentPane().setLayout(new GridLayout(numRows, numColumns));

                for (Drawable chart : charts) {
                    if (chart != null) {
                        JPanel chartPanel = new ClmChartPanel(chart);
                        frame.add(chartPanel);
                    } else {
                        JPanel chartPanel = new JPanel();
                        frame.getContentPane().add(chartPanel);
                    }

                }

                // Display the window.
                frame.pack();
                frame.setVisible(true);
            }
        });

        return frame;
    }

}
