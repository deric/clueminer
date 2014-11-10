package org.clueminer.export.evolution;

import au.com.bytecode.opencsv.CSVWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.prefs.Preferences;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.ClusterEvaluation;
import org.clueminer.clustering.api.ClusterEvaluator;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.EvaluationTable;
import org.clueminer.clustering.api.ExternalEvaluator;
import org.clueminer.clustering.api.evolution.Evolution;
import org.clueminer.clustering.api.factory.EvaluationFactory;
import org.clueminer.clustering.api.factory.ExternalEvaluatorFactory;
import org.clueminer.clustering.api.factory.InternalEvaluatorFactory;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.eval.utils.HashEvaluationTable;
import org.netbeans.api.progress.ProgressHandle;
import org.openide.util.Exceptions;

/**
 *
 * @author Tomas Barton
 */
public class EvolutionCsvRunner implements Runnable {

    private File file;
    private Evolution evolution;
    private ProgressHandle ph;
    private boolean includeHeader;
    private char separator;
    private boolean quoteStrings;

    public EvolutionCsvRunner() {

    }

    public EvolutionCsvRunner(File file, Evolution evolution, Preferences pref, ProgressHandle ph) {
        this.file = file;
        this.evolution = evolution;
        this.ph = ph;
        parsePref(pref);
    }

    @Override
    public void run() {
        try {
            try (CSVWriter writer = new CSVWriter(new FileWriter(file), separator)) {
                String[] line;
                Collection<? extends Clustering> result = evolution.getLookup().lookupAll(Clustering.class);
                if (result != null) {
                    //number of items in dataset must be same as number of instances in clusters
                    ph.start(result.size());

                    EvaluationTable et;
                    int cnt = 0;
                    List<ClusterEvaluation> evaluators = getEvaluators();
                    if (includeHeader) {
                        writeHeader(writer, evaluators);
                    }
                    for (Clustering c : result) {
                        line = new String[evaluators.size() + 1];
                        et = evaluationTable(c);
                        line[0] = c.getParams().toString();
                        int i = 1;
                        for (ClusterEvaluation ext : evaluators) {
                            line[i++] = String.valueOf(et.getScore(ext));
                        }
                        writer.writeNext(line, quoteStrings);
                        ph.progress(cnt++);
                    }
                } else {
                    throw new RuntimeException("no clustering result. did you run clustering?");
                }
            }
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    protected List<ClusterEvaluation> getEvaluators() {
        return EvaluationFactory.getInstance().getAll();
    }

    private void parsePref(Preferences pref) {
        includeHeader = pref.getBoolean(CsvEvolutionOptions.INCLUDE_HEADER, true);
        separator = pref.get("separator", ",").charAt(0);
        quoteStrings = pref.getBoolean("quote_strings", false);
    }

    protected EvaluationTable evaluationTable(Clustering<? extends Cluster> clustering) {
        EvaluationTable evalTable = clustering.getLookup().lookup(EvaluationTable.class);
        //we try to compute score just once, to eliminate delays
        if (evalTable == null) {
            Dataset<? extends Instance> dataset = clustering.getLookup().lookup(Dataset.class);
            if (dataset == null) {
                throw new RuntimeException("no dataset in lookup");
            }
            evalTable = new HashEvaluationTable(clustering, dataset);
            clustering.lookupAdd(evalTable);
        }
        return evalTable;
    }

    private void writeHeader(CSVWriter writer, List<ClusterEvaluation> evaluators) {
        String[] line = new String[evaluators.size() + 1];
        line[0] = "algorithm";
        for (int i = 0; i < evaluators.size(); i++) {
            line[i + 1] = evaluators.get(i).getName();

        }
        writer.writeNext(line, quoteStrings);
    }

}
