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
package org.clueminer.clustering.benchmark.nsga;

import com.google.common.base.Supplier;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import com.google.common.collect.Tables;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import org.clueminer.clustering.api.AgglParams;
import org.clueminer.clustering.api.ClusterEvaluation;
import static org.clueminer.clustering.benchmark.Bench.ensureFolder;
import static org.clueminer.clustering.benchmark.Bench.safeName;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.benchmark.ConsoleDump;
import org.clueminer.dataset.benchmark.GnuplotWriter;
import org.clueminer.dataset.benchmark.ResultsCollector;
import org.clueminer.evolution.mo.MoEvolution;
import org.openide.util.Exceptions;

/**
 *
 * @author Tomas Barton
 */
public class NsgaExp implements Runnable {

    private static ResultsCollector rc;
    private NsgaParams params;
    private String benchmarkFolder;
    private ClusterEvaluation[] scores;
    private HashMap<String, Map.Entry<Dataset<? extends Instance>, Integer>> datasets;
    //table for keeping results from experiments
    private final Table<String, String, Double> table;

    public NsgaExp(NsgaParams params, String benchmarkFolder, ClusterEvaluation[] scores, HashMap<String, Map.Entry<Dataset<? extends Instance>, Integer>> availableDatasets) {
        this.params = params;
        this.benchmarkFolder = benchmarkFolder;
        this.scores = scores;
        this.datasets = availableDatasets;

        table = Tables.newCustomTable(
                Maps.<String, Map<String, Double>>newHashMap(),
                new Supplier<Map<String, Double>>() {
                    @Override
                    public Map<String, Double> get() {
                        return Maps.newHashMap();
                    }
                });
        rc = new ResultsCollector(table);
    }

    @Override
    public void run() {
        try {

            ClusterEvaluation c1, c2;
            for (int i = 0; i < scores.length; i++) {
                c1 = scores[i];
                for (int j = 1; j < scores.length; j++) {
                    if (i != j) {
                        c2 = scores[j];
                        runExperiment(c1, c2);
                    }
                }
            }

        } catch (Exception e) {
            Exceptions.printStackTrace(e);
        }
    }

    private void runExperiment(ClusterEvaluation c1, ClusterEvaluation c2) {
        MoEvolution evolution;
        String name;
        System.out.println("datasets size: " + datasets.size());
        for (Map.Entry<String, Map.Entry<Dataset<? extends Instance>, Integer>> e : datasets.entrySet()) {
            Dataset<? extends Instance> d = e.getValue().getKey();
            name = safeName(d.getName());
            String csvRes = benchmarkFolder + File.separatorChar + name + File.separatorChar + name + ".csv";
            System.out.println("=== dataset " + name);
            System.out.println("size: " + d.size());
            ensureFolder(benchmarkFolder + File.separatorChar + name);

            evolution = new MoEvolution();
            evolution.addObjective(c1);
            evolution.addObjective(c2);
            evolution.setDataset(d);
            evolution.setGenerations(params.generations);
            evolution.setPopulationSize(params.population);
            GnuplotWriter gw = new GnuplotWriter(evolution, benchmarkFolder, name + File.separatorChar + safeName(c1.getName()) + "-" + safeName(c2.getName()));
            gw.setPlotDumpMod(50);
            gw.setCustomTitle("cutoff=" + evolution.getDefaultParam(AgglParams.CUTOFF_STRATEGY) + "(" + evolution.getDefaultParam(AgglParams.CUTOFF_SCORE) + ")");
            //collect data from evolution
            evolution.addEvolutionListener(new ConsoleDump());
            evolution.addEvolutionListener(gw);
            evolution.addEvolutionListener(rc);
            evolution.run();
            System.out.println("## updating results in: " + csvRes);
            rc.writeToCsv(csvRes);

        }
    }
}
