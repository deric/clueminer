package org.clueminer.dataset.benchmark;

import au.com.bytecode.opencsv.CSVWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedList;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.ClusterEvaluation;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.evolution.Evolution;
import org.clueminer.dataset.api.Attribute;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.clustering.api.evolution.EvolutionListener;
import org.clueminer.clustering.api.evolution.Individual;
import org.clueminer.clustering.api.evolution.Pair;
import org.clueminer.stats.AttrNumStats;
import org.clueminer.utils.DatasetWriter;
import org.openide.util.Exceptions;

/**
 *
 * @author Tomas Barton
 */
public class GnuplotWriter implements EvolutionListener {

    private Evolution evolution;
    private Dataset<Instance> dataset;
    private String benchmarkFolder;
    private String outputDir;
    private String dataDir;
    private LinkedList<String> results = new LinkedList<String>();
    private static String gnuplotExtension = ".gpt";
    //each 10 generations plot data
    private int plotDumpMod = 10;
    private ArrayList<String> plots = new ArrayList<String>(10);
    private char separator = ',';

    public GnuplotWriter(Evolution evolution, String benchmarkDir, String subDirectory) {
        this.evolution = evolution;
        this.dataset = evolution.getDataset();
        this.outputDir = subDirectory;
        benchmarkFolder = benchmarkDir;
        dataDir = getDataDir(subDirectory);
        mkdir(dataDir);
    }

    private void mkdir(String folder) {
        File file = new File(folder);
        if (!file.exists()) {
            if (!file.mkdirs()) {
                throw new RuntimeException("Failed to create " + folder + " !");
            }
        }
    }

    @Override
    public void bestInGeneration(int generationNum, Individual best, double avgFitness, double external) {
        //plotIndividual(generationNum, 1, 2, getDataDir(outputDir), best.getClustering());
        StringBuilder sb = new StringBuilder();
        sb.append(String.valueOf(generationNum)).append(separator);
        sb.append(String.valueOf(best.getFitness())).append(separator);
        sb.append(avgFitness).append(separator);
        sb.append(external);
        results.add(sb.toString());

        if (generationNum % plotDumpMod == 0) {
            String dataFile = writeData(generationNum, dataDir, best.getClustering());
            plots.add(plotIndividual(generationNum, 1, 2, dataDir, dataFile, best, external));
            //plots.add(plotIndividual(generationNum, 3, 4, getDataDir(outputDir), dataFile, best, external));
        }
    }

