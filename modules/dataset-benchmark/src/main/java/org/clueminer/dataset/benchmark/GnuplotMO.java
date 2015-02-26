/*
 * Copyright (C) 2015 clueminer.org
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

import au.com.bytecode.opencsv.CSVWriter;
import com.google.common.collect.Sets;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.clueminer.clustering.api.ClusterEvaluation;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.ExternalEvaluator;
import org.clueminer.clustering.api.factory.ExternalEvaluatorFactory;
import org.clueminer.evolution.api.Evolution;
import org.clueminer.evolution.api.EvolutionMO;
import org.clueminer.oo.api.OpListener;
import org.clueminer.oo.api.OpSolution;
import org.clueminer.utils.DatasetWriter;
import org.openide.util.Exceptions;

/**
 * Class for generating Gnuplot scripts. It collects results from multiple
 * evolution runs.
 *
 * @author deric
 */
public class GnuplotMO extends GnuplotHelper implements OpListener {

    private EvolutionMO evolution;
    private LinkedList<String> plots;

    public GnuplotMO() {
        plots = new LinkedList<>();
    }

    @Override
    public void started(Evolution evolution) {
        this.evolution = (EvolutionMO) evolution;
    }

    private String createName(EvolutionMO evo) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < evo.getNumObjectives(); i++) {
            if (i > 0) {
                sb.append("-");
            }
            sb.append(((ClusterEvaluation) evo.getObjectives().get(i)).getName());
        }
        return safeName(sb.toString());
    }

    private String gptDir() {
        return getCurrentDir() + File.separatorChar + "gpt";
    }

    private String dataDir() {
        return getCurrentDir() + File.separatorChar + "data";
    }

    /**
     * Result from single evolution run
     *
     * @param result
     */
    @Override
    public void finalResult(List<OpSolution> result) {
        String expName = createName(evolution);
        mkdir(dataDir());
        mkdir(gptDir());
        String dataFile = writeData(expName, dataDir(), result);

        writeGnuplot(gptDir(), expName, gnuplotParetoFront(dataFile, evolution.getObjective(0), evolution.getObjective(1)));
        plots.add(expName);

        writeBashScripts();
    }

    public void writeBashScripts() {

        try {
            bashPlotScript(plots.toArray(new String[plots.size()]), getCurrentDir(), "set term pdf font 'Times-New-Roman,8'", "pdf");
            bashPlotScript(plots.toArray(new String[plots.size()]), getCurrentDir(), "set terminal pngcairo size 800,600 enhanced font 'Verdana,10'", "png");

        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            Exceptions.printStackTrace(ex);
        } catch (IOException ex) {
            System.err.println("failed to write gnuplot scripts");
            Exceptions.printStackTrace(ex);
        }
    }

    /**
     * Write to new file or append to existing if file exists
     *
     * @param ident
     * @param dataDir
     * @param result
     * @return
     */
    private String writeData(String ident, String dataDir, List<OpSolution> result) {
        PrintWriter writer = null;
        String dataFile = ident + ".csv";
        try {
            File f = new File(dataDir + File.separatorChar + dataFile);
            boolean append = f.exists();
            writer = new PrintWriter(new FileOutputStream(f), append);
            CSVWriter csv = new CSVWriter(writer, ',');
            toCsv(csv, result, true);
            writer.close();
        } catch (FileNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
        return dataFile;
    }

    /**
     *
     * @param writer
     * @param result
     * @param writeHeader
     */
    public void toCsv(DatasetWriter writer, List<OpSolution> result, boolean writeHeader) {
        int offset = 2;
        ExternalEvaluatorFactory ef = ExternalEvaluatorFactory.getInstance();
        List<ExternalEvaluator> eval = ef.getAll();
        String[] header = new String[evolution.getNumObjectives() + eval.size() + offset];
        List<ClusterEvaluation> objectives = evolution.getObjectives();
        int i;
        if (writeHeader) {
            for (i = 0; i < evolution.getNumObjectives(); i++) {
                header[i] = objectives.get(i).getName();
            }
            header[i++] = "k";
            header[i++] = "fingerprint";
            for (ExternalEvaluator e : eval) {
                header[i++] = e.getName();
            }
        }

        writer.writeNext(header);
        Clustering clust;
        Set<Clustering> blacklist = Sets.newHashSet();
        String[] line = new String[header.length];
        for (OpSolution solution : result) {
            clust = solution.getClustering();
            if (!blacklist.contains(clust)) {
                for (i = 0; i < objectives.size(); i++) {
                    line[i] = String.valueOf(solution.getObjective(i));
                }
                line[i++] = String.valueOf(clust.size());
                line[i++] = clust.fingerprint();

                for (ExternalEvaluator e : eval) {
                    line[i++] = String.valueOf(clust.getEvaluationTable().getScore(e));
                }
                writer.writeNext(line);
                blacklist.add(clust);
            }
        }
    }

    private String getTitle() {
        StringBuilder sb = new StringBuilder();
        sb.append(evolution.getDataset().getName()).append(" [");
        sb.append(evolution.getAlgorithm().getName()).append(", ");
        sb.append(" generations: ").append(evolution.getGenerations()).append(", ");
        sb.append(" population: ").append(evolution.getPopulationSize()).append(", ");
        sb.append(" crossover: ").append(evolution.getCrossoverProbability()).append(", ");
        sb.append(" mutation: ").append(evolution.getMutationProbability());
        sb.append("]");
        return sb.toString();
    }

    /**
     *
     * @param dataFile
     * @param c1
     * @param c2
     * @return
     */
    private String gnuplotParetoFront(String dataFile, ClusterEvaluation c1, ClusterEvaluation c2) {
        //this will work in case of AUC, Precision, Jaccard... but not Adjusted Rand
        String res = "set cbrange [-1:1]\n"
                + "set palette model RGB defined (-1 \"red\",0.0 \"black\", 1 \"green\")\n"
                //"set palette model RGB defined (0 \"red\",0.5 \"black\", 1 \"green\")\n"
                + "set title '" + getTitle() + "'\n"
                + "set grid \n"
                + "set size 1.0, 1.0\n"
                + "set key outside bottom horizontal box\n"
                + "set datafile separator \",\"\n"
                + "set datafile missing \"NaN\"\n"
                + "set ylabel '" + c2.getName() + "'\n"
                + "set xlabel \"" + c1.getName() + "\"\n"
                + "set y2label \"Adjusted Rand\"\n"
                + "plot '" + "data" + File.separatorChar + dataFile + "'"
                + " u 1:2:7 title 'pareto front' with points pointtype 7 pointsize 1.1 palette, \\\n"
                + " '' using ($1):($2):($3) with labels offset 2 notitle"; //plot number of clusters next to colored point

        return res;
    }

}
