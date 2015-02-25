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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;
import org.clueminer.clustering.api.ClusterEvaluation;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.evolution.api.Evolution;
import org.clueminer.evolution.api.EvolutionMO;
import org.clueminer.oo.api.OpListener;
import org.clueminer.oo.api.OpSolution;
import org.clueminer.utils.DatasetWriter;
import org.openide.util.Exceptions;

/**
 *
 * @author deric
 */
public class GnuplotMO extends GnuplotHelper implements OpListener {

    private EvolutionMO evolution;

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

    @Override
    public void finalResult(List<OpSolution> result) {
        String expName = createName(evolution);
        String dataFile = writeData(expName, getCurrentDir(), result);
    }

    private String writeData(String ident, String dataDir, List<OpSolution> result) {
        PrintWriter writer = null;
        String dataFile = "data-" + ident + ".csv";
        try {
            writer = new PrintWriter(dataDir + File.separatorChar + dataFile, "UTF-8");
            CSVWriter csv = new CSVWriter(writer, ',');
            toCsv(csv, result);
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

    public void toCsv(DatasetWriter writer, List<OpSolution> result) {
        int offset = 3;
        String[] header = new String[evolution.getNumObjectives() + offset];
        header[0] = "k";
        header[1] = "fingerprint";
        header[2] = evolution.getExternal().getName();
        List<ClusterEvaluation> objectives = evolution.getObjectives();
        for (int i = 0; i < evolution.getNumObjectives(); i++) {
            header[i + offset] = objectives.get(i).getName();

        }
        writer.writeNext(header);
        Clustering clust;
        String[] line = new String[header.length];
        for (OpSolution solution : result) {
            clust = solution.getClustering();
            line[0] = String.valueOf(clust.size());
            line[1] = clust.fingerprint();
            line[2] = String.valueOf(clust.getEvaluationTable().getScore(evolution.getExternal()));
            for (int i = 0; i < objectives.size(); i++) {
                line[i + offset] = String.valueOf(solution.getObjective(i));
            }

            writer.writeNext(line);
        }
    }


}
