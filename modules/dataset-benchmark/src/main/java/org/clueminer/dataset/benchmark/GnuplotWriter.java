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
package org.clueminer.dataset.benchmark;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.ClusterEvaluation;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.ScoreException;
import org.clueminer.io.csv.CSVWriter;
import org.clueminer.dataset.api.Attribute;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.evolution.api.Evolution;
import org.clueminer.evolution.api.EvolutionListener;
import org.clueminer.evolution.api.EvolutionSO;
import org.clueminer.evolution.api.Individual;
import org.clueminer.evolution.api.Pair;
import org.clueminer.evolution.api.Population;
import org.clueminer.gnuplot.GnuplotHelper;
import org.clueminer.gnuplot.PointTypeIterator;
import org.clueminer.utils.DatasetWriter;
import org.openide.util.Exceptions;

/**
 *
 * @author Tomas Barton
 */
public class GnuplotWriter extends GnuplotHelper implements EvolutionListener {

    private final Evolution evolution;
    private final Dataset<? extends Instance> dataset;
    private final String outputDir;
    private final String dataDir;
    private final LinkedList<String> results;
    //each 10 generations plot data
    private int plotDumpMod = 10;
    private boolean plotIndividuals = false;
    private final LinkedList<String> plots;
    private int top = 5;

    public GnuplotWriter(Evolution evolution, String benchmarkDir, String subDirectory) {
        this.results = new LinkedList<>();
        this.plots = new LinkedList<>();
        this.evolution = evolution;
        this.dataset = evolution.getDataset();
        this.outputDir = benchmarkDir + File.separatorChar + subDirectory;
        this.dataDir = getDataDir(outputDir);
        mkdir(dataDir);
    }

    public boolean isPlotIndividuals() {
        return plotIndividuals;
    }

    public void setPlotIndividuals(boolean plotIndividuals) {
        this.plotIndividuals = plotIndividuals;
    }

