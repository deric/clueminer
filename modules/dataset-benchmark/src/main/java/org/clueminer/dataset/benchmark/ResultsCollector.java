/*
 * Copyright (C) 2011-2018 clueminer.org
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

import com.google.common.collect.Sets;
import com.google.common.collect.Table;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Set;
import org.clueminer.clustering.api.ClusterEvaluation;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.ExternalEvaluator;
import org.clueminer.clustering.api.factory.ExternalEvaluatorFactory;
import org.clueminer.evolution.api.Evolution;
import org.clueminer.evolution.api.EvolutionListener;
import org.clueminer.evolution.api.EvolutionMO;
import org.clueminer.evolution.api.EvolutionSO;
import org.clueminer.evolution.api.Individual;
import org.clueminer.evolution.api.Pair;
import org.clueminer.evolution.api.Population;
import org.clueminer.io.csv.CSVWriter;
import org.clueminer.oo.api.OpListener;
import org.clueminer.oo.api.OpSolution;
import org.openide.util.Exceptions;

/**
 *
 * @author Tomas Barton
 */
public class ResultsCollector implements EvolutionListener, OpListener {

    //reference to results table
    //we convert the table to CSV, so in the end it's going to be string
    private Table<String, String, Double> table;
    private Evolution evolution;
    private Set<Clustering> allSolutions;

    public ResultsCollector(Table<String, String, Double> table) {
        this.table = table;
        allSolutions = Sets.newHashSet();
    }

    @Override
    public void started(Evolution evolution) {
        this.evolution = evolution;
    }

    @Override
    public void bestInGeneration(int generationNum, Population<? extends Individual> population, double external) {
        //we care only about final results
    }

    @Override
    public void finalResult(Evolution evolution, int g, Individual best, Pair<Long, Long> time,
            Pair<Double, Double> bestFitness, Pair<Double, Double> avgFitness, double external) {

        if (evolution instanceof EvolutionSO) {
            EvolutionSO evoso = (EvolutionSO) evolution;
            table.put(evolution.getDataset().getName(), evoso.getEvaluator().getName(), external);
        } else {
            throw new RuntimeException("MO evolution is not supported yet");
        }

    }

    public void writeToCsv(String filename) {
        try (PrintWriter writer = new PrintWriter(filename, "UTF-8"); CSVWriter csv = new CSVWriter(writer, ',')) {

            String[] header = new String[table.columnKeySet().size() + 1];
            int i = 1;
            header[0] = "measure";
            for (String column : table.columnKeySet()) {
                header[i++] = column;
            }
            csv.writeNext(header);
            for (String key : table.rowKeySet()) {
                String[] line = new String[table.columnKeySet().size() + 1];
                line[0] = key;
                i = 1;
                for (String column : table.columnKeySet()) {
                    line[i++] = String.valueOf(table.get(key, column));
                }
                csv.writeNext(line);
            }
            csv.close();
        } catch (FileNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        } catch (UnsupportedEncodingException ex) {
            Exceptions.printStackTrace(ex);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    /**
     * Write table to CSV with column's averages
     *
     * @param tab
     * @param filename
     */
    public void writeAvgColsCsv(Table<String, String, Double> tab, String filename) {
        try (PrintWriter writer = new PrintWriter(filename, "UTF-8"); CSVWriter csv = new CSVWriter(writer, ',')) {
            String[] header = new String[tab.columnKeySet().size() + 1];
            int i = 1;
            header[0] = "measure";
            for (String column : tab.columnKeySet()) {
                header[i++] = column;
            }
            csv.writeNext(header);
            int rowCount = tab.rowKeySet().size();
            int colCount = header.length - 1;
            double[] avg = new double[colCount];
            double value;
            for (String key : tab.rowKeySet()) {
                String[] line = new String[tab.columnKeySet().size() + 1];
                line[0] = key;
                i = 1;
                for (String column : tab.columnKeySet()) {
                    value = tab.get(key, column);
                    avg[i - 1] += value;
                    line[i++] = String.valueOf(value);
                }
                csv.writeNext(line);
            }
            String[] res = new String[header.length];
            res[0] = "avg";
            for (int j = 0; j < header.length - 1; j++) {
                res[j + 1] = String.valueOf(avg[j] / rowCount);
            }
            csv.writeNext(res);
            csv.close();
        } catch (FileNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        } catch (UnsupportedEncodingException ex) {
            Exceptions.printStackTrace(ex);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    @Override
    public void resultUpdate(Individual[] result, boolean isExplicit) {
        //we are mostly interested for final set of clusterings, not incremental updates
    }

    @Override
    public void finalResult(List<OpSolution> result) {
        ExternalEvaluatorFactory ef = ExternalEvaluatorFactory.getInstance();
        List<ExternalEvaluator> eval = ef.getAll();

        Clustering clust;
        int i = 0;
        for (OpSolution sol : result) {
            clust = sol.getClustering();

            if (!allSolutions.contains(clust)) {
                String key = null;
                //store objectives
                if (evolution instanceof EvolutionMO) {
                    EvolutionMO mo = (EvolutionMO) evolution;
                    key = getKey(mo);
                }

                for (ExternalEvaluator e : eval) {
                    table.put(evolution.getDataset().getName() + " - " + key + " #" + i, e.getName(), clust.getEvaluationTable().getScore(e));
                }
                allSolutions.add(clust);
                i++;
            }
        }
    }

    private String getKey(EvolutionMO evo) {
        StringBuilder sb = new StringBuilder();
        List<ClusterEvaluation> evals = evo.getObjectives();
        for (int i = 0; i < evals.size(); i++) {
            if (i > 0) {
                sb.append(" & ");
            }
            sb.append(evals.get(i).getName());
        }
        return sb.toString();
    }

    @Override
    public void finishedBatch() {
        allSolutions = Sets.newHashSet();
    }
}