    @Override
    public void finalResult(Evolution evol, int g, Individual best, Pair<Long, Long> time,
            Pair<Double, Double> bestFitness, Pair<Double, Double> avgFitness, double external) {
        plotFitness(getDataDir(outputDir), results, evolution.getEvaluator());

        try {
            bashPlotScript(plots.toArray(new String[plots.size()]), dataDir, "set term pdf font 'Times-New-Roman,8'", "pdf");
            bashPlotScript(plots.toArray(new String[plots.size()]), dataDir, "set terminal pngcairo size 800,600 enhanced font 'Verdana,10'", "png");

        } catch (FileNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        } catch (UnsupportedEncodingException ex) {
            Exceptions.printStackTrace(ex);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    private String writeData(int n, String dataDir, Clustering<Cluster> clusters) {
        PrintWriter writer = null;
        String strn = String.format("%02d", n);
        String dataFile = "data-" + strn + ".csv";
        try {

            writer = new PrintWriter(dataDir + File.separatorChar + dataFile, "UTF-8");
            CSVWriter csv = new CSVWriter(writer, ',');
            toCsv(csv, clusters, dataset);
            writer.close();

        } catch (FileNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        } catch (UnsupportedEncodingException ex) {
            Exceptions.printStackTrace(ex);
        } finally {
            writer.close();
        }
        return dataFile;
    }

    private String plotIndividual(int n, int x, int y, String dataDir, String dataFile, Individual best, double external) {
        PrintWriter template = null;
        String strn = String.format("%02d", n);
        //filename without extension
        String scriptFile = "plot-" + strn + String.format("-x%02d", x) + String.format("-y%02d", y);

        try {
            template = new PrintWriter(dataDir + scriptFile + gnuplotExtension, "UTF-8");
        } catch (FileNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        } catch (UnsupportedEncodingException ex) {
            Exceptions.printStackTrace(ex);
        }
        template.write(plotTemplate(n, x, y, best, dataFile, external));
        template.close();
        return scriptFile;
    }

    private void plotFitness(String dataDir, LinkedList<String> table, ClusterEvaluation validator) {
        PrintWriter template = null;
        PrintWriter template2 = null;

        String dataFile = "data-fitness.csv";
        String scriptFile = "fitness-" + safeName(validator.getName());
        String scriptExtern = "external-" + safeName(evolution.getExternal().getName());

        try {
            PrintWriter writer = new PrintWriter(dataDir + File.separatorChar + dataFile, "UTF-8");
            CSVWriter csv = new CSVWriter(writer, ',');
            String[] header = new String[4];
            header[0] = "generation";
            header[1] = "best";
            header[2] = "avg";
            header[3] = "external";
            csv.writeNext(header);
            for (String row : table) {
                csv.writeLine(row);

            }
            writer.close();

            template = new PrintWriter(dataDir + scriptFile + gnuplotExtension, "UTF-8");
            template.write(gnuplotFitness(dataFile, validator, evolution.getExternal()));
            plots.add(scriptFile);

            template2 = new PrintWriter(dataDir + scriptExtern + gnuplotExtension, "UTF-8");
            template2.write(gnuplotExternal(dataFile, evolution.getExternal()));
            plots.add(scriptExtern);

        } catch (FileNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        } catch (UnsupportedEncodingException ex) {
            Exceptions.printStackTrace(ex);
        } finally {
            if (template != null) {
                template.close();
            }
            if (template2 != null) {
                template2.close();
            }
        }

    }

    private String safeName(String name) {
        return name.toLowerCase().replace(" ", "_");
    }

    private String gnuplotFitness(String dataFile, ClusterEvaluation validator, ClusterEvaluation external) {
        String res = "set title 'Fitness = " + validator.getName() + "'\n"
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
        String res = "set title '" + validator.getName() + "'\n"
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

    private String plotTemplate(int k, int x, int y, Individual best, String dataFile, double external) {
        Clustering<Cluster> clustering = best.getClustering();
        Cluster<Instance> first = clustering.get(0);
        double fitness = best.getFitness();
        int attrCnt = first.attributeCount();
        int labelPos = attrCnt + 1;
        //attributes are numbered from zero, gnuplot columns from 1
        double max = first.getAttribute(x - 1).statistics(AttrNumStats.MAX);
        double min = first.getAttribute(x - 1).statistics(AttrNumStats.MIN);
        String xrange = "[" + min + ":" + max + "]";
        max = first.getAttribute(y - 1).statistics(AttrNumStats.MAX);
        min = first.getAttribute(y - 1).statistics(AttrNumStats.MIN);
        String yrange = "[" + min + ":" + max + "]";

        String res = "set datafile separator \",\"\n"
                + "set key outside bottom horizontal box\n"
                + "set title \"generation = " + k + ", fitness = " + fitness + ", jacc = " + external + "\"\n"
                + "set xlabel \"" + first.getAttribute(x - 1).getName() + "\" font \"Times,7\"\n"
                + "set ylabel \"" + first.getAttribute(y - 1).getName() + "\" font \"Times,7\"\n"
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
            res += "\"< awk -F\\\",\\\" '{if($" + labelPos + " == \\\"" + clust.getName() + "\\\") print}' " + dataFile + "\" u " + x + ":" + y + " t \"" + clust.getName() + "\" w p pt " + pti.next();
            if (i != last) {
                res += ", \\\n";
            } else {
                res += "\n";
            }

            i++;
        }
        return res;
    }

    private String getDataDir(String dir) {
        return createFolder(dir) + "data" + File.separatorChar;
    }

    private String createFolder(String name) {
        String dir = benchmarkFolder + File.separatorChar + name + File.separatorChar;
        boolean success = (new File(dir)).mkdirs();
        if (success) {
            System.out.println("Directory: " + dir + " created");
        }
        return dir;
    }

    public void toCsv(DatasetWriter writer, Clustering<Cluster> clusters, Dataset<Instance> dataset) {
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

    private void bashPlotScript(String[] plots, String dir, String term, String ext) throws FileNotFoundException, UnsupportedEncodingException, IOException {
        //bash script to generate results
        String shFile = dir + File.separatorChar + "plot-" + ext;
        PrintWriter template = new PrintWriter(shFile, "UTF-8");
        template.write("#!/bin/bash\n"
                + "cd data\n");
        template.write("TERM=\"" + term + "\"\n");
        for (int j = 0; j < plots.length; j++) {
            template.write("gnuplot -e \"${TERM}\" " + plots[j] + gnuplotExtension + " > ../" + plots[j] + "." + ext + "\n");
        }

        template.close();
        Runtime.getRuntime().exec("chmod u+x " + shFile);
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
}