    @Override
    public void bestInGeneration(int generationNum, Population<? extends Individual> population, double external) {
        //plotIndividual(generationNum, 1, 2, getDataDir(outputDir), best.getClustering());
        Individual[] ind = population.getIndividuals();
        double sum = 0.0;
        double sumExt = 0.0;
        for (int i = 0; i < getTop(); i++) {
            sum += ind[i].getFitness();
            try {
                sumExt += evolution.getExternal().score(ind[i].getClustering());
            } catch (ScoreException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        double topNfit = sum / (double) getTop();
        double topNext = sumExt / (double) getTop();

        StringBuilder sb = new StringBuilder();
        sb.append(String.valueOf(generationNum)).append(separator);
        sb.append(String.valueOf(population.getBestFitness())).append(separator);
        sb.append(population.getAvgFitness()).append(separator);
        sb.append(external).append(separator);
        sb.append(topNfit).append(separator);
        sb.append(topNext);
        results.add(sb.toString());

        if (plotIndividuals && generationNum % plotDumpMod == 0) {
            String dataFile = writeData(generationNum, dataDir, population.getBestIndividual().getClustering());
            plots.add(plotIndividual(generationNum, 1, 2, dataDir, dataFile, population.getBestIndividual(), external));
            //plots.add(plotIndividual(generationNum, 3, 4, getDataDir(outputDir), dataFile, best, external));
        }
    }

    @Override
    public void finalResult(Evolution evol, int g, Individual best, Pair<Long, Long> time,
            Pair<Double, Double> bestFitness, Pair<Double, Double> avgFitness, double external) {

        if (evolution instanceof EvolutionSO) {
            EvolutionSO evoso = (EvolutionSO) evolution;
            plotFitness(dataDir, results, evoso.getEvaluator());
        } else {
            throw new RuntimeException("MO evolution is not supported yet");
        }

        try {
            bashPlotScript(plots.toArray(new String[plots.size()]), outputDir, "gpt", "set term pdf font 'Times-New-Roman,8'", "pdf");
            bashPlotScript(plots.toArray(new String[plots.size()]), outputDir, "gpt", "set terminal pngcairo size 800,600 enhanced font 'Verdana,10'", "png");

        } catch (FileNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        } catch (UnsupportedEncodingException ex) {
            Exceptions.printStackTrace(ex);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    private String writeData(int n, String dataDir, Clustering<Instance, Cluster<Instance>> clusters) {
        PrintWriter writer = null;
        String strn = String.format("%02d", n);
        String dataFile = "data-" + strn + ".csv";
        try {
            writer = new PrintWriter(dataDir + File.separatorChar + dataFile, "UTF-8");
            CSVWriter csv = new CSVWriter(writer, ',');
            toCsv(csv, clusters, dataset);
            writer.close();

        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            Exceptions.printStackTrace(ex);
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
        return dataFile;
    }

    private String plotIndividual(int n, int x, int y, String dataDir, String dataFile, Individual best, double external) {
        PrintWriter template;
        String strn = String.format("%02d", n);
        //filename without extension
        String scriptFile = "plot-" + strn + String.format("-x%02d", x) + String.format("-y%02d", y);
        String filename = dataDir + scriptFile + gnuplotExtension;
        try {
            template = new PrintWriter(filename, "UTF-8");
            template.write(plotTemplate(n, x, y, best, dataFile, external));
            template.close();
        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            Exceptions.printStackTrace(ex);
        }
        return scriptFile;
    }

    private void plotFitness(String dir, LinkedList<String> table, ClusterEvaluation validator) {
        String dataFile = "data-fitness.csv";
        String scriptFile = "fitness-" + safeName(validator.getName());
        String scriptTopFile = "fitness-top-" + safeName(validator.getName());
        String scriptExtern = "external-" + safeName(evolution.getExternal().getName());

        try (PrintWriter writer = new PrintWriter(dir + File.separatorChar + dataFile, "UTF-8")) {
            CSVWriter csv = new CSVWriter(writer, ',');
            String[] header = new String[6];
            header[0] = "generation";
            header[1] = "best";
            header[2] = "avg";
            header[3] = "external";
            header[4] = "top" + top + "-fit";
            header[5] = "top" + top + "-ext";
            csv.writeNext(header);
            for (String row : table) {
                csv.writeLine(row);

            }
        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            Exceptions.printStackTrace(ex);
        }

        writeGnuplot(dir, scriptFile, gnuplotFitness(dataFile, validator, evolution.getExternal()));
        plots.add(scriptFile);
        writeGnuplot(dir, scriptExtern, gnuplotExternal(dataFile, evolution.getExternal()));
        plots.add(scriptExtern);
        writeGnuplot(dir, scriptTopFile, gnuplotTop(dataFile, validator, evolution.getExternal()));
        plots.add(scriptTopFile);
    }

    private String getTitle(ClusterEvaluation validator) {
        StringBuilder sb = new StringBuilder();
        sb.append(evolution.getName()).append("[p=")
                .append(evolution.getPopulationSize())
                .append(", g=").append(evolution.getGenerations())
                .append("] fitness = ").append(validator.getName())
                .append(" ").append(customTitle);

        return sb.toString();
    }

    private String gnuplotFitness(String dataFile, ClusterEvaluation validator, ClusterEvaluation external) {
        String res = "set title '" + getTitle(validator) + "'\n"
                + "set grid \n"
                + "set size 1.0, 1.0\n"
                + "set key outside bottom horizontal box\n"
                + "set datafile separator \",\"\n"
                + "set datafile missing \"NaN\"\n"
                + "set ylabel '" + validator.getName() + "'\n"
                + "set xlabel 'generation'\n"
                + "set y2label \"" + external.getName() + "\"\n"
                + "set y2tics\n"
                + "set y2range [0:1]\n" //@TODO this might differ for other external measures
                + "plot '" + dataFile + "' u 1:2 title 'best' with linespoints linewidth 2 pointtype 7 pointsize 0.3,\\\n"
                + "'' u 1:5 title 'top" + top + " avg' with linespoints linewidth 2 pointtype 9 pointsize 0.3,\\\n"
                + "'' u 1:6 title 'top" + top + "external (" + external.getName() + ")' axes x1y2 with linespoints lt 1 lw 3 pt 3 pointsize 0.3 linecolor rgbcolor \"blue\"";
        return res;
    }

    /**
     * Plots average of top individuals in a generation
     *
     * @param dataFile
     * @param validator
     * @param external
     * @return
     */
    private String gnuplotTop(String dataFile, ClusterEvaluation validator, ClusterEvaluation external) {
        String res = "set title '" + getTitle(validator) + "'\n"
                + "set grid \n"
                + "set size 1.0, 1.0\n"
                + "set key outside bottom horizontal box\n"
                + "set datafile separator \",\"\n"
                + "set datafile missing \"NaN\"\n"
                + "set ylabel '" + validator.getName() + "'\n"
                + "set xlabel 'generation'\n"
                + "set y2label \"" + external.getName() + "\"\n"
                + "set y2tics\n"
                + "set y2range [0:1]\n" //@TODO this might differ for other external measures
                + "plot '" + dataFile + "' u 1:2 title 'best' with linespoints linewidth 2 pointtype 7 pointsize 0.3,\\\n"
                + "'' u 1:3 title 'avg' with linespoints linewidth 2 pointtype 9 pointsize 0.3,\\\n"
                + "'' u 1:4 title 'external (" + external.getName() + ")' axes x1y2 with linespoints lt 1 lw 3 pt 3 pointsize 0.3 linecolor rgbcolor \"blue\"";
        return res;
    }

    private String gnuplotExternal(String dataFile, ClusterEvaluation validator) {
        String res = "set title '" + getTitle(validator) + "'\n"
                + "set grid \n"
                + "set size 1.0, 1.0\n"
                + "set key outside bottom horizontal box\n"
                + "set datafile separator \",\"\n"
                + "set datafile missing \"NaN\"\n"
                + "set ylabel '" + validator.getName() + "'\n"
                + "set xlabel 'generation'\n"
                + "plot '" + dataFile + "' u 1:4 title 'external' with linespoints linewidth 2 pointtype 7 pointsize 0.3";

        return res;
    }

    private int attrCount() {
        return dataset.attributeCount();
    }

    private String plotTemplate(int k, int x, int y, Individual best, String dataFile, double external) {
        Clustering<Instance, Cluster<Instance>> clustering = best.getClustering();
        double fitness = best.getFitness();
        int attrCnt = attrCount();
        int clusterLabelPos = attrCnt + 1;
        //attributes are numbered from zero, gnuplot columns from 1
        /* double max = dataset.getAttribute(x - 1).statistics(AttrNumStats.MAX);
         * double min = dataset.getAttribute(x - 1).statistics(AttrNumStats.MIN);
         * String xrange = "[" + min + ":" + max + "]";
         * max = dataset.getAttribute(y - 1).statistics(AttrNumStats.MAX);
         * min = dataset.getAttribute(y - 1).statistics(AttrNumStats.MIN);
         * String yrange = "[" + min + ":" + max + "]"; */

        String res = "set datafile separator \",\"\n"
                + "set key outside bottom horizontal box\n"
                + "set title \"generation = " + k + ", fitness = " + fitness + ", jacc = " + external + "\"\n"
                + "set xlabel \"" + dataset.getAttribute(x - 1).getName() + "\" font \"Times,7\"\n"
                + "set ylabel \"" + dataset.getAttribute(y - 1).getName() + "\" font \"Times,7\"\n"
                //   + "set xtics 0,0.5 nomirror\n"
                //   + "set ytics 0,0.5 nomirror\n"
                + "set mytics 2\n"
                + "set mx2tics 2\n"
                //  + "set xrange " + xrange + "\n"
                //  + "set yrange " + yrange + "\n"
                + "set grid\n"
                + "set pointsize 0.5\n";
        int i = 0;
        int last = clustering.size() - 1;
        PointTypeIterator pti = new PointTypeIterator();
        for (Cluster clust : clustering) {
            if (i == 0) {
                res += "plot ";
            }
            res += "\"< awk -F\\\",\\\" '{if($" + clusterLabelPos + " == \\\"" + clust.getName() + "\\\") print}' " + dataFile + "\" u " + x + ":" + y + " t \"" + clust.getName() + "\" w p pt " + pti.next();
            if (i != last) {
                res += ", \\\n";
            } else {
                res += "\n";
            }

            i++;
        }
        return res;
    }

    public void toCsv(DatasetWriter writer, Clustering<Instance, Cluster<Instance>> clusters, Dataset<? extends Instance> dataset) {
        String[] header = new String[dataset.attributeCount() + 2];
        header[dataset.attributeCount()] = "label";
        header[dataset.attributeCount() + 1] = "class";
        int i = 0;
        for (Attribute ta : dataset.getAttributes().values()) {
            header[i++] = String.valueOf(ta.getName());
        }
        writer.writeNext(header);
        for (Cluster<Instance> clust : clusters) {
            for (Instance inst : clust) {
                writer.writeLine(appendClass(inst, clust.getName()));
            }
        }
    }

    private StringBuilder appendClass(Instance inst, String klass) {
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < inst.size(); i++) {
            if (i > 0) {
                res.append(separator);
            }
            res.append(inst.value(i));
        }
        return res.append(separator).append(klass).append(separator).append(inst.classValue());
    }

    public int getPlotDumpMod() {
        return plotDumpMod;
    }

    /**
     * Sets modulo for generation number to dump best individual to chart
     *
     * @param plotDumpMod
     */
    public void setPlotDumpMod(int plotDumpMod) {
        this.plotDumpMod = plotDumpMod;
    }

    public int getTop() {
        return top;
    }

    /**
     * How many individuals are taken as best representatives
     *
     * @param n
     */
    public void setTop(int n) {
        this.top = n;
    }

    @Override
    public void started(Evolution evolution) {
    }

    @Override
    public void resultUpdate(Individual[] result) {
        //not much to do
    }

}
