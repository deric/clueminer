package org.clueminer.evolution;

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
import org.clueminer.dataset.api.Attribute;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.stats.AttrNumStats;
import org.clueminer.utils.DatasetWriter;
import org.clueminer.utils.FileUtils;
import org.clueminer.utils.PointTypeIterator;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

/**
 *
 * @author Tomas Barton
 */
public class GnuplotWriter implements EvolutionListener {

    private Evolution evolution;
    private Dataset<Instance> dataset;
    private String benchmarkFolder;
    private ClusterEvaluation externalValidation;
    private String outputDir;
    private LinkedList<String> results = new LinkedList<String>();
    private static String gnuplotExtension = ".gpt";
    //each 10 generations plot data
    private int plotMod = 10;
    private ArrayList<String> plots = new ArrayList<String>(10);
    private String separator = ",";

    public GnuplotWriter(Evolution evolution, ClusterEvaluation external, String outputDir) {
        this.evolution = evolution;
        this.dataset = evolution.getDataset();
        this.externalValidation = external;
        this.outputDir = outputDir;


        String home = System.getProperty("user.home") + File.separatorChar
                + NbBundle.getMessage(
                FileUtils.class,
                "FOLDER_Home");
        benchmarkFolder = home + File.separatorChar + "benchmark";


        String dataDir = getDataDir(outputDir);
        (new File(dataDir)).mkdir();

    }

    @Override
    public void bestInGeneration(int generationNum, Individual best, double avgFitness) {
        //plotIndividual(generationNum, 1, 2, getDataDir(outputDir), best.getClustering());
        StringBuilder sb = new StringBuilder();
        sb.append(String.valueOf(generationNum)).append(separator);
        sb.append(String.valueOf(best.getFitness())).append(separator);
        sb.append(avgFitness).append(separator);
        double extern = externalValidation.score(best.getClustering(), dataset);
        sb.append(extern);
        results.add(sb.toString());

        if (generationNum % plotMod == 0) {
            String script = plotIndividual(generationNum, 1, 2, getDataDir(outputDir), best.getClustering());
            plots.add(script);
        }
    }

    @Override
    public void finalResult(Individual best, long evolutionTime) {
        plotFitness(getDataDir(outputDir), results, evolution.getEvaluator());

        try {
            bashPlotScript(plots.toArray(new String[plots.size()]), createFolder(outputDir), "set term pdf font 'Times-New-Roman,8'", "pdf");
            bashPlotScript(plots.toArray(new String[plots.size()]), createFolder(outputDir), "set terminal pngcairo size 800,600 enhanced font 'Verdana,10'", "png");

        } catch (FileNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        } catch (UnsupportedEncodingException ex) {
            Exceptions.printStackTrace(ex);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    private String plotIndividual(int n, int x, int y, String dataDir, Clustering<Cluster> clusters) {
        PrintWriter template = null;
        String strn = String.format("%02d", n);
        String dataFile = "data-" + strn + ".csv";
        //filename without extension
        String scriptFile = "plot-" + strn;

        try {
            PrintWriter writer = new PrintWriter(dataDir + File.separatorChar + dataFile, "UTF-8");
            CSVWriter csv = new CSVWriter(writer, ',');
            toCsv(csv, clusters, dataset);
            writer.close();

            template = new PrintWriter(dataDir + scriptFile + gnuplotExtension, "UTF-8");
        } catch (FileNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        } catch (UnsupportedEncodingException ex) {
            Exceptions.printStackTrace(ex);
        }
        template.write(plotTemplate(n, x, y, clusters, dataFile));
        template.close();
        return scriptFile;
    }

    private void plotFitness(String dataDir, LinkedList<String> table, ClusterEvaluation validator) {
        PrintWriter template = null;
        PrintWriter template2 = null;

        String dataFile = "data-fitness.csv";
        String scriptFile = "fitness-" + safeName(validator.getName());
        String scriptExtern = "external-" + safeName(externalValidation.getName());

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
            template.write(gnuplotFitness(dataFile, validator));
            plots.add(scriptFile);

            template2 = new PrintWriter(dataDir + scriptExtern + gnuplotExtension, "UTF-8");
            template2.write(gnuplotExternal(dataFile, externalValidation));
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

    private String gnuplotFitness(String dataFile, ClusterEvaluation validator) {
        String res = "set title '" + validator.getName() + "'\n"
                + "set key off\n"
                + "set grid \n"
                + "set size 1.0, 1.0\n"
                + "set key outside bottom horizontal box\n"
                + "set datafile separator \",\"\n"
                + "set datafile missing \"NaN\"\n"
                + "set ylabel '" + validator.getName() + "'\n"
                + "set xlabel 'generation'\n"
                + "plot '" + dataFile + "' u 1:2 title 'best' with linespoints linewidth 2 pointtype 7 pointsize 0.3,\\\n"
                + " '' u 1:3 title 'avg' with linespoints linewidth 2 pointtype 9 pointsize 0.3";
        return res;
    }

    private String gnuplotExternal(String dataFile, ClusterEvaluation validator) {
        String res = "set title '" + validator.getName() + "'\n"
                + "set key off\n"
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

    private String plotTemplate(int k, int x, int y, Clustering<Cluster> clustering, String dataFile) {
        Cluster<Instance> first = clustering.get(0);
        double fitness = evolution.getEvaluator().score(clustering, dataset);
        int attrCnt = first.attributeCount();
        int labelPos = attrCnt + 1;
        //attributes are numbered from zero, gnuplot columns from 1
        double max = first.getAttribute(x - 1).statistics(AttrNumStats.MAX);
        double min = first.getAttribute(x - 1).statistics(AttrNumStats.MIN);
        String xrange = "[" + min + ":" + max + "]";
        max = first.getAttribute(y - 1).statistics(AttrNumStats.MAX);
        min = first.getAttribute(y - 1).statistics(AttrNumStats.MIN);
        String yrange = "[" + min + ":" + max + "]";

        double jacc = externalValidation.score(clustering, dataset);

        String res = "set datafile separator \",\"\n"
                + "set key outside bottom horizontal box\n"
                + "set title \"generation = " + k + ", fitness = " + fitness + ", jacc = " + jacc + "\"\n"
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
        boolean success = (new File(dir)).mkdir();
        if (success) {
            System.out.println("Directory: " + dir + " created");
        }
        return dir;
    }

    public void toCsv(DatasetWriter writer, Clustering<Cluster> clusters, Dataset<Instance> dataset) {
        String[] header = new String[dataset.attributeCount() + 1];
        header[dataset.attributeCount()] = "label";
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
                res.append(',');
            }
            res.append(inst.value(i));
        }
        return res.append(',').append(klass);
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
}
